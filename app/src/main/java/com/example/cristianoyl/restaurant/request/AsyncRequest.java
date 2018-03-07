package com.example.cristianoyl.restaurant.request;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cristianoyl.restaurant.utils.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by CristianoYL on 8/2/17.
 */

public class AsyncRequest extends AsyncTask<Void, Void, String> {

    private static final String TAG = "AsyncRequest";
    private String jwt;
    private String url;
    private String method;
    private String jsonData;
    private int responseCode = 400;
    private RequestAction requestAction = null;

    public AsyncRequest(String jwt, String url, String method, String data, RequestAction action){
        this.jwt = jwt;
        this.url = url;
        this.method = method;
        this.jsonData = data;
        requestAction = action;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return sendRequest(jwt,url,method,jsonData);
    }

    @Override
    protected void onPreExecute() {
        if ( requestAction != null ) {
            requestAction.actOnPre();
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        requestAction.actOnPost(responseCode,result);
        super.onPostExecute(result);
    }

    private String sendRequest(String jwt, String url, String method, String jsonData){
        Log.d(TAG,"sending " + method +" request to " + url);
        Log.d(TAG,"payload:" + jsonData);
        String response = "";
        InputStream inputStream;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
            httpURLConnection.setRequestMethod(method);
            if ( jwt != null ) {
                httpURLConnection.setRequestProperty("Authorization", "JWT "+jwt);
                Log.d(TAG,"Setting JWT:"+jwt);
            }
            if (jsonData != null) {
                httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(Constants.CONN_TIME_OUT);
                httpURLConnection.setReadTimeout(Constants.READ_TIME_OUT);
                outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, Constants.SERVER_CHARSET));
                writer.write(jsonData);
                writer.flush();
                writer.close();
            }
            this.responseCode = httpURLConnection.getResponseCode();
            if (this.responseCode < 400) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, Constants.SERVER_CHARSET), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            line = reader.readLine();
            if (line != null) {
                stringBuilder.append(line);
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append("\n");
                    stringBuilder.append(line);
                    Log.d(TAG, "readLine:" + line + ";");
                }
            }
            inputStream.close();
            response = stringBuilder.toString();
            Log.i(TAG, "response:"+ response + ";");
        } catch (SocketTimeoutException e) {
            response = Constants.MSG_TIME_OUT;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( httpURLConnection != null ) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }
}
