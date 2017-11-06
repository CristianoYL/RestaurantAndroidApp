package com.example.cristianoyl.restaurant.stripe;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Log;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.activities.MainActivity;
import com.example.cristianoyl.restaurant.models.StripeApiVersion;
import com.example.cristianoyl.restaurant.request.EndPoints;
import com.example.cristianoyl.restaurant.request.RequestAction;
import com.example.cristianoyl.restaurant.request.RequestHelper;
import com.example.cristianoyl.restaurant.utils.Constants;
import com.google.gson.Gson;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CristianoYL on 11/2/17.
 */

/**
 * An implementation of {@link EphemeralKeyProvider} that can be used to generate
 * ephemeral keys on the backend.
 */
public class CustomEphemeralKeyProvider implements EphemeralKeyProvider {

    private static final String TAG = "EphemeralKeyProvider";
    private String jwt;

    public CustomEphemeralKeyProvider(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        RequestAction postEphemeralKey = new RequestAction() {
            @Override
            public void actOnPre() {

            }

            @Override
            public void actOnPost(int responseCode, String response) {
                if ( responseCode == 200 ) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String rawKey = jsonResponse.getString(Constants.EPHEMERAL_KEY);
                        keyUpdateListener.onKeyUpdate(rawKey);
                        PaymentConfiguration.init(Constants.STRIPE_PUBLISHABLE_KEY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,e.getMessage());
                    }
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString(Constants.KEY_MSG);
                        Log.e(TAG,message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,response);
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(this.jwt,EndPoints.urlEphemeralKey(), StripeApiVersion.getJsonStripeApiVersion(),postEphemeralKey);
    }
}
