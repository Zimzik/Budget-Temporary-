package com.example.zimzik.budget.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.budget.Database.Member;
import com.example.zimzik.budget.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentMemberInfoFragment extends android.support.v4.app.Fragment {

    private Member mMember;
    private TextView mTextView;
    private TextView mTvAge;
    private TextView mTvPhoneNumber;
    private TextView mTvCash;
    private final Gson mGson = new Gson();
    private static final String KEY_MEMBER = "KEY_MEMBER";

    public CurrentMemberInfoFragment() {
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMember = mGson.fromJson(getArguments().getString(KEY_MEMBER), Member.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_member_info, container, false);
        mTextView = view.findViewById(R.id.cm_tv_name);
        mTvAge = view.findViewById(R.id.cm_tv_age);
        mTvPhoneNumber = view.findViewById(R.id.cm_tv_phone_number);
        mTvCash = view.findViewById(R.id.cm_tv_cash);
        setAllFields(mMember);
        return view;
    }

    private void setAllFields(Member member) {
        String name = member.getLastName() + " " + member.getFirstName();
        String age = String.format("%s (%d)", new SimpleDateFormat("dd.MM.yyyy").format(new Date(member.getBirthday())).toString(), MemberListFragment.calculateAge(member.getBirthday()));
        long phoneNumber = member.getPhoneNumber();
        mTextView.setText(name);
        mTvAge.setText(age);
        mTvPhoneNumber.setText(String.valueOf(phoneNumber));
        mTvCash.setText("0 ₴");
    }

    public static CurrentMemberInfoFragment newInstance(Member member) {
        CurrentMemberInfoFragment fragment = new CurrentMemberInfoFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(KEY_MEMBER, gson.toJson(member));
        fragment.setArguments(bundle);
        return fragment;
    }
}
