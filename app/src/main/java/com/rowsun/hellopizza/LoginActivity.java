package com.rowsun.hellopizza;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
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

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements OnDataReceived {
    MyDataQuery dq;
    CardView signin;
    EditText email, password;
    TextView signup, forgetPassword;
    Pref pf;
    ProgressDialog pg;
    String fbdata, fbid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        dq = new MyDataQuery(this, this);
        pf = new Pref(this);
        pg = new ProgressDialog(this);
        pg.setMessage("Please wait..");
        if (pf.getBoolPreferences("isLoggedIn")) {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return;
        }
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        signin = (CardView) findViewById(R.id.sign_in);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signup = (TextView) findViewById(R.id.sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eml = email.getText().toString();
                String psd = password.getText().toString();
                if (eml.isEmpty()) {
                    email.setError("invalid email");
                    return;
                }
                if (psd.isEmpty()) {
                    password.setError("invalid password");
                    return;
                }
                pg.show();
                dq.getRequestData(Utilities.BASE_URL_IMAGE + "auth/api/login", getHashMap(eml, psd));

            }
        });


    }

    @Override
    public void onSuccess(String table_name, String result) {
        pg.dismiss();
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.optString("status").equals("400")) {
                pf.setBoolPreferences("isLoggedIn", true);
                JSONObject msg = new JSONObject();
                pf.setPreferences("data", obj.optString("data"));
                msg = obj.optJSONObject("data");
                pf.setPreferences("user_id", msg.optString("id"));
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }else{
                Utilities.toast(this, "Invalid Username and password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getHashMap(String email, String password) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("password", password);
        return hashMap;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
