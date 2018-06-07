package com.example.zimzik.budget.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.example.zimzik.budget.R;

public class EditChorusMember extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText mEtFirstName, mEtSecondName, mEtPhoneNumber;
    private TextView mTvBirthday;
    private Member mMember;
    private AppDB mDB;
    private Date mBirthday;
    public static final String DATEPICKER_TAG = "datepicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chorus_member);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edit member");
        }
        mDB = AppDB.getsInstance(this);
        mEtFirstName = findViewById(R.id.et_em_first_name);
        mEtSecondName = findViewById(R.id.et_em_second_name);
        mEtPhoneNumber = findViewById(R.id.et_em_phone_number);
        mTvBirthday = findViewById(R.id.tv_em_birthday);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        mTvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });

        Gson gson = new Gson();
        mMember = gson.fromJson(getIntent().getStringExtra("member"), Member.class);
        mBirthday = new Date(mMember.getBirthday());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
        mTvBirthday.setText(sdf.format(mBirthday));
        mEtFirstName.setText(mMember.getFirstName());
        mEtSecondName.setText(mMember.getLastName());
        mEtPhoneNumber.setText(String.valueOf(mMember.getPhoneNumber()));

        findViewById(R.id.save_changes_em_btn).setOnClickListener(v -> {
            String firstName = mEtFirstName.getText().toString();
            String secondName = mEtSecondName.getText().toString();
            String stringPhoneNumber = mEtPhoneNumber.getText().toString();
            mEtFirstName.setError(null);
            mEtSecondName.setError(null);
            mEtPhoneNumber.setError(null);

            if (firstName.isEmpty() || secondName.isEmpty() || stringPhoneNumber.isEmpty()) {
                if (firstName.isEmpty()) {
                    mEtFirstName.setError("This field is empty!");
                }
                if (secondName.isEmpty()) {
                    mEtSecondName.setError("This field is empty!");
                }
                if (stringPhoneNumber.isEmpty()) {
                    mEtPhoneNumber.setError("This field is empty");
                }
            } else {
                mMember.setFirstName(firstName);
                mMember.setLastName(secondName);
                mMember.setPhoneNumber(Long.valueOf(stringPhoneNumber));
                mMember.setBirthday(mBirthday.getTime());
                Thread applyChanges = new Thread(() -> {
                    mDB.memberDao().update(mMember);
                });
                applyChanges.start();
                try {
                    applyChanges.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                String newMember = gson.toJson(mMember);
                intent.putExtra("mMember", newMember);
                setResult(RESULT_OK, intent);
                Toast.makeText(this, "Information has successfully updated!", Toast.LENGTH_LONG).show();
                finish();
            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mBirthday = calendar.getTime();
        mTvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(mBirthday));
    }

    private boolean isVibrate() {
        return true;
    }
}