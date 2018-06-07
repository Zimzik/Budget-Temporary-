package com.example.zimzik.budget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.budget.data.db.models.Period;
import com.example.zimzik.budget.enums.Months;

import java.util.List;

import com.example.zimzik.budget.R;

public class FinancialListAdapter extends RecyclerView.Adapter<FinancialListAdapter.ViewHolder> {
    private List<Period> mPeriods;

    public FinancialListAdapter(List<Period> periods) {
        mPeriods = periods;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.periods_list_item_view, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FinancialListAdapter.ViewHolder holder, int position) {
        final Period period = mPeriods.get(position);
        holder.year.setText(String.valueOf(period.getYear()));
        holder.month.setText(Months.values()[period.getMonthNum()].toString());
        holder.money.setText(String.valueOf(period.getMoney()));
    }

    @Override
    public int getItemCount() {
        return mPeriods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView year, month, money;

        ViewHolder(View view) {
            super(view);
            year = view.findViewById(R.id.tv_period_year);
            month = view.findViewById(R.id.tv_period_month);
            money = view.findViewById(R.id.tv_period_money);
        }
    }
}