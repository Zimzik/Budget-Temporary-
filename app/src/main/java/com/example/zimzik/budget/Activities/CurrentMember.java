package com.example.zimzik.budget.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zimzik.budget.Adapters.ViewPagerAdapter;
import com.example.zimzik.budget.Database.AppDB;
import com.example.zimzik.budget.Database.Member;
import com.example.zimzik.budget.Fragments.CurrentMemberFinInfoFragment;
import com.example.zimzik.budget.Fragments.CurrentMemberInfoFragment;
import com.example.zimzik.budget.R;
import com.google.gson.Gson;

public class CurrentMember extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private Member mMember;
    private AppDB mDB;
    private Gson mGson = new Gson();
    private final String KEY_MEMBER = "member";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_member);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Member");
        }

        mDB = AppDB.getsInstance(this);
        mMember = mGson.fromJson(getIntent().getStringExtra(KEY_MEMBER), Member.class);

        mViewPager = findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        CurrentMemberFinInfoFragment memberFinInfoFragment = CurrentMemberFinInfoFragment.newInstance(mMember);
        CurrentMemberInfoFragment memberInfoFragment = CurrentMemberInfoFragment.newInstance(mMember);

        mAdapter.addFragment(memberFinInfoFragment, "Financial info");
        mAdapter.addFragment(memberInfoFragment, "Member info");
        viewPager.setAdapter(mAdapter);
    }

    private void onDeleteMemberClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Are you shure to delete mMember %s %s from DB?", mMember.getLastName(), mMember.getFirstName()));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
            Thread deleteMember = new Thread(() -> mDB.memberDao().delete(mMember));
            deleteMember.start();
            try {
                deleteMember.join();
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cm_menu_edit) {
            Intent intent = new Intent(this, EditChorusMember.class);
            intent.putExtra(KEY_MEMBER, mGson.toJson(mMember));
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.cm_menu_delete) {
            onDeleteMemberClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            mMember = mGson.fromJson(data.getStringExtra(KEY_MEMBER), Member.class);
            Fragment fragment = mAdapter.getItem(1);
        }
    }
}
