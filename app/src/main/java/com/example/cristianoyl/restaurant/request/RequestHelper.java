package com.example.cristianoyl.restaurant.request;

import com.example.cristianoyl.restaurant.utils.Constants;

/**
 * Created by CristianoYL on 8/2/17.
 *
 * This helper class provides an easy access that allows user to
 * send HTTP requests and process response asynchronously.
 */

public class RequestHelper {
    /**
     * send a GET request to the given url with params,
     * perform the actions if specified
     * @param url the url to send the request to
     * @param action the action to perform before/after the request
     */
    public static void sendGetRequest(String url, RequestAction action){
        AsyncRequest asyncRequest = new AsyncRequest(url, Constants.METHOD_GET,null,action);
        asyncRequest.execute();
    }

    /**
     * send a POST request to the given url with params,
     * perform the actions if specified
     * @param url the url to send the request to
     * @param jsonData the params of the request in JSON format
     * @param action the action to perform before/after the request
     */
    public static void sendPostRequest(String url, String jsonData, RequestAction action){
        AsyncRequest asyncRequest = new AsyncRequest(url, Constants.METHOD_POST,jsonData,action);
        asyncRequest.execute();
    }

    /**
     * send a PUT request to the given url with params,
     * perform the actions if specified
     * @param url the url to send the request to
     * @param jsonData the params of the request in JSON format
     * @param action the action to perform before/after the request
     */
    public static void sendPutRequest(String url, String jsonData, RequestAction action){
        AsyncRequest asyncRequest = new AsyncRequest(url, Constants.METHOD_PUT,jsonData,action);
        asyncRequest.execute();
    }

    /**
     * send a DELETE request to the given url with params,
     * perform the actions if specified
     * @param url the url to send the request to
     * @param jsonData the params of the request in JSON format
     * @param action the action to perform before/after the request
     */
    public static void sendDelRequest(String url, String jsonData, RequestAction action){
        AsyncRequest asyncRequest = new AsyncRequest(url, Constants.METHOD_DEL,jsonData,action);
        asyncRequest.execute();
    }

    private static void sendRequest(String url, String method, String jsonData, RequestAction action) {

    }

}
