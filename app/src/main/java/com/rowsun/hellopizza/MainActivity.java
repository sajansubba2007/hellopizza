package com.rowsun.hellopizza;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.rowsun.hellopizza.bases.databases.DatabaseHelper;
import com.rowsun.hellopizza.bases.http.MyDataQuery;
import com.rowsun.hellopizza.bases.http.OnDataReceived;
import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.FragmentInteractListener {

    Pref pf;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pf = new Pref(this);
        if (pf.getBoolPreferences("isLoggedIn")) {
            setupProfileView(navigationView);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);

        }
        openfragment(new HomeFragment());


    }


    private void setupProfileView(NavigationView navigationView) {
        String profile = new Pref(this).getPreferences("data");
        View profileView = navigationView.getHeaderView(0);
        try {
            Utilities.log(profile + "");
            JSONObject jsonObject = new JSONObject(profile);
            String name = jsonObject.optString("first_name") + " " + jsonObject.optString("last_name");
            String email = jsonObject.optString("email");
            ((TextView) profileView.findViewById(R.id.name)).setText(name);
            ((TextView) profileView.findViewById(R.id.email)).setText(email);

        } catch (Exception e) {
            ((TextView) profileView.findViewById(R.id.name)).setText("Hello Pizza");
            ((TextView) profileView.findViewById(R.id.email)).setText("hellopizza@gmail.com");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_del) {
            new Pref(this).setPreferences("type", "delivery");
            openfragment(MenuFragment.newInstance("delivery"));
        } else if (id == R.id.nav_pick) {

            new Pref(this).setPreferences("type", "pickup");
            openfragment(MenuFragment.newInstance("delivery"));

        } else if (id == R.id.nav_home) {
            openfragment(new HomeFragment());

        } else if (id == R.id.nav_share) {
            Utilities.shareApp(this);
        } else if (id == R.id.nav_send) {
            Utilities.sendEmail(this, "Send Feedback", "hellopizza@gmail.com");
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this).setTitle("SmokenRibs").setMessage("Are you sure want to logout?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String detail = pf.getPreferences("data");

                    HashMap<String, String> map = new HashMap<String, String>();
                    try {
                        JSONObject object = new JSONObject(detail);
                        map.put("email", object.optString("email"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    new MyDataQuery(MainActivity.this, new OnDataReceived() {
                        @Override
                        public void onSuccess(String table_name, String result) {
                            pf.setBoolPreferences("isLoggedIn", false);
                            new DatabaseHelper(MainActivity.this).deleteAllData();
                            pf.setBoolPreferences("isLoggedIn", false);
                            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                            setupProfileView(navigationView);
                        }
                    }).getRequestData(Utilities.BASE_URL_IMAGE + "auth/api/logout", map);
                }
            }).setNegativeButton("Cancel", null).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    boolean doubleBackToExitPressedOnce = false;
    boolean force = false;

    @Override
    public void finish() {
        if (force) {
            super.finish();
            return;
        }
        if (!isHomeFragment) {
            openfragment(new HomeFragment());
            return;
        }
        if (doubleBackToExitPressedOnce && isHomeFragment) {
            super.finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    boolean isHomeFragment;

    public void openfragment(Fragment fragment) {
        if (fragment != null) {
            this.isHomeFragment = fragment instanceof HomeFragment;

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
    }

    @Override
    public void open(Fragment f) {
        openfragment(f);
    }
}
