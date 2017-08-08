package com.example.cristianoyl.restaurant.request;

/**
 * Created by CristianoYL on 8/2/17.
 */

public interface RequestAction {
    void actOnPre();
    void actOnPost(int responseCode,String response);
}
