package com.example.zimzik.budget.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Divergence;
import com.example.zimzik.budget.data.db.models.Revenue;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDivergenceDialogFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog mDatePickerDialog;

    private EditText mEtDescription, mEtMoney;
    private TextView mTvDate;

    private Date mDivergenceDate;
    private static SaveDivergence<Divergence> sSaveDivergence;

    public static final String DATEPICKER_TAG = "datepicker";


    public static AddDivergenceDialogFragment newInstance(SaveDivergence<Divergence> saveDivergence) {
        sSaveDivergence = saveDivergence;

        Bundle args = new Bundle();

        AddDivergenceDialogFragment fragment = new AddDivergenceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        mDatePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Add new divergence");
        View view = inflater.inflate(R.layout.dialog_add_new_divergence, null);

        mTvDate = view.findViewById(R.id.tv_div_date);
        mEtDescription = view.findViewById(R.id.et_div_descr);
        mEtMoney = view.findViewById(R.id.et_div_money);

        mTvDate.setOnClickListener(v -> {
            mDatePickerDialog.setVibrate(isVibrate());
            mDatePickerDialog.setYearRange(1985, 2028);
            mDatePickerDialog.setCloseOnSingleTapDay(false);
            mDatePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
        });

        view.findViewById(R.id.save_divergence_btn).setOnClickListener(v -> onSaveButtonClick());
        view.findViewById(R.id.cancel_new_divergence_btn).setOnClickListener(v -> onCancelButtonClick());


        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mDivergenceDate = calendar.getTime();
        mTvDate.setText(new SimpleDateFormat("dd/MM/yyy").format(calendar.getTime()));
    }

    private void onSaveButtonClick() {
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
            Divergence divergence = new Divergence(mDivergenceDate.getTime(), mEtDescription.getText().toString(), Integer.valueOf(mEtMoney.getText().toString()));
            sSaveDivergence.call(divergence);
            mTvDate.setText("Date");
            mEtDescription.setText("");
            mEtMoney.setText("");
            getDialog().dismiss();
        }
    }

    private void onCancelButtonClick() {
        getDialog().dismiss();
    }

    private Boolean isVibrate() {
        return true;
    }
    public interface SaveDivergence<T> {
        void call(T object);
    }
}
