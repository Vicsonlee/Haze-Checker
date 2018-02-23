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
 * Fragment for PSI, displays PSI readings for all regions
 */
public class PsiFragment extends Fragment {

    private static final String ARG_PSI = "psi_frag";

    private Map<String,String> psiValues;

    public PsiFragment() {
        // Required empty public constructor
    }

    public static PsiFragment newInstance(Map<String,String> psi) {
        PsiFragment fragment = new PsiFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PSI, (Serializable) psi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            psiValues = (Map<String, String>) getArguments().getSerializable(ARG_PSI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_psi, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (psiValues != null) {
            TextView central = getView().findViewById(R.id.psi_central);
            TextView north = getView().findViewById(R.id.psi_north);
            TextView south = getView().findViewById(R.id.psi_south);
            TextView east = getView().findViewById(R.id.psi_east);
            TextView west = getView().findViewById(R.id.psi_west);

            central.setText(psiValues.get("central"));
            north.setText(psiValues.get("north"));
            south.setText(psiValues.get("south"));
            east.setText(psiValues.get("east"));
            west.setText(psiValues.get("west"));
        }

    }

}
