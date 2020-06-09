package ru.polytech.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText ETLogin;
    private EditText ETPassword;
    private EditText ETRepeatPassword;
    private Switch SwRegister;
    private Button BLogin;
    private String baseUrl;
    private boolean swState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ETLogin = findViewById(R.id.etLogin);
        ETPassword = findViewById(R.id.etPassword);
        ETRepeatPassword = findViewById(R.id.etRepeatPassword);
        SwRegister = findViewById(R.id.swRegister);
        BLogin = findViewById(R.id.bLogin);

        SwRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ETRepeatPassword.setVisibility(View.VISIBLE);
                } else {
                    ETRepeatPassword.setVisibility(View.GONE);
                }
                swState = isChecked;
            }
        });

        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ETLogin.getText().toString();
                String password = ETPassword.getText().toString();

                baseUrl = "http://192.168.1.6:8080/";

                String urlResource;
                if (swState) {
                    String repeatPassword = ETRepeatPassword.getText().toString();
                    if (!password.equals(repeatPassword)) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "passwords are not the same!",  Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    urlResource = "auth/signUp";
                } else {
                    urlResource = "auth/signIn";
                }

                JSONObject object = new JSONObject();
                try {
                    object.put("userName", username);
                    object.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final RequestHandler requestHandler = new RequestHandler(baseUrl);

                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                requestHandler.setUrlResource(urlResource);
                requestHandler.setHttpMethod("POST");
                requestHandler.execute(new CallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        startMainActivityAndClose(result);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                    @Override
                    public void onFail(String message, int code) {
                        ifFailure(code);
                    }
                }, object.toString());
            }
        });
    }

    private void ifFailure(int code) {
        final String msg;
        switch (code) {
            case HttpURLConnection.HTTP_BAD_REQUEST: {
                msg = "User with this name already exist!";
                break;
            }
            default: {
                msg = "Invalid username or password!";
            }
        }

        runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(),
                        msg,  Toast.LENGTH_LONG);
                toast.show();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    private void startMainActivityAndClose(String result) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(BLogin.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            Intent intent = new Intent(this, MainActivity.class);
            JSONObject jsonReturnObject = new JSONObject(result);
            intent.putExtra("token", jsonReturnObject.get("token").toString());
            intent.putExtra("url", baseUrl);
            JSONArray rolesArray = jsonReturnObject.getJSONArray("roles");
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < rolesArray.length(); i++) {
                list.add(rolesArray.get(i).toString());
            }
            intent.putStringArrayListExtra("roles", list);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
