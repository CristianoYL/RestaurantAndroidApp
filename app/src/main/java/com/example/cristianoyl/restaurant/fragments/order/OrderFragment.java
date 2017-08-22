package com.example.cristianoyl.restaurant.fragments.order;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.cristianoyl.restaurant.R.id.lv_address;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
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
    private TextView tvAddress, tvPhone;
    private ListView lvAddress;
    private List<String> strAddressList;

    private OnFragmentInteractionListener mListener;
    private AddressResultReceiver mResultReceiver;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(40.5233403,-74.4588031);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

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
        if (getArguments() != null) {
            restaurant = new Gson().fromJson(getArguments().getString(ARG_RESTAURANT),Restaurant.class);
            orderMap = (HashMap<Menu,Integer>) getArguments().getSerializable(ARG_ORDER_MAP);
        }
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        TextView tvRestaurantName = (TextView) view.findViewById(R.id.tv_name);
        tvRestaurantName.setText(restaurant.name);

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

        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "add a message", Toast.LENGTH_SHORT).show();
            }
        });

        TextView tvCoupon = (TextView) view.findViewById(R.id.tv_coupon);
        tvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "use a coupon", Toast.LENGTH_SHORT).show();
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
        tvAddress = (TextView) view.findViewById(R.id.tv_address);

        // show a popup dialog window when clicking the tvAddress
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_address,null);
                final EditText etAddress = (EditText) view.findViewById(R.id.et_address);
                lvAddress = (ListView) view.findViewById(lv_address);
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
                                LatLng latLng = new LatLng(
                                        addressList.get(0).getLatitude(),
                                        addressList.get(0).getLongitude());
                                mCameraPosition = CameraPosition.fromLatLngZoom(latLng,Constants.DEFAULT_ZOOM);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
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

        tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_phone,null);
                final EditText etPhone = (EditText) view.findViewById(R.id.et_phone);
                if ( !tvPhone.getText().toString().equals(getString(R.string.label_contact_phone)) ) {
                    etPhone.setText(tvPhone.getText().toString());
                }
                builder.setView(view);
                builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tvPhone.setText(PhoneNumberUtils.formatNumber(etPhone.getText().toString(),Locale.getDefault().getCountry()));
                        } else {
                            tvPhone.setText(PhoneNumberUtils.formatNumber(etPhone.getText().toString()));
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) getActivity().findViewById(R.id.frame_map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

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
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), Constants.DEFAULT_ZOOM));
            strAddressList = getCompleteAddressString(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),5);
            if ( strAddressList.size() > 0 ) {
                tvAddress.setText(strAddressList.get(0));
            } else {
                Toast.makeText(getContext(), "Location info unavailable, please try again.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, Constants.DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
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
            mLastKnownLocation = null;
        }
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
        updateLocationUI();
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     *  start an IntentService to query for a Address using a String
     * @param queryAddress  the string that contains the address info
     */
    private void getAddressFromString(String queryAddress) {
        Log.d(TAG,"Searching for address:" + queryAddress);
//        try {
//            return new Geocoder(getContext()).getFromLocationName(
//                    queryAddress,5);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
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
                ArrayList<String> addresses = new ArrayList<>(5);
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
}
