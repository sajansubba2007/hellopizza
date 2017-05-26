package com.rowsun.hellopizza;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rowsun on 9/27/16.
 */

public class Menu implements Serializable {

    public String id, name,image,description, price;

    public Menu() {
    }

    public Menu(String id, String name, String description, String image, String price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
    }

    public static List<Menu> getMenuList(String string)
    {
        try{
            return getMenuList(new JSONArray(string));
        }catch(Exception e){e.printStackTrace();}
        return new ArrayList<>();
    }
    public static List<Menu> getMenuList(JSONArray jArr) {
        List<Menu> list = new ArrayList<>();
        try {
            for (int i = 0; i < jArr.length(); i++) {
                list.add(new Menu(jArr.optJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Menu(JSONObject jObj) {
        this.id=jObj.optString("id");
        this.name = jObj.optString("menu_name");
        this.image = jObj.optString("image");
        this.description = jObj.optString("description");
        this.price = jObj.optString("price");

    }
}
