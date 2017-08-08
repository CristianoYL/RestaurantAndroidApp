package com.example.cristianoyl.restaurant.fragments.restaurant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.request.EndPoints;
import com.example.cristianoyl.restaurant.request.RequestAction;
import com.example.cristianoyl.restaurant.request.RequestHelper;
import com.example.cristianoyl.restaurant.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRestaurantClickListener}
 * interface.
 */
public class RestaurantFragment extends Fragment {

    private int mColumnCount = 1;
    private OnRestaurantClickListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantFragment() {
    }

    public static RestaurantFragment newInstance() {
        RestaurantFragment fragment = new RestaurantFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            loadRestaurants(recyclerView);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRestaurantClickListener) {
            mListener = (OnRestaurantClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRestaurantClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    private void loadRestaurants(final RecyclerView recyclerView){
        RequestAction actionGetRestaurants = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constants.LIST_RESTAURANT);
                        List<Restaurant> list = new ArrayList<>(jsonArray.length());
                        for ( int i = 0; i < jsonArray.length(); i++ ) {
                            JSONObject jsonRestaurant = jsonArray.getJSONObject(i);
                            int id = jsonRestaurant.getInt(Constants.RESTAURANT_ID);
                            String name = jsonRestaurant.getString(Constants.RESTAURANT_NAME);
                            float fee = (float) jsonRestaurant.getDouble(Constants.RESTAURANT_FEE);
                            float limit = (float) jsonRestaurant.getDouble(Constants.RESTAURANT_LIMIT);
                            String address = jsonRestaurant.getString(Constants.RESTAURANT_ADDRESS);
                            String openTime = jsonRestaurant.getString(Constants.RESTAURANT_OPEN_TIME);
                            String closeTime = jsonRestaurant.getString(Constants.RESTAURANT_CLOSE_TIME);
                            boolean isOpen = jsonRestaurant.getBoolean(Constants.RESTAURANT_IS_OPEN);
                            String logo = jsonRestaurant.getString(Constants.RESTAURANT_LOGO);
                            String promo = jsonRestaurant.getString(Constants.RESTAURANT_PROMO);
                            String phone = jsonRestaurant.getString(Constants.RESTAURANT_PHONE);
                            list.add(new Restaurant(id,name,address,openTime,closeTime,logo,promo,phone,fee,limit,isOpen));
                        }
                        Context context = recyclerView.getContext();
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(context,
                                DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(mDividerItemDecoration);
                        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(list, mListener));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String url = EndPoints.urlRestaurant();
        RequestHelper.sendGetRequest(url,actionGetRestaurants);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRestaurantClickListener {
        void onRestaurantClicked(Restaurant restaurant);
    }
}
