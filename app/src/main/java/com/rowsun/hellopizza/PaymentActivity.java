package com.rowsun.hellopizza;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rowsun.hellopizza.bases.databases.DatabaseHelper;
import com.rowsun.hellopizza.bases.http.MyDataQuery;
import com.rowsun.hellopizza.bases.http.OnDataReceived;
import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity implements OnDataReceived {

    Pref pf;
    String user_id, name, mobile, code, details, date, cardnumber;
    EditText et_name, dadd, et_mobile, et_credit_card, et_card_code, et_expiry;
    JsonObject object;
    ProgressDialog pg;
    MyDataQuery dq, dl;
    CardView checkout;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Delivery Details");
        et_name = (EditText) findViewById(R.id.et_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        dadd = (EditText) findViewById(R.id.et_add);
        et_card_code = (EditText) findViewById(R.id.et_card_code);
        et_credit_card = (EditText) findViewById(R.id.et_card_number);
        et_expiry = (EditText) findViewById(R.id.et_date);
        checkout = (CardView) findViewById(R.id.checkout);

        final TextInputLayout ti_name = (TextInputLayout) findViewById(R.id.ti_name);
        final TextInputLayout ti_mobile = (TextInputLayout) findViewById(R.id.ti_mobile);
        final TextInputLayout ti_add = (TextInputLayout) findViewById(R.id.ti_add);
        final TextInputLayout ti_cardcode = (TextInputLayout) findViewById(R.id.ti_card_code);
        final TextInputLayout ti_expiry = (TextInputLayout) findViewById(R.id.ti_date);
        final TextInputLayout ti_card_number = (TextInputLayout) findViewById(R.id.ti_card_number);
        pg = new ProgressDialog(this);
        pg.setTitle("Placing order");
        pg.setMessage("Please wait...");
        pf = new Pref(this);
        dq = new MyDataQuery(this, this);
        final String detail = pf.getPreferences("data");
        if(pf.getPreferences("type").equalsIgnoreCase("pickup")){
            dadd.setHint("Enter Pickup Date Time : 2017-05-20 11:09:10");
            ti_add.setHint("Enter Pickup Date Time : 2017-05-20 11:09:10");
        }else{
            dadd.setHint("Full delivery address");
            ti_add.setHint("Full delivery address");
        }
        try {
            JSONObject object = new JSONObject(detail);
            Utilities.log("User Detail:" + detail);
            user_id = object.optString("id");
            et_name.setText(object.optString("first_name") + " " + object.optString("last_name"));
            et_mobile.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.toast(PaymentActivity.this, "Something went wrong :(");
            finish();
            return;
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = et_name.getText().toString();
                mobile = et_mobile.getText().toString();
                details = dadd.getText().toString();
                cardnumber = et_credit_card.getText().toString();
                code = et_card_code.getText().toString();
                date = et_expiry.getText().toString();

                if (name.isEmpty()) {
                    ti_name.setError("Name is required");
                    return;
                }
                if (mobile.isEmpty()) {
                    ti_mobile.setError("Mobile Number is required");
                    return;
                }
                if (details.isEmpty()) {
                    ti_add.setError("Delivery address is required");
                    return;
                }
                if (cardnumber.isEmpty()) {
                    ti_card_number.setError("Card Number is required");
                    return;
                } if (code.isEmpty()) {
                    ti_cardcode.setError("Card Code is required");
                    return;
                } if (date.isEmpty()) {
                    ti_expiry.setError("Expiring date is required");
                    return;
                }

                object = new JsonObject();
                object.addProperty("user_id", user_id);
                object.addProperty("phone_number", mobile);
                object.addProperty("order_type", pf.getPreferences("type"));
                if(pf.getPreferences("type").equalsIgnoreCase("pickup")){
                    object.addProperty("pickup_time", details);

                }else{
                    object.addProperty("delivery_address", details);

                }
                object.addProperty("delivery_charge", "0");
                object.addProperty("bill_id", "");
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(pf.getPreferences("carts"));
                JsonArray carts = tradeElement.getAsJsonArray();
                object.add("orders", carts);
                final String total = pf.getPreferences("total");
                startPaypal(total);


            }
        });

        ((TextView)findViewById(R.id.sub_total)).setText(pf.getPreferences("total"));
        ((TextView)findViewById(R.id.total)).setText(pf.getPreferences("total"));
    }

    public void startPaypal(String total) {

        pg.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("data", String.valueOf(object));
        Utilities.log("Order detail : " + object);
        dq.getRequestData(Utilities.BASE_URL + "order/store", param);


    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onSuccess(String table_name, String result) {
        Utilities.log("Result " + result);
        pg.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("data")) {
                DatabaseHelper db = new DatabaseHelper(this);
                db.deleteAllData();
                Utilities.toast(this, "Order Created");
                new AlertDialog.Builder(this).setTitle("SUCCESS").setMessage("Order created successfully").setIcon(R.drawable.ic_check_black_24dp).setPositiveButton("Go to home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PaymentActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("force", true));
                        finish();
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        } catch (JSONException e) {
            Utilities.toast(this, "Unable to make order");
            e.printStackTrace();
        }

    }

}
