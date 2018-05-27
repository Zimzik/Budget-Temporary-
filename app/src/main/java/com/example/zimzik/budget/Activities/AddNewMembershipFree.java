package com.example.zimzik.budget.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zimzik.budget.Database.AppDB;
import com.example.zimzik.budget.Database.Member;
import com.example.zimzik.budget.Database.Period;
import com.google.gson.Gson;
import com.example.zimzik.budget.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddNewMembershipFree extends AppCompatActivity {
    private static final String TAG = AddNewMembershipFree.class.getSimpleName();
    private static final String KEY_MEMBER = "KEY_MEMBER";
    private int mMonthNum;
    private int mYear;
    private int mMoney;
    private Spinner mMonthSpinner, mYearsSpinner;
    private EditText mEtMoney;
    private Context mContext = this;
    private Gson mGson = new Gson();
    private Member mMember;
    private AppDB mDb;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_membership_free);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Money");
        }

        // get DB instance
        mDb = AppDB.getsInstance(this);

        //get Member
        mMember = mGson.fromJson(getIntent().getStringExtra(KEY_MEMBER), Member.class);

        // get spinners of month and mYear and mMoney EditText
        mMonthSpinner = findViewById(R.id.months_spinner);
        mYearsSpinner = findViewById(R.id.years_spinner);
        mEtMoney = findViewById(R.id.et_money);

        // adapters for spinners
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> yearsAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, android.R.layout.simple_spinner_item);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMonthSpinner.setAdapter(monthAdapter);
        mYearsSpinner.setAdapter(yearsAdapter);

        // get number  of month
        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMonthNum = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // get mYear
        mYearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYear = Integer.valueOf(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Save mMoney button handler
        findViewById(R.id.save_money_btn).setOnClickListener(view -> {
            if (mEtMoney.getText().toString().isEmpty()) {
                mEtMoney.setError("This field is empty!");
            } else {
                mMoney = Integer.valueOf(mEtMoney.getText().toString());
                Period period = new Period(mYear, mMonthNum, mMoney, mMember.getUid());
                mDb.getPeriodRepo().insertMonth(period)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Toast.makeText(mContext, "Information successfully saved on DB", Toast.LENGTH_LONG).show();
                            mEtMoney.setText("");
                        }, __ -> ignoreOrUpdate(period));
            }

        });

        findViewById(R.id.show_all_money_btn).setOnClickListener(view -> mDb.getPeriodRepo()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(periods -> {
                    for (Period p : periods) {
                        Log.i("Ololo", String.format("From DB: id: %d, month: %d, mYear: %d, mMoney: %d", p.getMemberId(), p.getMonthNum(), p.getYear(), p.getMoney()));
                    }
                }));

        findViewById(R.id.delete_all_money_btn).setOnClickListener(view -> mDb.getPeriodRepo()
                .deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Toast.makeText(mContext, "All records deleted successfully!", Toast.LENGTH_LONG).show(),
                        throwable -> Log.i(TAG, "Delete error", throwable)));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void ignoreOrUpdate(Period period) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Information for the current month is present in the DB. Do you want to update this?");
        builder.setPositiveButton("Update", (dialogInterface, i) -> mDb.getPeriodRepo()
                .updateMonth(period)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(mContext, "Information successfully updated!", Toast.LENGTH_LONG).show();
                    mEtMoney.setText("");
                }));


        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }
}
