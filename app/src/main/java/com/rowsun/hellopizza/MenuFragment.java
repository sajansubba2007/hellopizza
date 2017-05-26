package com.rowsun.hellopizza;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rowsun.hellopizza.bases.http.MyDataQuery;
import com.rowsun.hellopizza.bases.http.OnDataReceived;
import com.rowsun.hellopizza.bases.utils.Pref;
import com.rowsun.hellopizza.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements OnDataReceived {
    RecyclerView rv_menu;
    MyDataQuery dq;
    List<Menu> menuList;
    AdapterMenuItem mAdapter;
    Pref pf;
    String type;

    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance(String type) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_menu, container, false);
        pf = new Pref(getActivity());
        dq = new MyDataQuery(getActivity(), this);
        menuList = new ArrayList<>();
        rv_menu = (RecyclerView) v.findViewById(R.id.rv_menu);
        rv_menu.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_menu.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAdapter = new AdapterMenuItem(getContext(),menuList);
        rv_menu.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(pf.containsKey(Pref.KEY_MENU_CACHE)){
            onSuccess("menu_list", pf.getPreferences(Pref.KEY_MENU_CACHE));
        }
        dq.getRequestData(Utilities.BASE_URL + "menus/list");
    }

    @Override
    public void onSuccess(String table_name, String result) {
        try {
            JSONObject object = new JSONObject(result);
            if(!result.isEmpty()){
                pf.setPreferences(Pref.KEY_MENU_CACHE, result);
            }
            menuList.clear();
            menuList.addAll(Menu.getMenuList(object.optJSONArray("menus")));
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
