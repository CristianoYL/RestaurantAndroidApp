package com.example.cristianoyl.restaurant.models;

import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CristianoYL on 8/4/17.
 */

public class Restaurant {
    public int id;
    public String name, address, openTime, closeTime, logo, promo, phone;
    public float fee, limit;
    public boolean isOpen;

    private Restaurant(int id, String name, String address, String openTime,
                      String closeTime, String logo, String promo, String phone,
                      float fee, float limit, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.logo = logo;
        this.promo = promo;
        this.phone = phone;
        this.fee = fee;
        this.limit = limit;
        this.isOpen = isOpen;
    }

    public static Restaurant buildFromJson(JSONObject jsonRestaurant) throws JSONException {
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
        return new Restaurant(id,name,address,openTime,closeTime,logo,promo,phone,fee,limit,isOpen);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
