package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

/**
 * Created by CristianoYL on 8/4/17.
 */

public class Restaurant {
    public int id;
    public String name, address, openTime, closeTime, logo, promo, phone;
    public float fee, limit;
    public boolean isOpen;

    public Restaurant(int id, String name, String address, String openTime,
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

    public String toJson(){
        return new Gson().toJson(this);
    }
}
