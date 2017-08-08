package com.example.cristianoyl.restaurant.fragments.menu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Menu;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.request.EndPoints;
import com.example.cristianoyl.restaurant.request.RequestAction;
import com.example.cristianoyl.restaurant.request.RequestHelper;
import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";
    private static final String ARG_RESTAURANT = "restaurant";

    private Restaurant restaurant;
    private Menu[] menus;

    private ListView lv_menu;

    private OnFragmentInteractionListener mListener;



    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param restaurant The restaurant of which menu is to be displayed.
     * @return A new instance of fragment MenuFragment.
     */
    public static MenuFragment newInstance(Restaurant restaurant) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESTAURANT, restaurant.toJson());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = new Gson().fromJson(getArguments().getString(ARG_RESTAURANT),Restaurant.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Button btnBack = (Button) view.findViewById(R.id.btn_back);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final Button btnOrder = (Button) view.findViewById(R.id.btn_order);
        lv_menu = (ListView) view.findViewById(R.id.lv_menu);
        tvName.setText(restaurant.name);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v);
            }
        });
        MenuAdapter.OnOrderChangeListener onOrderChangeListener = new MenuAdapter.OnOrderChangeListener() {
            @Override
            public void addOrder(Menu menu) {
                float totalSum = Float.parseFloat(btnOrder.getText().toString().replace("$",""));
                totalSum += menu.price;
                String newSum = String.format(Locale.US,"%.2f",totalSum);   // precision to first two decimals
                btnOrder.setText("$"+newSum);
            }

            @Override
            public void removeOrder(Menu menu) {
                float totalSum = Float.parseFloat(btnOrder.getText().toString().replace("$",""));
                totalSum -= menu.price;
                String newSum = String.format(Locale.US,"%.2f",totalSum);   // precision to first two decimals
                btnOrder.setText("$"+newSum);
            }
        };
        loadMenu(restaurant.id,onOrderChangeListener);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(View view);
    }

    private void loadMenu(int restaurantID, final MenuAdapter.OnOrderChangeListener onOrderChangeListener){
        RequestAction actionGetMenu = new RequestAction() {
            @Override
            public void actOnPre() {
            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constants.MODEL_MENU);
                        menus = new Menu[jsonArray.length()];
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonMenu = jsonArray.getJSONObject(i);
                            int id = jsonMenu.getInt(Constants.MENU_ID);
                            int rid = jsonMenu.getInt(Constants.MENU_RID);
                            String name = jsonMenu.getString(Constants.MENU_NAME);
                            float price = (float) jsonMenu.getDouble(Constants.MENU_PRICE);
                            String category = jsonMenu.getString(Constants.MENU_CATEGORY);
                            String description = jsonMenu.getString(Constants.MENU_DESCRIPTION);
                            int spicy = jsonMenu.getInt(Constants.MENU_SPICY);
                            boolean isAvailable = jsonMenu.getBoolean(Constants.MENU_IS_AVAILABLE);
                            boolean isRecommended = jsonMenu.getBoolean(Constants.MENU_IS_RECOMMENDED);
                            Menu menu = new Menu(id,rid,name,category,description,price,spicy,isAvailable,isRecommended);
                            menus[i] = menu;
                        }

                        lv_menu.setAdapter(new MenuAdapter(getContext(),menus,onOrderChangeListener));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG,response);
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString(Constants.KEY_MSG);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        Log.e(TAG,message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG,response);
                    }
                }
            }
        };
        String url = EndPoints.urlRestaurantMenu(restaurantID);
        RequestHelper.sendGetRequest(url,actionGetMenu);
    }
}
