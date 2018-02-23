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
 * Fragment for Home, displays national PSI/PM25 averages
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PSI = "home_psi";
    private static final String ARG_PM25 = "home_pm25";

    private Map<String,String> psiValues;
    private Map<String,String> pm25Values;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(Map<String,String> psi, Map<String,String> pm25) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PSI, (Serializable) psi);
        args.putSerializable(ARG_PM25, (Serializable) pm25);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            psiValues = (Map<String, String>) getArguments().getSerializable(ARG_PSI);
            pm25Values = (Map<String, String>) getArguments().getSerializable(ARG_PM25);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (psiValues != null) {
            TextView psi = getView().findViewById(R.id.home_psi_value);
            psi.setText(psiValues.get("national"));
        }

        if (pm25Values != null) {
            TextView pm25 = getView().findViewById(R.id.home_pm25_value);
            pm25.setText(pm25Values.get("national"));
        }

    }

}
