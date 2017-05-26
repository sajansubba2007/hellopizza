package com.rowsun.hellopizza;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rowsun.hellopizza.bases.databases.DatabaseHelper;
import com.rowsun.hellopizza.utilities.Utilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rowsun on 9/27/16.
 */

public class AdapterMenuItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Menu> menuList;
    private Context context;
    private LayoutInflater inflater;
    private DatabaseHelper db;

    public AdapterMenuItem(Context context, List<Menu> menuList) {
        this.context = context;
        this.menuList = menuList;
        this.inflater = LayoutInflater.from(context);
        db = new DatabaseHelper(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_menu_item, parent, false);
        return new ViewHolderMenuItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolderMenuItem v = (ViewHolderMenuItem) holder;
        final Menu m = menuList.get(position);
        v.name.setText(m.name);
        v.desc.setText(m.description);
        v.price.setText("$ " + m.price);
        Utilities.log("image = " +  m.image);
        if (!m.image.isEmpty()) {
            Picasso.with(context).load( m.image).into(v.image, new Callback() {
                @Override
                public void onSuccess() {
                        v.image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    v.image.setVisibility(View.GONE);
                }
            });
        }

        v.add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cart c = new Cart(m.id, m.name, m.image, m.description, m.price, "1");

                if (db.isExists(m.id)) {
                    db.updateCart(c);
                } else {
                    Utilities.log(c.item_id + "");
                    db.addToCart(c);
                }
                context.startActivity(new Intent(context, CartActivity.class));
                Utilities.toast(v.getContext(), "Cart updated name = " + c.title + ", quantity =  " + c.quantity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class ViewHolderMenuItem extends RecyclerView.ViewHolder {
        ImageView image, add_order;
        TextView desc;
        TextView name, price;

        public ViewHolderMenuItem(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            desc = (TextView) itemView.findViewById(R.id.description);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            add_order = (ImageView) itemView.findViewById(R.id.add_order);

        }
    }
}
