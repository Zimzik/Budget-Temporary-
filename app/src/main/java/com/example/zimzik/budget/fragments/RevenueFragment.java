package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.RevenueListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Revenue;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RevenueFragment extends android.support.v4.app.Fragment {

    private AppDB mDB;
    private TextView mTvTotalSumm;
    private RevenueListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private AddRevenueDialogFragment mAddRevenueDialogFragment;

    public RevenueFragment() {
    }

    public static RevenueFragment newInstance() {

        Bundle args = new Bundle();

        RevenueFragment fragment = new RevenueFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        mAddRevenueDialogFragment = AddRevenueDialogFragment.newInstance(revenue -> mDB.getRevenueRepo().insertRevenue(revenue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), "Revenue successfully saved!", Toast.LENGTH_LONG).show();
                    refreshTable();
                }));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_revenues, container, false);
        view.findViewById(R.id.add_money_btn_rv).setOnClickListener(v -> addMoneyButtonClick());
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ_rv);
        mRecyclerView = view.findViewById(R.id.rv_revenues);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable();
        return view;
    }

    @SuppressLint("CheckResult")
    private void addMoneyButtonClick() {
        mAddRevenueDialogFragment.show(getFragmentManager(), "AddRevenueDialog");
    }

    @SuppressLint("CheckResult")
    private void refreshTable() {
        mDB.getRevenueRepo().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(revenues -> {
                    Collections.sort(revenues, (r1, r2) -> {
                        Long r1Time = r1.getDate();
                        Long r2Time = r2.getDate();
                        if (r2Time > r1Time) return 1;
                        else if (r2Time == r1Time) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Revenue r : revenues) {
                        summ += r.getSumm();
                    }
                    String s = String.format("Total: %d", summ);
                    mTvTotalSumm.setText(s);
                    mAdapter = new RevenueListAdapter(revenues, revenue -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure to delete this period?");
                        builder.setPositiveButton("Delete", (dialog, which) -> deleteRevenueFromDB(revenue));
                        builder.setNegativeButton("Cancel", (dialog, which) -> {

                        });
                        builder.setCancelable(true);
                        builder.show();
                    });
                    mRecyclerView.setAdapter(mAdapter);

                });
    }

    @SuppressLint("CheckResult")
    private void deleteRevenueFromDB(Revenue revenue) {
        mDB.getRevenueRepo().delete(revenue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), "Revenue successfully deleted!", Toast.LENGTH_LONG).show();
                    refreshTable();
                });
    }

    private Boolean isVibrate() {
        return true;
    }
}
