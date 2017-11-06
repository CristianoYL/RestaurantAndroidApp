package com.example.cristianoyl.restaurant.request;

/**
 * Created by CristianoYL on 8/3/17.
 *
 * this class provide static access to all the API endpoints
 */

public class EndPoints {
    private static final String URL_LOCAL = "http://192.168.1.13:5000";
    private static final String URL_CHANG = "http://192.168.0.116:5000";
    private static final String URL_MU = "http://10.0.5.51:5000";
    private static final String URL_HEROKU = "";
    private static final String URL = URL_CHANG;

    // login
    public static String urlLogin(){
        return URL + "/auth";
    }

    // register
    public static String urlUser(){
        return URL + "/user";
    }

    // restaurant
    public static String urlRestaurant() {
        return URL + "/restaurant";
    }

    // menu
    public static String urlRestaurantMenu(int restaurantID) {
//        return URL + "/menu/rid/" + restaurantID;
        return URL + "/menu";
    }

    // stripe
    public static String urlEphemeralKey() {
        return URL + "/transaction/ephemeral_key";
    }
}
