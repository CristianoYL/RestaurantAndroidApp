package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

/**
 * Created by CristianoYL on 8/3/17.
 */

public class User {
    int id;
    String stripeID, email, phone, password;

    public User(int id, String stripeID, String email, String phone, String password) {
        this.id = id;
        this.stripeID = stripeID;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(String email, String password){
        this(0,null,email,null,password);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public String toCredentialJson(){
        return new Credential(email,password).toJson();
    }
}
