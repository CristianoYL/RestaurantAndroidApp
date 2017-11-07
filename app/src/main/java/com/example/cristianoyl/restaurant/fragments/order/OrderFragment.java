package com.example.cristianoyl.restaurant.fragments.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Menu;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.services.FetchAddressIntentService;
import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import com.google.android.gms.maps.SupportMapFragment;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceCardData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.cristianoyl.restaurant.R.id.lv_address;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnOrderFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = "OrderFragment";

    private static final String ARG_RESTAURANT = "restaurant";
    private static final String ARG_ORDER_MAP = "orderMap";

    private Restaurant restaurant;
    private HashMap<Menu,Integer> orderMap;
    private TextView tvMessage, tvCoupon, tvAddress, tvPhone, tvPaymentMethod;
    private ListView lvAddress;
    private List<String> strAddressList;
    private Button btnPlaceOrder;

    private boolean isPaymentMethodSet, isPhoneSet, isAddressSet;

    private OnOrderFragmentInteractionListener mListener;
    private AddressResultReceiver mResultReceiver;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private CameraPosition mCameraPosition;
    private LocationRequest locationRequest;
    private LocationListener locationListener;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(40.5233403,-74.4588031);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final int LOCATION_EXPIRATION_TIME = 1000 * 60;  // 1 minute

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mCurrentLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String IS_PHONE_SET = "is_phone_set";
    private static final String IS_ADDRESS_SET = "is_address_set";
    private static final String IS_PAYMENT_METHOD_SET = "is_payment_method_set";

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orderMap a map of menu and its order sum.
     * @return A new instance of fragment OrderFragment.
     */
    public static OrderFragment newInstance(Restaurant restaurant, HashMap<Menu,Integer> orderMap) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESTAURANT,restaurant.toJson());
        args.putSerializable(ARG_ORDER_MAP, orderMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Test");
        if (getArguments() != null) {
            restaurant = new Gson().fromJson(getArguments().getString(ARG_RESTAURANT),Restaurant.class);
            orderMap = (HashMap<Menu,Integer>) getArguments().getSerializable(ARG_ORDER_MAP);
        }
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            isPhoneSet = savedInstanceState.getBoolean(IS_PHONE_SET);
            isPaymentMethodSet = savedInstanceState.getBoolean(IS_PAYMENT_METHOD_SET);
            isAddressSet = savedInstanceState.getBoolean(IS_ADDRESS_SET);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            outState.putBoolean(IS_PHONE_SET,isPhoneSet);
            outState.putBoolean(IS_PAYMENT_METHOD_SET,isPaymentMethodSet);
            outState.putBoolean(IS_ADDRESS_SET,isAddressSet);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        TextView tvRestaurantName = view.findViewById(R.id.tv_name);
        tvRestaurantName.setText(restaurant.name);

        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackButtonPressed();
            }
        });

        LinearLayout layout_orders = (LinearLayout) view.findViewById(R.id.layout_orders);
        int i = 1;  // below the "order summary" TextView
        float total = 0;
        for ( Menu menu : orderMap.keySet() ) {
            if ( orderMap.get(menu) > 0 ) {
                View v = inflater.inflate(R.layout.layout_order_item, container, false);
                TextView tv_name = (TextView) v.findViewById(R.id.tv_item_name);
                TextView tv_count = (TextView) v.findViewById(R.id.tv_item_count);
                TextView tv_sum = (TextView) v.findViewById(R.id.tv_item_sum);
                tv_name.setText(menu.name);
                tv_count.setText("x" + orderMap.get(menu));
                float itemSum = menu.price * orderMap.get(menu);
                total += itemSum;
                tv_sum.setText("$" + itemSum);
                layout_orders.addView(v, i++, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // inflate dialog view and find sub views
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_basic,null);
                TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);
                final EditText etMessage = view.findViewById(R.id.edit_text);
                // copy the previously input data
                final String cachedInput = tvMessage.getText().toString();
                if ( !cachedInput.equals(getString(R.string.label_add_message)) ) {
                    etMessage.setText(cachedInput);
                }
                // set hint and input types
                textInputLayout.setHint(getString(R.string.label_add_message));
                etMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                // everything ready, render dialog
                builder.setView(view);
                builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( etMessage.getText().toString().length() == 0 ) {
                            tvMessage.setText(getString(R.string.label_add_message));
                        } else {
                            tvMessage.setText(etMessage.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing, simply dismiss the dialog
                    }
                });
                builder.show();
            }
        });

        tvCoupon = view.findViewById(R.id.tv_coupon);
        tvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // inflate dialog view and find sub views
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_basic,null);
                TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);
                final EditText etCoupon = view.findViewById(R.id.edit_text);
                // copy the previously input data
                final String cachedInput = tvCoupon.getText().toString();
                if ( !cachedInput.equals(getString(R.string.label_use_coupon)) ) {
                    etCoupon.setText(cachedInput);
                }
                // set hint and input types
                textInputLayout.setHint(getString(R.string.label_use_coupon));
                etCoupon.setInputType(InputType.TYPE_CLASS_TEXT);
                // everything ready, render dialog
                builder.setView(view);
                builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( etCoupon.getText().toString().length() == 0 ) {
                            tvCoupon.setText(getString(R.string.label_use_coupon));
                        } else {
                            tvCoupon.setText(etCoupon.getText().toString());
                        }

                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing, simply dismiss the dialog
                    }
                });
                builder.show();
            }
        });
        // subtotal
        TextView tvSubtotal = (TextView) view.findViewById(R.id.tv_subtotal);
        tvSubtotal.setText("$" + total);
        // tax
        TextView tvTax = (TextView) view.findViewById(R.id.tv_tax);
        double tax = total * Constants.TAX_RATE;
        tvTax.setText("$" + String.format(Locale.getDefault(),"%.2f", tax));
        total += tax;
        // delivery fee
        TextView tvDelivery = (TextView) view.findViewById(R.id.tv_delivery);
        float delivery = restaurant.fee;
        tvDelivery.setText("$" + String.format(Locale.getDefault(),"%.2f", delivery));
        total += delivery;
        // tips 15% by default
        TextView tvTips = (TextView) view.findViewById(R.id.tv_tips);
        float tipRate = 0.15f;
        float tips = total * tipRate;
        tvTips.setText("$" + String.format(Locale.getDefault(),"%.2f", tips));
        total += tips;
        // total
        TextView tvTotal = (TextView) view.findViewById(R.id.tv_total);
        tvTotal.setText("$" + String.format(Locale.getDefault(),"%.2f", total));

        // delivery address
        // manual input
        tvAddress = view.findViewById(R.id.tv_address);

        // show a popup dialog window when clicking the tvAddress
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_address,null);
                final EditText etAddress = view.findViewById(R.id.et_address);
                lvAddress = view.findViewById(lv_address);
                etAddress.setText(tvAddress.getText().toString());
                // if there is location info available, show all the (5) possible suggestions in the ListView
                if ( strAddressList != null ) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, strAddressList);
                    lvAddress.setAdapter(adapter);
                    lvAddress.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            etAddress.setText(parent.getItemAtPosition(position).toString());
                        }
                    });
                }
                builder.setView(view);
                mResultReceiver = new AddressResultReceiver(new Handler());
                // add a OnTextChangedListener and modifies the suggestion list according to user input
                etAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // update lvAddress according to input address, show 5 possible addresses in the list
                        getAddressFromString(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvAddress.setText(etAddress.getText().toString());
                        mMap.clear();
                        try {
                            List<Address> addressList = new Geocoder(getContext(),Locale.getDefault())
                                    .getFromLocationName(tvAddress.getText().toString(),1);
                            if ( addressList != null && addressList.size() == 1 ) {
                                stopLocating();
                                LatLng latLng = new LatLng(
                                        addressList.get(0).getLatitude(),
                                        addressList.get(0).getLongitude());
                                mCameraPosition = CameraPosition.fromLatLngZoom(latLng,Constants.DEFAULT_ZOOM);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(tvAddress.getText().toString()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing, simply dismiss the dialog
                    }
                });
                builder.show();
            }
        });

        tvPhone = view.findViewById(R.id.tv_phone);
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // inflate dialog view and find sub views
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_basic,null);
                TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);
                final EditText etPhone = view.findViewById(R.id.edit_text);
                // copy the previously input data
                final String cachedInput = tvPhone.getText().toString();
                if ( !cachedInput.equals(getString(R.string.label_contact_phone)) ) {
                    etPhone.setText(cachedInput);
                }
                // set hint and input types
                textInputLayout.setHint(getString(R.string.label_contact_phone));
                etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
                PhoneNumberFormattingTextWatcher textWatcher = new PhoneNumberFormattingTextWatcher();
                etPhone.addTextChangedListener(textWatcher);
                // everything ready, render dialog
                builder.setView(view);
                builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tvPhone.setText(PhoneNumberUtils.formatNumber(etPhone.getText().toString(),Locale.getDefault().getCountry()));
                        } else {
                            tvPhone.setText(PhoneNumberUtils.formatNumber(etPhone.getText().toString()));
                        }
                        if ( tvPhone.getText().length() < Constants.LOCALE_PHONE_NUMBER_LENGTH ) {
                            Toast.makeText(getContext(), R.string.error_incorrect_phone_number, Toast.LENGTH_SHORT).show();
                            isPhoneSet = false;
                        } else {
                            isPhoneSet = true;
                        }

                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing, simply dismiss the dialog
                        if ( tvPhone.getText().length() < Constants.LOCALE_PHONE_NUMBER_LENGTH ) {
                            Toast.makeText(getContext(), R.string.error_incorrect_phone_number, Toast.LENGTH_SHORT).show();
                            isPhoneSet = false;
                        } else {
                            isPhoneSet = true;
                        }
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });

        tvPaymentMethod = view.findViewById(R.id.tv_card);
        tvPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChooseCard();
                }
            }
        });

        btnPlaceOrder = view.findViewById(R.id.btn_place_order);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isPhoneSet && isAddressSet && isPaymentMethodSet ) {
                    Toast.makeText(getContext(), "Place order!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.error_order_info_incomplete, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        if ( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity() /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        mGoogleApiClient.connect();
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frame_map);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrderFragmentInteractionListener) {
            mListener = (OnOrderFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnOrderFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Use a custom info window adapter to handle multiple lines of text in the
//        // info window contents.
//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            @Override
//            // Return null here, so that getInfoContents() is called next.
//            public View getInfoWindow(Marker arg0) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                // Inflate the layouts for the info window, title and snippet.
//                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents,
//                        (FrameLayout) getActivity().findViewById(R.id.frame_map), false);
//
//                TextView title = (infoWindow.findViewById(R.id.title));
//                title.setText(marker.getTitle());
//
//                TextView snippet = (infoWindow.findViewById(R.id.snippet));
//                snippet.setText(marker.getSnippet());
//
//                return infoWindow;
//            }
//        });

        // Turn on the My Location layer and the related control on the map.
        updateMapUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            isAddressSet = true;
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            isAddressSet = true;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), Constants.DEFAULT_ZOOM));
        } else {
            isAddressSet = false;
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, Constants.DEFAULT_ZOOM));
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        if ( mMap.isMyLocationEnabled() ) {
            startLocating();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocating(){
        if ( locationListener == null ) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    isAddressSet = true;
                    if ( isBetterLocation(location,mCurrentLocation) ) {
                        mCurrentLocation = location;
                        LatLng newLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                        strAddressList = getCompleteAddressString(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude(),Constants.LOCATION_RESULT_COUNT);
                        if ( strAddressList.size() > 0 ) {
                            tvAddress.setText(strAddressList.get(0));
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(newLocation).title(tvAddress.getText().toString()));
                        } else {
                            Toast.makeText(getContext(), "Location info unavailable, please try again.", Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG,"Use new location");
                    } else {
                        Log.d(TAG,"Use previous best location");
                    }

                }
            };
        }
        if ( locationRequest == null ) {
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(1000);
            locationRequest.setInterval(2000);
            locationRequest.setNumUpdates(5);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,locationListener);
    }

    @SuppressLint("MissingPermission")
    private void stopLocating(){
        if ( locationListener != null ) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,locationListener);
        }

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateMapUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    getDeviceLocation();
                    return false;
                }
            });

        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            Log.d(TAG,"first location");
            return true;
        }

        Log.d(TAG,"Comparing new location("+location.getLatitude()+","+location.getLongitude()+")" +
                " to currentBestLocation("+currentBestLocation.getLatitude()+","+currentBestLocation.getLongitude()+")");

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > LOCATION_EXPIRATION_TIME;
        boolean isSignificantlyOlder = timeDelta < -LOCATION_EXPIRATION_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Log.d(TAG,"is sig newer");
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Log.d(TAG,"is sig older");
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Log.d(TAG,"more accurate");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.d(TAG,"newer but less accurate");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.d(TAG,"newer sig less accurate same provider");
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateMapUI();
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        mMapFragment.getMapAsync(this);
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    private List<String> getCompleteAddressString(double latitude, double longitude, int count) {
        Log.d("TEST","Get address for " + latitude + "," + longitude);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        ArrayList<String> addressList = new ArrayList<>(count);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, count);
            if (addresses != null) {
                for ( Address address : addresses ) {
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(address.getAddressLine(i)).append("\n");
                    }
                    strReturnedAddress.deleteCharAt(strReturnedAddress.length()-1);
                    addressList.add(strReturnedAddress.toString());
                    Log.w(TAG, "Resolved address:" + strReturnedAddress.toString());
                }
            } else {
                Log.e(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Cannot get Address!");
        }
        return addressList;
    }

    /**
     *  start an IntentService to query for a Address using a String
     * @param queryAddress  the string that contains the address info
     */
    private void getAddressFromString(String queryAddress) {
        Log.d(TAG,"Searching for address:" + queryAddress);

        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA,queryAddress);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }
        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG,"Result received:("+resultCode+") "+resultData);

            if (resultCode == Constants.SUCCESS_RESULT) {
                List<Address> addressList = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
                if ( addressList == null ) {
                    return;
                }
                ArrayList<String> addresses = new ArrayList<>(Constants.LOCATION_RESULT_COUNT);
                for ( Address address : addressList ) {
                    ArrayList<String> addressLines = new ArrayList<>();
                    for ( int i = 0; i <= address.getMaxAddressLineIndex(); i++ ) {
                        addressLines.add(address.getAddressLine(i));
                    }
                    String addressName = TextUtils.join(System.getProperty("line.separator"),addressLines);
                    addresses.add(addressName);
                }
                lvAddress.setAdapter(new ArrayAdapter<>(
                        getContext(),android.R.layout.simple_list_item_1,addresses
                ));
                lvAddress.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            } else {
                String errorMessage = resultData.getString(Constants.RESULT_DATA_KEY);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }

    }

    public void updateSelectedPaymentMethod(Source source){
        if ( source != null ) {
            if ( Source.CARD.equals(source.getType())) { // user selected a card as payment source
                SourceCardData cardData = (SourceCardData) source.getSourceTypeModel();
                String selectedCardInfo = getString(R.string.label_payment_method) + ":\n"
                        + cardData.getBrand() + " ending in " + cardData.getLast4();
                tvPaymentMethod.setText(selectedCardInfo);
                isPaymentMethodSet = true;
            } else {
                // TODO: implement other payment methods first
                Log.d(TAG,"Source type = " + source.getType());
                tvPaymentMethod.setText(R.string.label_payment_method);
                isPaymentMethodSet = false;
            }
        } else {
            tvPaymentMethod.setText(R.string.label_payment_method);
            isPaymentMethodSet = false;
        }
    }

    /**
     *  The context must implement this interface to perform UI interaction on this fragment
     */
    public interface OnOrderFragmentInteractionListener {
        void onChooseCard();    // goto the payment method selection page
        void onBackButtonPressed();
    }
}
