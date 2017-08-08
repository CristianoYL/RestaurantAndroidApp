package com.example.cristianoyl.restaurant.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.User;
import com.example.cristianoyl.restaurant.request.EndPoints;
import com.example.cristianoyl.restaurant.request.RequestAction;
import com.example.cristianoyl.restaurant.request.RequestHelper;
import com.example.cristianoyl.restaurant.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private Button btnLogin, btnRegister, btnGuest;
    private EditText etAccount, etPassword, etPasswordConfirm;
    private View loadingView;
    private TextInputLayout inputLayoutPassword, inputLayoutConfirm;

    private boolean isLogin = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_auth);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnGuest = (Button) findViewById(R.id.btn_guest);
        etAccount = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPasswordConfirm = (EditText) findViewById(R.id.et_password_confirm);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_password);
        inputLayoutConfirm = (TextInputLayout) findViewById(R.id.input_password_confirm);
        loadingView = findViewById(R.id.layout_loading);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearErrors();
                if ( isLogin ) {    // user is trying to login
                    login();
                } else {    // user is trying to cancel registration
                    etPasswordConfirm.setText(null);
                    inputLayoutConfirm.setVisibility(View.GONE);
                    btnLogin.setText(R.string.btn_login);
                    isLogin = true;
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearErrors();
                if ( isLogin ) {    // user is trying to go to registration page
                    inputLayoutConfirm.setVisibility(View.VISIBLE);
                    btnLogin.setText(R.string.btn_cancel);
                    isLogin = false;
                } else {
                    register();
                }
            }
        });
    }

    private void goToMainActivity(String accessToken){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra(Constants.ACCESS_TOKEN,accessToken);
        startActivity(intent);
    }

    private void login(){
        if ( !TextUtils.isEmpty(etAccount.getText()) && !TextUtils.isEmpty(etPassword.getText()) ) {
            String phone = etAccount.getText().toString();
            String password = etPassword.getText().toString();
            User user = new User(0,phone,password);
            String url = EndPoints.urlLogin();
            RequestAction loginAction = new RequestAction() {
                @Override
                public void actOnPre() {
                    loadingView.setVisibility(View.VISIBLE);
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    loadingView.setVisibility(View.GONE);
                    if ( responseCode == 200 ) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String accessToken = jsonObject.getString(Constants.ACCESS_TOKEN);
                            goToMainActivity(accessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String description = jsonObject.getString(Constants.KEY_DESC);
                            etPassword.setError(description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            etPassword.setError(response);
                        }
                        Log.e(TAG,"Error Message:" + response);

                    }
                }
            };
            RequestHelper.sendPostRequest(url,user.toCredentialJson(),loginAction);
        }
    }

    private void register(){
        if ( !TextUtils.isEmpty(etAccount.getText()) && !TextUtils.isEmpty(etPassword.getText())
                && !TextUtils.isEmpty(etPasswordConfirm.getText()) ) {
            if ( etAccount.getText().toString().length() < 10 ) {   // check phone number
                etAccount.setError(getString(R.string.error_invalid_phone));
                etAccount.requestFocus();
                return;
            }
            if ( etPassword.getText().toString().length() < 8 ) {
                etPassword.setError(getString(R.string.error_password_length));
                etPassword.requestFocus();
                return;
            }
            if ( !etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()) ) {    // check password matching
                etPasswordConfirm.setError(getString(R.string.error_password_inconsistent));
                etPasswordConfirm.requestFocus();
                return;
            }
            String phone = etAccount.getText().toString();
            String password = etPassword.getText().toString();
            User user = new User(0,phone,password);
            String url = EndPoints.urlUser();
            RequestAction registerAction = new RequestAction() {
                @Override
                public void actOnPre() {
                    loadingView.setVisibility(View.VISIBLE);
                }

                @Override
                public void actOnPost(int responseCode, String response) {
                    loadingView.setVisibility(View.GONE);
                    if ( responseCode == 201 ) {
                        Toast.makeText(LoginActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                        etPasswordConfirm.setText(null);
                        inputLayoutConfirm.setVisibility(View.GONE);
                        btnLogin.setText(R.string.btn_login);
                        isLogin = true;
                    } else {
                        Log.e(TAG,"Error Message:" + response);
                        etPasswordConfirm.setError(response);
                    }
                }
            };
            RequestHelper.sendPostRequest(url,user.toJson(),registerAction);
        } else {
            if ( TextUtils.isEmpty(etAccount.getText()) ) {
                etAccount.setError(getString(R.string.error_empty_username));
                etAccount.requestFocus();
            } else if ( TextUtils.isEmpty(etPassword.getText()) ) {
                etPassword.setError(getString(R.string.error_empty_password));
                etPassword.requestFocus();
            } else {
                etPasswordConfirm.setError(getString(R.string.error_empty_confirmation));
                etPasswordConfirm.requestFocus();
            }
        }
    }

    private void clearErrors(){
        etAccount.setError(null);
        etPassword.setError(null);
        etPasswordConfirm.setError(null);
    }
}
