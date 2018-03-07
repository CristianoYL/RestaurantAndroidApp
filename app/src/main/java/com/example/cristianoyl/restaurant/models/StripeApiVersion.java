package com.example.cristianoyl.restaurant.models;

import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.gson.Gson;

/**
 * Created by CristianoYL on 11/6/17.
 */

public class StripeApiVersion {
    String stripe_api_version = Constants.STRIPE_API_VERSION;

    public static String getJsonStripeApiVersion() {
        return new Gson().toJson(new StripeApiVersion());
    }
}
