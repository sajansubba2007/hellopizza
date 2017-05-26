package com.rowsun.hellopizza.bases.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Keep;


import com.rowsun.hellopizza.Cart;
import com.rowsun.hellopizza.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rowsun on 9/27/16.
 */
@Keep
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_CART = "cart";
    private static final String COLUMN_ID = "item_id";
    private static final String ID = "_id";
    private static final String COLUMN_NAME = "title";
    private static final String COLUMN_IMAGE = "img";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";

    private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART +
            " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
            COLUMN_ID + " TEXT ," +
            COLUMN_NAME + " TEXT ," +
            COLUMN_IMAGE + " TEXT ," +
            COLUMN_DESCRIPTION + " TEXT ," +
            COLUMN_PRICE + " TEXT ," +
            COLUMN_QUANTITY + " TEXT "
            + ");";
    private Context mContext;
    private static final String DB_NAME = "hello_pizza";
    private static final int DB_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CART);
            Utilities.log("Table Created.......................");
        } catch (SQLException exception) {
            Utilities.log("Error" + exception);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP " + TABLE_CART + " IF EXISTS;");
            onCreate(db);
        } catch (SQLException exception) {
            Utilities.log(exception + "");
        }
    }

    public boolean addToCart(Cart m) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, m.item_id);
        values.put(COLUMN_NAME, m.title);
        values.put(COLUMN_IMAGE, m.img);
        values.put(COLUMN_DESCRIPTION, m.description);
        values.put(COLUMN_PRICE, m.price);
        values.put(COLUMN_QUANTITY, m.quantity);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CART, null, values);
        db.close();
        return true;
    }

    public void updateCart(Cart m) {
        SQLiteDatabase db = getWritableDatabase();
        if (isExists(TABLE_CART, m.item_id)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, m.item_id);
            values.put(COLUMN_NAME, m.title);
            values.put(COLUMN_IMAGE, m.img);
            values.put(COLUMN_DESCRIPTION, m.description);
            values.put(COLUMN_PRICE, m.price);
            values.put(COLUMN_QUANTITY, m.quantity);
            db.update(TABLE_CART, values, COLUMN_ID + "=?", new String[]{m.item_id});
            db.close();
        }
    }

    public List<Cart> getCart() {
        List<Cart> result = new ArrayList<>();
        String sql = "Select * from " + TABLE_CART;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Cart s = new Cart(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                result.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public Cart getCart(String id) {
        Cart s = new Cart();
        String sql = "SELECT * FROM " + TABLE_CART + " WHERE id=" + id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            {
                s = new Cart(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            }
        }
        cursor.close();
        db.close();
        return s;
    }

    public boolean isExists(String table, String id) {
        String sql = "Select * from " + table + " where " + COLUMN_ID + "=" + id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();
        db.close();
        return false;

    }

    public boolean isExists(String id) {
        String sql = "Select * from " + TABLE_CART + " where " + COLUMN_ID + "=" + id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();
        db.close();
        return false;

    }

    public void deleteById(String id) {
        String sql = "DELETE FROM " + TABLE_CART + " WHERE "+COLUMN_ID+"=" + id ;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void deleteAllData() {
        String sql = "DELETE FROM " + TABLE_CART + " ";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


}