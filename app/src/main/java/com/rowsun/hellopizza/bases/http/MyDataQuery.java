package com.rowsun.hellopizza.bases.http;

import android.content.Context;
import android.os.AsyncTask;


import com.rowsun.hellopizza.bases.utils.Pref;

import java.util.HashMap;

/**
 * Created by rowsun on 9/2/15.
 */
public class MyDataQuery {
    Context context;
    OnDataReceived action;
    Pref pf;

    public MyDataQuery(Context context, OnDataReceived action) {
        this.context = context;
        this.action = action;
        pf = new Pref(context);
    }

//    public void getRequestData(String table){
//        getRequestData(table, 0);
//    }

    public void getRequestData(HashMap<String, String> params) {
        getRequestData("table", params);
    }

    public void getRequestData(String table, int skip) {
        getRequestData(table, getRequestParameters(table, skip));
    }

    public void getRequestData(final String table) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                return new ServerRequest(context).httpGetData(table);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
//                Utilities.log("MY data query result = " + result);
                if (action != null) {
                    action.onSuccess(table, result);
                }
            }
        }.execute();

    }

    public void getRequestData(final String table, final HashMap<String, String> params) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return new ServerRequest(context).httpPostData(table,params);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (action != null) {
                    action.onSuccess(table, result);
                }
            }
        }.execute();
    }

    public HashMap<String, String> getRequestParameters(String table, int skip) {
        HashMap<String, String> params = new HashMap<String, String>();


        return params;
    }
}
