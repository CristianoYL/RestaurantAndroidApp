package com.example.cristianoyl.restaurant.models;

import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

    Menu(int id, int rid, String name, String category, String description, float price,
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

    public static Menu buildFromJson(JSONObject jsonMenu) throws JSONException {
        int id = jsonMenu.getInt(Constants.MENU_ID);
        int rid = jsonMenu.getInt(Constants.MENU_RID);
        String name = jsonMenu.getString(Constants.MENU_NAME);
        float price = (float) jsonMenu.getDouble(Constants.MENU_PRICE);
        String category = jsonMenu.getString(Constants.MENU_CATEGORY);
        String description = jsonMenu.getString(Constants.MENU_DESCRIPTION);
        int spicy = jsonMenu.getInt(Constants.MENU_SPICY);
        boolean isAvailable = jsonMenu.getBoolean(Constants.MENU_IS_AVAILABLE);
        boolean isRecommended = jsonMenu.getBoolean(Constants.MENU_IS_RECOMMENDED);
        return new Menu(id,rid,name,category,description,price,spicy,isAvailable,isRecommended);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
