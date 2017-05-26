package com.rowsun.hellopizza;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import com.rowsun.hellopizza.bases.http.MyDataQuery;
import com.rowsun.hellopizza.bases.http.OnDataReceived;
import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity implements OnDataReceived {

    MyDataQuery dq;
    Pref pf;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pf = new Pref(this);
        pg = new ProgressDialog(this);
        pg.setMessage("Please wait...");
        final String fbdata = getIntent().getStringExtra("fbdata");

        final String fbid = getIntent().getStringExtra("fbid");
        Utilities.log("FBDATA " + fbdata);
        Utilities.log("FBID " + fbid);
        dq = new MyDataQuery(this, this);
        final EditText et_name, et_lname, et_email, et_password;
        final TextInputLayout ti_name, ti_mobile, ti_email, ti_password;
        et_name = (EditText) findViewById(R.id.et_name);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        ti_name = (TextInputLayout) findViewById(R.id.ti_name);
        ti_mobile = (TextInputLayout) findViewById(R.id.ti_mobile);
        ti_email = (TextInputLayout) findViewById(R.id.ti_email);
        ti_password = (TextInputLayout) findViewById(R.id.ti_password);
        try {
            JSONObject object = new JSONObject(fbdata);
            et_name.setText(object.optString("first_name") + " " + object.optString("last_name"));
            et_email.setText(object.optString("email"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, lname, email, password;
                name = et_name.getText().toString();
                lname = et_lname.getText().toString();
                email = et_email.getText().toString();
                password = et_password.getText().toString();
                boolean flag = false;
                if (name.isEmpty()) {
                    ti_name.setError("First Name is required");
                    flag = true;
                }
                if (lname.isEmpty()) {
                    ti_mobile.setError("Last Name");
                    flag = true;
                }
                if (email.isEmpty()) {
                    ti_email.setError("Email is required");
                    flag = true;
                }
                if (password.isEmpty()) {
                    ti_password.setError("Password is required");
                    flag = true;
                }
                if (flag) {
                    return;
                }
                Utilities.log("fbbb" + fbdata);

                pg.show();
//                dq.getRequestData(Utilities.BASE_URL + "register", map);
                serverRequest(name, lname, email, password, fbdata);
            }
        });

    }

    String ss = "";

    public String serverRequest(final String name, final String lname, final String email, final String password, final String fbdata) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("first_name", name)
                        .add("last_name", lname)
                        .add("email", email)
                        .add("password", password)
                        .add("password_confirmation", password)
                        .build();
                Request request = new Request.Builder()
                        .url(Utilities.BASE_URL + "register")
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                pg.dismiss();
                ss = s;
                onSuccess(Utilities.BASE_URL + "register", s);

                super.onPostExecute(s);
            }
        }.execute();
        return ss;
    }

    @Override
    public void onSuccess(String table_name, String result) {
        Utilities.log("Result" + result);
        pg.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.optString("status").equals("400")) {
                pf.setBoolPreferences("isLoggedIn", true);
                JSONObject msg;
                pf.setPreferences("data", jsonObject.optString("user"));
                msg = jsonObject.optJSONObject("user");
                pf.setPreferences("user_id", msg.optString("id"));
                startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            } else {
                Utilities.toast(this, "Registration failed.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
