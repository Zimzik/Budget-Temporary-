package com.example.zimzik.budget.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.zimzik.budget.Activities.CurrentMember;
import com.example.zimzik.budget.Activities.NewChorusMember;
import com.example.zimzik.budget.Adapters.MemberListAdapter;
import com.example.zimzik.budget.Database.AppDB;
import com.example.zimzik.budget.Database.Member;
import com.example.zimzik.budget.R;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MemberListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MemberListAdapter mMemberListAdapter;
    private RecyclerView mRecyclerView;
    private AppDB mDB;


    public MemberListFragment() {
    }

    public static MemberListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MemberListFragment fragment = new MemberListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = view.findViewById(R.id.rv_members);
        mMemberListAdapter = refreshList();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.member_list_menu, menu);
        MenuItem item = menu.findItem(R.id.search_member);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mMemberListAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_new_member) {
            startActivity(new Intent(getContext(), NewChorusMember.class));
        }
        return super.onOptionsItemSelected(item);
    }

    // return refreshed list adapter
    @SuppressLint("CheckResult")
    private MemberListAdapter refreshList() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<Member>> future = executorService.submit(() -> mDB.memberDao().getAllMembers());
        List<Member> memberList = null;
        try {
            memberList = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        Collections.sort(memberList, (m1, m2) -> m1.toString().compareToIgnoreCase(m2.toString()));

        MemberListAdapter listAdapter = new MemberListAdapter(memberList, m -> {
            Intent intent = new Intent(getActivity(), CurrentMember.class);
            Gson gson = new Gson();
            String myJson = gson.toJson(m);
            intent.putExtra("member", myJson);
            startActivity(intent);
        }, m -> deleteMemberFromDB(m));
        mRecyclerView.setAdapter(listAdapter);
        return listAdapter;
    }

    // delete member from db method
    private void deleteMemberFromDB(Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(String.format("Are you shure to delete member %s %s from DB?", m.getLastName(), m.getFirstName()));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
            mDB.getMemberRepo().delete(m)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    // calculate age
    public static int calculateAge(long birthday) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTime(new Date(birthday));
        dob.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age;
    }

    @Override
    public void onRefresh() {
        refreshList();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMemberListAdapter = refreshList();
    }
}
