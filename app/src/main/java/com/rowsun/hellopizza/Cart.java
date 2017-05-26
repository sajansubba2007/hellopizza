package com.rowsun.hellopizza;

import android.support.annotation.Keep;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rowsun on 9/27/16.
 */
@Keep
public class Cart {
    @Keep
    public String item_id, title,img,description, price, quantity;

    public Cart() {
    }

    public Cart(String item_id, String title, String img, String description, String price, String quantity) {
        this.item_id = item_id;
        this.title = title;
        this.img = img;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public static List<Cart> getCartList(String string)
    {
        try{
            return getCartList(new JSONArray(string));
        }catch(Exception e){e.printStackTrace();}
        return new ArrayList<>();
    }
    public static List<Cart> getCartList(JSONArray jArr) {
        List<Cart> list = new ArrayList<>();
        try {
            for (int i = 0; i < jArr.length(); i++) {
                list.add(new Cart(jArr.optJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Cart(JSONObject jObj) {
        this.item_id=jObj.optString("item_id");
        this.title = jObj.optString("title");
        this.img = jObj.optString("img");
        this.description = jObj.optString("description");
        this.price = jObj.optString("price");
        this.quantity = "1";

    }
}
