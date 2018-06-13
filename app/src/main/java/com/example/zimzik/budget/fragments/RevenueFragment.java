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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RevenueFragment extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = RevenueFragment.class.getSimpleName();
    public static final String DATEPICKER_TAG = "datepicker";
    private DatePickerDialog mDatePickerDialog;
    private Date mRevenueDate;
    private AppDB mDB;
    private TextView mTvTotalSumm;
    private RevenueListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // for dialog

    private EditText mEtDescription, mEtMoney;
    private TextView mTvDate;

    public RevenueFragment() {
    }

    public static RevenueFragment newInstance() {

        Bundle args = new Bundle();

        RevenueFragment fragment = new RevenueFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mRevenueDate = calendar.getTime();
        mTvDate.setText(new SimpleDateFormat("dd/MM/yyy").format(calendar.getTime()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        final Calendar calendar = Calendar.getInstance();
        mDatePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(setDialogView())
                .setMessage("Add new revenue")
                .setPositiveButton(R.string.save, (dialog, which) -> {})
                .setNegativeButton("Cancel", null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            mTvDate.setError(null);
            mEtDescription.setError(null);
            mEtMoney.setError(null);
            if (mTvDate.getText().toString().isEmpty() || mEtDescription.getText().toString().isEmpty() || mEtMoney.getText().toString().isEmpty() ) {
                if (mTvDate.getText().toString().isEmpty()) {
                    mTvDate.setError("This field is empty!");
                }
                if (mEtDescription.getText().toString().isEmpty()) {
                    mEtDescription.setError("This field is empty!");
                }
                if (mEtMoney.getText().toString().isEmpty()) {
                    mEtMoney.setError("This field is empty!");
                }
            } else {
                Revenue revenue = new Revenue(mRevenueDate.getTime(), mEtDescription.getText().toString(), Integer.valueOf(mEtMoney.getText().toString()));
                mDB.getRevenueRepo().insertRevenue(revenue)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Toast.makeText(getContext(), "Revenue successfully saved!", Toast.LENGTH_LONG).show();
                            refreshTable();
                        });
                dialog.dismiss();
            }
        });
    }

    private View setDialogView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_new_revenue, null);
        mTvDate = view.findViewById(R.id.tv_rev_date);
        mEtDescription = view.findViewById(R.id.et_rev_descr);
        mEtMoney = view.findViewById(R.id.et_rev_money);

        mTvDate.setOnClickListener(v -> {
            mDatePickerDialog.setVibrate(isVibrate());
            mDatePickerDialog.setYearRange(1985, 2028);
            mDatePickerDialog.setCloseOnSingleTapDay(false);
            mDatePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
        });
        return view;
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
