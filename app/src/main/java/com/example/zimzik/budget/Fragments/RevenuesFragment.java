package com.example.zimzik.budget.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zimzik.budget.R;

public class RevenuesFragment extends Fragment {
   

    public RevenuesFragment() {
        // Required empty public constructor
    }

    public static RevenuesFragment newInstance() {
        
        Bundle args = new Bundle();
        
        RevenuesFragment fragment = new RevenuesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revenues, container, false);
    }
    
}
