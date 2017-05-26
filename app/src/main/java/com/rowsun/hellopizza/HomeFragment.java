package com.rowsun.hellopizza;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rowsun.hellopizza.bases.utils.Pref;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delivery:
                if (listener != null) {
                    new Pref(getActivity()).setPreferences("type", "delivery");
                    listener.open(MenuFragment.newInstance("delivery"));

                }
                break;
            case R.id.pickup:
                if (listener != null) {
                    listener.open(MenuFragment.newInstance("pickup"));
                    new Pref(getActivity()).setPreferences("type", "pickup");

                }
                break;

//            case R.id.save:
//                String url = ((EditText) getView().findViewById(R.id.et_base_url)).getText().toString();
//                new Pref(getActivity()).setPreferences("url", url);
//                break;
        }
    }

    interface FragmentInteractListener {
        void open(Fragment f);
    }

    FragmentInteractListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentInteractListener) context;
    }

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        v.findViewById(R.id.delivery).setOnClickListener(this);
        v.findViewById(R.id.pickup).setOnClickListener(this);
        return v;
    }

}
