package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by CristianoYL on 8/7/17.
 */

public class Menu implements Serializable{
    public int id,rid;
    public String name, category, description;
    public float price;
    public int spicy;
    public boolean isAvailable, isRecommended;

    public Menu(int id, int rid, String name, String category, String description, float price,
                int spicy, boolean isAvailable, boolean isRecommended) {
        this.id = id;
        this.rid = rid;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.spicy = spicy;
        this.isAvailable = isAvailable;
        this.isRecommended = isRecommended;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
