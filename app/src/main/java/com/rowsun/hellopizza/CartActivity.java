package com.rowsun.hellopizza;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.rowsun.hellopizza.bases.databases.DatabaseHelper;
import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv_cart;
    DatabaseHelper db;
    List<Cart> cartList;
    Double total = 0.0;
    LinearLayout ll_empty;
    TextView ttl;
    CardView checkout, gt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Cart");
        gt = (CardView) findViewById(R.id.go_to);
        checkout = (CardView) findViewById(R.id.checkout);
        ttl = (TextView) findViewById(R.id.ttl);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

        cartList = new ArrayList<>();
        db = new DatabaseHelper(this);
        cartList.addAll(db.getCart());
        rv_cart = (RecyclerView) findViewById(R.id.rv_cart);
        rv_cart.setLayoutManager(new LinearLayoutManager(this));
        rv_cart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv_cart.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.row_cart, parent, false);
                return new ViewHolderCart(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                final ViewHolderCart vc = (ViewHolderCart) holder;
                final Cart c = cartList.get(position);
                vc.name.setText(cartList.get(position).title);
                final Double p, q;
                p = Double.parseDouble(c.price);
                q = Double.parseDouble(c.quantity);
                vc.total.setText("$" + (p * q));
                int pos = q.intValue();
                vc.qty.setText(new Double(c.quantity).intValue() + "");

                vc.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.deleteById(c.item_id);
                        cartList.remove(position);
                        findTotal(cartList);
                        notifyDataSetChanged();
                        ((TextView)findViewById(R.id.item_count)).setText(cartList.size() + " ITEM");
                    }
                });

                if (!c.img.isEmpty()) {
                    Picasso.with(CartActivity.this).load(c.img).into(vc.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            vc.image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            vc.image.setVisibility(View.GONE);

                        }
                    });
                }
                vc.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int a = Integer.parseInt(vc.qty.getText().toString());
                        a++;
                        vc.qty.setText(a + "");
                        c.quantity = String.valueOf(a);
                        db.updateCart(c);
                        cartList.set(position, c);
                        vc.total.setText("$" + (p * a));
                        findTotal(cartList);
                        notifyDataSetChanged();

                    }
                });
                vc.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int a = Integer.parseInt(vc.qty.getText().toString());
                        if (a != 1) {
                            a--;
                        }
                        vc.qty.setText(a + "");
                        c.quantity = String.valueOf(a);
                        db.updateCart(c);
                        cartList.set(position, c);
                        vc.total.setText("$" + (p * a));
                        findTotal(cartList);
                        notifyDataSetChanged();

                    }
                });
                if (!c.img.isEmpty()) {
                    Picasso.with(CartActivity.this).load(c.img).into(vc.image);
                }
//                vc.qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                        int a = Integer.parseInt(vc.qty.getSelectedItem().toString());
//                        c.quantity = String.valueOf(a);
//                        db.updateCart(c);
//                        cartList.set(position, c);
//                        notifyDataSetChanged();
//                        vc.total.setText("$" + (p * a));
//                        findTotal(cartList);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });


            }

            @Override
            public int getItemCount() {
                return cartList.size();
            }
        });


        findTotal(cartList);
        if (cartList.size() > 0) {
            ll_empty.setVisibility(View.GONE);

        } else {
            checkout.setVisibility(View.GONE);
            ttl.setVisibility(View.GONE);
            gt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CartActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            });
        }

        final Pref pf = new Pref(this);
        findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!pf.getBoolPreferences("isLoggedIn")){
                    startActivityForResult(new Intent(CartActivity.this, LoginActivity.class), 200);
                    return;
                }

                JSONArray array = new JSONArray();
                for (int i = 0; i < cartList.size(); i++) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("menu_id", cartList.get(i).item_id);
                        object.put("quantity", cartList.get(i).quantity);
                        object.put("message_to_chef", "");
                        object.put("toppings", "");
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                new Pref(CartActivity.this).setPreferences("carts", array.toString());

                startActivity(new Intent(CartActivity.this, PaymentActivity.class));
            }
        });
        ((TextView)findViewById(R.id.item_count)).setText(cartList.size() + " ITEM");
        findViewById(R.id.continue_shoping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void findTotal(List<Cart> cartList) {
        total = 0.0;
        for (int i = 0; i < cartList.size(); i++) {
            total += (Double.parseDouble(cartList.get(i).quantity) * Double.parseDouble(cartList.get(i).price));
        }
        ((TextView) findViewById(R.id.ttl)).setText("Total = $" + total);
        new Pref(this).setPreferences("total", total.toString());
    }

    class ViewHolderCart extends RecyclerView.ViewHolder {
        ImageView image, add, minus, remove;
        TextView qty, name, total;

        public ViewHolderCart(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            total = (TextView) itemView.findViewById(R.id.total);
            qty = (TextView) itemView.findViewById(R.id.qty);
            add = (ImageView) itemView.findViewById(R.id.add);
            minus = (ImageView) itemView.findViewById(R.id.minus);
            remove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuItem item = menu.add(1, 100, 300, "Settings");
        item.setIcon(R.drawable.ic_delete);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == 100) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("Are you sure want to clear cart ???");
            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deleteAllData();
                    cartList.clear();
                    rv_cart.getAdapter().notifyDataSetChanged();
                    findTotal(cartList);
                    ll_empty.setVisibility(View.VISIBLE);
                    checkout.setVisibility(View.GONE);
                    ttl.setVisibility(View.GONE);
                    gt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(CartActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    });
                }
            }).setNegativeButton("No", null).show();

        }
        return true;
    }
}
