package com.rowsun.hellopizza.bases.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerRequest {

    Context context;
    String action;
    Pref pf;

    public ServerRequest(Context context) {
        this.context = context;
        pf = new Pref(context);
    }

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
        /*if (ni == null) {
            return false; // There are no active networks.
		} else
			return true;*/
    }


    private String getUrlEncodeData(HashMap<String, String> params) {
        if (params == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
                Utilities.log("server, " + entry.getKey() + " = " + entry.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public String httpGetData(String url) {
        return httpGetData(url, null);
    }

//    public String httpGetData(String url, HashMap<String, String> params) {
//        Utilities.log("ServerRequest - GET :: url = " + url + "?" + getUrlEncodeData(params));
//        StringBuffer response = new StringBuffer();
//        try {
////            URLConnection conn = new URL(url).openConnection();
//            URL u = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestMethod("GET");
//
//            conn.setRequestProperty("X-APP-ID", Constants.APP_ID);
//            if (pf.getPreferences("pref_lang").equals("Nepali")) {
//                conn.setRequestProperty("X-APP-LANG", "ne");
//            } else {
//                conn.setRequestProperty("X-APP-LANG", "en");
//            }
//            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//            writer.write(getUrlEncodeData(getSha1Hex(params)));
//            writer.flush();
//            writer.close();
//
//            BufferedReader reader = new BufferedReader(new
//                    InputStreamReader(conn.getInputStream()));
//            String inputLine;
//
//            while ((inputLine = reader.readLine()) != null) {
//                response.append(inputLine);
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Utilities.log("ServerRequest - GET :: url : " + url + getUrlEncodeData(params) + " Response : " + response.toString());
//        return response.toString();
//    }

        public String httpPostData(HashMap<String, String> params){
        return httpPostData(Utilities.BASE_URL, params);
    }

    public String httpGetData(String url, HashMap<String, String> params){
        Utilities.log("ServerRequest - GET :: url : " + url  );
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public String httpPostData(String url, HashMap<String, String> params) {
        Utilities.log("ServerRequest - POST :: url = " + url + getUrlEncodeData(params));
        StringBuffer response = new StringBuffer();
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
//            conn.setRequestProperty("X-APP-ID", Constants.APP_ID);
//            conn.setRequestProperty("X-APP-LANG", "en");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(getUrlEncodeData(getSha1Hex(params)));
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utilities.log("ServerRequest - POST :: url : " + url + " Params : " + getUrlEncodeData(params) + " Response = " + response.toString());
        return response.toString();
    }

    public static HashMap<String, String> getSha1Hex(HashMap<String, String> params) {
        //params.put("signature", getSha1Hex(getUrlEncodeData(params)));
//        Utilities.log(Constants.APP_ID + "_" + Constants.ANDROID_KEY + params.get("action"));
//        params.put("signature", getSha1Hex(Constants.APP_ID + "_" + Constants.ANDROID_KEY + "_" + params.get("action")));
        return params;
    }

    private static String getSha1Hex(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(string.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            Utilities.log("SHA-1 :: params = " + string + " signature = " + buffer.toString());
            return buffer.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }
}
