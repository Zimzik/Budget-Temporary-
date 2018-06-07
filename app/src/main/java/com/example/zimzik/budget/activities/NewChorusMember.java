package com.example.zimzik.budget.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.R;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewChorusMember extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView mTvBirthday;
    public static final String DATEPICKER_TAG = "datepicker";
    private Date mBirthday;
    private AppDB mDB;
    private EditText mEtFirstName, mEtSecondName, mEtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chorus_member);
        Context that = this;
        //Init EditTexts
        mEtFirstName = findViewById(R.id.et_first_name);
        mEtSecondName = findViewById(R.id.et_second_name);
        mEtPhoneNumber = findViewById(R.id.et_phone_number);

        //Init DB
        mDB = AppDB.getsInstance(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New member");
        }

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });

        // Save button implementation
        findViewById(R.id.save_new_member_btn).setOnClickListener(v -> {
            String firstName = mEtFirstName.getText().toString();
            String secondName = mEtSecondName.getText().toString();
            String stringBirthday = mTvBirthday.getText().toString();
            String strintPhoneNumber = mEtPhoneNumber.getText().toString();
            mEtFirstName.setError(null);
            mEtSecondName.setError(null);
            mTvBirthday.setError(null);
            mEtPhoneNumber.setError(null);

            if (firstName.isEmpty() || secondName.isEmpty() || stringBirthday.equals("Birthday") || strintPhoneNumber.isEmpty()) {
                if (firstName.isEmpty()) {
                    mEtFirstName.setError("This field is empty!");
                }
                if (secondName.isEmpty()) {
                    mEtSecondName.setError("This field is empty!");
                }
                if (stringBirthday.equals("Birthday")) {
                    mTvBirthday.setError("This field is empty");
                }
                if (strintPhoneNumber.isEmpty()) {
                    mEtPhoneNumber.setError("This field is empty");
                }
            } else {
                //Create new thread to save data on DB
                Thread insert = new Thread(() -> mDB.memberDao().insertAll(new Member(firstName, secondName, mBirthday.getTime(), Long.valueOf(strintPhoneNumber))));
                insert.start();
                try {
                    insert.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Successfully data saved message
                Toast.makeText(that, "New member succesfully saved on DB", Toast.LENGTH_LONG).show();

                // Clear all fileds and errors
                mEtFirstName.setError(null);
                mEtSecondName.setError(null);
                mTvBirthday.setError(null);
                mEtPhoneNumber.setError(null);
                mEtFirstName.setText("");
                mEtSecondName.setText("");
                mTvBirthday.setText(R.string.birthday);
                mEtPhoneNumber.setText("");
            }
        });
    }

    private boolean isVibrate() {
        return true;
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mBirthday = calendar.getTime();
        mTvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(mBirthday));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
