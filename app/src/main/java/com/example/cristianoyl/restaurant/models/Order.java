package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;
import com.stripe.android.model.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CristianoYL on 11/7/17.
 */

public class Order {
    int id;     // order id
    int uid;    // user id
    ArrayList<MenuList> menuList;    // order content and info
    float deliveryFee, tips, subtotal, discount, tax, total;
    String message; // the message customer leaves for the merchant
    String status;  // order status
    String time;    // time of order
    String phone;   // customer's contact phone #
    String deliveryAddress;
    Source source;  // the payment source customer selects


    Order(int id, int uid, ArrayList<MenuList> menuList, float deliveryFee, float tips, float subtotal,
                 float discount, float tax, float total, String message, String status, String time,
                 String phone, String deliveryAddress, Source source) {
        this.id = id;
        this.uid = uid;
        this.menuList = menuList;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.message = message;
        this.status = status;
        this.time = time;
        this.phone = phone;
        this.deliveryAddress = deliveryAddress;
        this.source = source;
    }

    // use this constructor to create a new Order object from customer's order
    public Order() {
        this(0,0,new ArrayList<MenuList>(),0,0,0,0,0,0,
                null,null,null,null,null,null);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public void setDeliveryFee(float deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setTips(float tips) {
        this.tips = tips;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setSource(Source source) {
        this.source = source;
    }
    public void addOrderItem(int menuID, float unitPrice, int count) {
        this.menuList.add(new MenuList(menuID,unitPrice,count));
    }
}
