package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

/**
 * Created by CristianoYL on 8/3/17.
 */

public class User {
    int id;
    String phone, password;

    public User(int id, String phone, String password) {
        this.id = id;
        this.phone = phone;
        this.password = password;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public String toCredentialJson(){
        return new Credential(phone,password).toJson();
    }
}
