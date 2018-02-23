package com.assignment.hazechecker.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assignment.hazechecker.R;

import java.io.Serializable;
import java.util.Map;

/**
 * Fragment for PM25, displays PM25 readings for all regions
 */
public class Pm25Fragment extends Fragment {
    private static final String ARG_PM25 = "pm25_frag";

    private Map<String,String> pm25Values;

    public Pm25Fragment() {
        // Required empty public constructor
    }

    public static Pm25Fragment newInstance(Map<String,String> pm25) {
        Pm25Fragment fragment = new Pm25Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PM25, (Serializable) pm25);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pm25Values = (Map<String, String>) getArguments().getSerializable(ARG_PM25);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pm25, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (pm25Values != null) {
            TextView central = getView().findViewById(R.id.pm25_central);
            TextView north = getView().findViewById(R.id.pm25_north);
            TextView south = getView().findViewById(R.id.pm25_south);
            TextView east = getView().findViewById(R.id.pm25_east);
            TextView west = getView().findViewById(R.id.pm25_west);

            central.setText(pm25Values.get("central"));
            north.setText(pm25Values.get("north"));
            south.setText(pm25Values.get("south"));
            east.setText(pm25Values.get("east"));
            west.setText(pm25Values.get("west"));
        }

    }

}
