package com.example.zimzik.budget.Activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import com.example.zimzik.budget.Fragments.MemberListFragment;
import com.example.zimzik.budget.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setActionBar("Budget");
                        return true;
                    case R.id.navigation_member_list:
                        setActionBar("Chorus member list");
                        MemberListFragment memberListFragment = MemberListFragment.newInstance();
                        ft.replace(R.id.viewpager, memberListFragment);
                        ft.commit();
                        return true;
                    case R.id.navigation_revenues:
                        setActionBar("Revenues");
                        return true;
                    case R.id.navigation_divergence:
                        setActionBar("Divergence");
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBar("Budget");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setActionBar(String s) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(s);
        }
    }

}
