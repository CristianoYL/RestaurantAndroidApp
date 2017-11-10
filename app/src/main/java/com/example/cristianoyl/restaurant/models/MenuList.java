package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

/**
 * Created by CristianoYL on 11/8/17.
 */

public class MenuList {
    int mid;    // menu id
    float price;   //  unit price
    int count;      // count of this item

    public MenuList(int mid, float price, int count) {
        this.mid = mid;
        this.price = price;
        this.count = count;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
