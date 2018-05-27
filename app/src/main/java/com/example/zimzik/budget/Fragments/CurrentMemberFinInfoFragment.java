package com.example.zimzik.budget.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.budget.Activities.AddNewMembershipFree;
import com.example.zimzik.budget.Adapters.FinancialListAdapter;
import com.example.zimzik.budget.Database.AppDB;
import com.example.zimzik.budget.Database.Member;
import com.example.zimzik.budget.Database.Period;
import com.google.gson.Gson;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.example.zimzik.budget.R;

public class CurrentMemberFinInfoFragment extends Fragment {

    private Member mMember;
    private static final String KEY_MEMBER = "KEY_MEMBER";
    private final Gson mGson = new Gson();
    private AppDB mDB;
    private FinancialListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTvTotalSumm;

    public CurrentMemberFinInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMember = mGson.fromJson(getArguments().getString(KEY_MEMBER), Member.class);
        }
    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_member_fin_info, container, false);
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ);
        view.findViewById(R.id.add_money_btn).setOnClickListener(v -> addMoneyButtonClick());
        mDB = AppDB.getsInstance(getContext());
        mRecyclerView = view.findViewById(R.id.rv_periods);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable(mRecyclerView);
        return view;
    }

    @Override
    public void onResume() {
        refreshTable(mRecyclerView);
        super.onResume();
    }

    public static CurrentMemberFinInfoFragment newInstance(Member member) {
        CurrentMemberFinInfoFragment fragment = new CurrentMemberFinInfoFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(KEY_MEMBER, gson.toJson(member));
        fragment.setArguments(bundle);
        return fragment;
    }

    private void addMoneyButtonClick() {
        Intent intent = new Intent(getActivity(), AddNewMembershipFree.class);
        String myJson = mGson.toJson(mMember);
        intent.putExtra(KEY_MEMBER, myJson);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void refreshTable(RecyclerView rv) {
        mDB.getPeriodRepo().selectById(mMember.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(periods -> {
                    Collections.sort(periods, (p1, p2) -> {
                        if (p1.getYear() > p2.getYear() || p1.getYear() == p2.getYear() && p1.getMonthNum() > p2.getMonthNum()) return 1;
                        else if (p1.getYear() == p2.getYear() && p1.getMonthNum() == p2.getMonthNum()) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Period p : periods) {
                        summ += p.getMoney();
                    }
                    String s = String.format("Total: %d", summ);
                    mTvTotalSumm.setText(s);
                    mAdapter = new FinancialListAdapter(periods);
                    rv.setAdapter(mAdapter);
                });
    }
}
