package com.rowsun.hellopizza;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WbViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WbViewFragment extends Fragment {


    public WbViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WbViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    String url;
    public static WbViewFragment newInstance(String url ) {
        WbViewFragment fragment = new WbViewFragment();
        Bundle args = new Bundle();

        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wb_view, container, false);
        WebView wv = (WebView) v.findViewById(R.id.web_view);
        if(!TextUtils.isEmpty(url)){
            wv.loadUrl(url);
            wv.getSettings().setJavaScriptEnabled(true);
        }

      return v;
    }

}
