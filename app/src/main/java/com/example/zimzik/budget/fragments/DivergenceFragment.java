package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.DivergenceListAdapter;
import com.example.zimzik.budget.adapters.RevenueListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Divergence;
import com.example.zimzik.budget.data.db.models.Revenue;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DivergenceFragment extends Fragment {

    private AppDB mDB;
    private TextView mTvTotalSumm;
    private DivergenceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private AddDivergenceDialogFragment mAddDivergenceDialogFragment;
    public DivergenceFragment() {
        // Required empty public constructor
    }

    public static DivergenceFragment newInstance() {

        Bundle args = new Bundle();

        DivergenceFragment fragment = new DivergenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        mAddDivergenceDialogFragment = AddDivergenceDialogFragment.newInstance(divergence -> {
            mDB.getDivergenceRepo().insertDivergence(divergence)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(getContext(), "Divergence successfully saved!", Toast.LENGTH_LONG).show();
                        refreshTable();
                    });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_divergence, container, false);
        view.findViewById(R.id.add_money_btn_div).setOnClickListener(v -> addMoneyButtonClick());
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ_div);
        mRecyclerView = view.findViewById(R.id.rv_divergence);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable();
        return view;
    }

    @SuppressLint("CheckResult")
    private void addMoneyButtonClick() {
        mAddDivergenceDialogFragment.show(getFragmentManager(), "AddDivergence");
    }

    @SuppressLint("CheckResult")
    private void refreshTable() {
        mDB.getDivergenceRepo().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(divergences -> {
                    Collections.sort(divergences, (r1, r2) -> {
                        Long r1Time = r1.getDate();
                        Long r2Time = r2.getDate();
                        if (r2Time > r1Time) return 1;
                        else if (r2Time == r1Time) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Divergence r : divergences) {
                        summ += r.getSumm();
                    }
                    String s = String.format("Total: %d", summ);
                    mTvTotalSumm.setText(s);
                    mAdapter = new DivergenceListAdapter(divergences, divergence -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure to delete this period?");
                        builder.setPositiveButton("Delete", (dialog, which) -> deleteDivergenceFromDb(divergence));
                        builder.setNegativeButton("Cancel", (dialog, which) -> {

                        });
                        builder.setCancelable(true);
                        builder.show();
                    });
                    mRecyclerView.setAdapter(mAdapter);

                });
    }

    @SuppressLint("CheckResult")
    private void deleteDivergenceFromDb(Divergence divergence) {
        mDB.getDivergenceRepo().delete(divergence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), "Divergence successfully deleted!", Toast.LENGTH_LONG).show();
                    refreshTable();
                });
    }


}
