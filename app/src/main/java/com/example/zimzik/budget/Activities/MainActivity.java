package com.example.zimzik.budget.Activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.zimzik.budget.Fragments.DivergenceFragment;
import com.example.zimzik.budget.Fragments.HomeFragment;
import com.example.zimzik.budget.Fragments.MemberListFragment;
import com.example.zimzik.budget.Fragments.RevenuesFragment;
import com.example.zimzik.budget.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setActionBarTitle("Budget");
                        setFragment(HomeFragment.newInstance(), ft);
                        return true;
                    case R.id.navigation_member_list:
                        setActionBarTitle("Chorus member list");
                        setFragment(MemberListFragment.newInstance(), ft);
                        return true;
                    case R.id.navigation_revenues:
                        setActionBarTitle("Revenues");
                        setFragment(RevenuesFragment.newInstance(), ft);
                        return true;
                    case R.id.navigation_divergence:
                        setActionBarTitle("Divergence");
                        setFragment(DivergenceFragment.newInstance(), ft);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarTitle("Budget");
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        setFragment(HomeFragment.newInstance(), ft);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setActionBarTitle(String s) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(s);
        }
    }

    private void setFragment(Fragment fragment, FragmentTransaction ft) {
        ft.replace(R.id.fr_holder, fragment);
        ft.commit();
    }

}
