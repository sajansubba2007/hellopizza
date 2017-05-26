package com.rowsun.hellopizza.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.rowsun.hellopizza.R;
import com.rowsun.hellopizza.bases.http.ServerRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by rowsun on 6/7/16.
 */
public class Utilities {
    public static final String BASE_URL = "http://192.168.0.105:8000/api/";
    public static final String SD_PATH = Environment.getExternalStorageDirectory() + "/hellopizza";
    public static final String BASE_URL_IMAGE = "http://192.168.0.105:8000/";

    public static void log(String string) {
//        if (BuildConfig.DEBUG)
            Log.i("Utils Log", string);
    }

    public static String removeTags(String in) {
        int index = 0;
        int index2 = 0;
        while (index != -1) {
            index = in.indexOf("<");
            index2 = in.indexOf(">", index);
            if (index != -1 && index2 != -1) {
                in = in.substring(0, index).concat(in.substring(index2 + 1, in.length()));
            }
        }
        return in;
    }

    public static String replaceImageIfNoInternet(Context context, String html) {
        if (!ServerRequest.isNetworkConnected(context)) {
            html = html.replaceAll("\\<img.*?>", "<img src='file:///android_asset/no_image.jpg'/>");
        }
        return html;
    }

    public static String resizeImage(String url, String size) {
        if (!url.isEmpty()) {
            url = "http://cdn.hamroapi.com/resize?w=" + size + "&url=" + url;
        }
        return url;
    }

    public static void shareApp(Context context) {
        context.startActivity(Intent.createChooser(getDefaultShareIntent("Share " + context.getString(R.string.app_name),
                "http://play.google.com/store/apps/details?id="
                        + context.getPackageName()), "Share this App"));
    }

    public static long getPrefNum(Context ctx, String key) {
        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(ctx);
        return pf.getLong(key, 0l);

    }

    public static String getDeviceId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";

    }

    private static String getFileNameFromUrl(String url) {
        try {
            return url.substring(url.lastIndexOf("/") + 1);
        } catch (Exception e) {
        }
        return "";

    }

    public static File getSDPathFromUrl(String url) {
        return createFilePath(SD_PATH, getFileNameFromUrl(url));
    }

    public static boolean checkFileExist(String url) {
        if (url == null || url.isEmpty())
            return false;
        File f = createFilePath(SD_PATH, getFileNameFromUrl(url));
        return f.exists();

    }

    public static String captureScreen(View v) {
        Toast.makeText(v.getContext(), "Generating Screenshot. Please wait..", Toast.LENGTH_LONG).show();
        // View v = activity.getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/hindishayari");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void shareFile(final Context context, final String title, String path) {
        if (path == null) {
            return;
        }
        MediaScannerConnection.scanFile(context, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        context.startActivity(Intent.createChooser(
                                shareIntent, "Share Screenshot"));
                        //Toast.makeText(context, "Select an app to share", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private static File createFilePath(String folder, String filename) {
        File f = new File(folder);
        if (!f.exists())
            f.mkdirs();
        return new File(f, filename);
    }

    public static boolean setPrefNum(Context ctx, String key, long val) {
        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(ctx);
        return pf.edit().putLong(key, val).commit();
    }

    public static boolean isCacheValid(Context ctx, String key) {
        long last = getPrefNum(ctx, key);
        return (System.currentTimeMillis() - last < 60 * 60 * 1000);
    }

    public static boolean isCacheValid(Context ctx, String key, long validity) {
        long last = getPrefNum(ctx, key);
        return (System.currentTimeMillis() - last < validity);
    }


    public static String getDeviceInfo() {
        try {
            return Build.MANUFACTURER.toUpperCase() + " (" + Build.MODEL + ")";
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "";

    }


    public static void openLink(Context context, String url) {
        if (url == null || url.isEmpty())
            return;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String withSuffix(String c) {
        long count = Long.parseLong(c);
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "KMGTPE".charAt(exp - 1));
    }

    public static void sendEmail(Context context, String name, String email) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, name);
        i.putExtra(Intent.EXTRA_TEXT, name);
        try {

            context.startActivity(Intent.createChooser(i, "Select action"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getYoutubeLink(String id, String hq) {

        return "https://img.youtube.com/vi/" + id + "/" + hq + "default.jpg";
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface DynamicHeight {
        void HeightChange(int position, int height);
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    public static void doRate(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + context.getPackageName()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + context.getPackageName()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }


    public static long getRemainingTime(String then) throws ParseException {
        // Example 1
// Date then; // When the notification will occur
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = simpleDateFormat.parse(then);
        long timestamp = d.getTime();
        long nowtime = System.currentTimeMillis();
        return nowtime - timestamp;
//        String remaining1 = DateUtils.formatElapsedTime((timestamp - nowtime) / 1000);
//        return remaining1;
    }
//  public static long getTime(String then) throws ParseException {
//        // Example 1
//// Date then; // When the notification will occur
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date d = simpleDateFormat.parse(then);
//        long timestamp = d.getTime();
//        long nowtime = System.currentTimeMillis();
//        return nowtime - timestamp;
////        String remaining1 = DateUtils.formatElapsedTime((timestamp - nowtime) / 1000);
////        return remaining1;
//    }

    public static String getRelativeTime(long timestamp) {
        long nowtime = System.currentTimeMillis();
        if (timestamp < nowtime) {
            return (String) DateUtils.getRelativeTimeSpanString(timestamp, nowtime, 0);
        }
        return "Just now";
    }

    public static String getRelativeTime(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = simpleDateFormat.parse(date);
        long timestamp = d.getTime();
        long nowtime = System.currentTimeMillis();
        if (timestamp < nowtime) {
            return (String) DateUtils.getRelativeTimeSpanString(timestamp, nowtime, 0);
        }
        return "Just now";
    }

    public static Intent getDefaultShareIntent(String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return Intent.createChooser(intent, title);
    }

    public static int getVersioncode(Context context) {

        int version = 1;

        try {

            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return version;
    }

    //    public static void sendEvent(String category, String action, String label) {
//        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
//        if (t == null) return;
//        t.send(new HitBuilders.EventBuilder()
//                .setCategory(category)
//                .setAction(action)
//                .setLabel(label)
//                .build());
//
//    }
//
//
//    public static void sendScreen(String screenName) {
//        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
//        if (t == null) return;
//        t.setScreenName(screenName);
//        t.send(new HitBuilders.ScreenViewBuilder()
//                .build());
//
//    }
    public static String getDateWithFormat(String format, String date) {
        //Formats yyyy-MM-dd hh:mm:ss , For month in string MMM
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date from = dateFormat.parse(date);
            return new SimpleDateFormat(format).format(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}