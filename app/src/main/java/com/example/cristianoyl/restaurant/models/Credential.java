package com.example.cristianoyl.restaurant.models;

import com.google.gson.Gson;

/**
 * Created by CristianoYL on 8/3/17.
 */

public class Credential {
    String username,password;

    public Credential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
