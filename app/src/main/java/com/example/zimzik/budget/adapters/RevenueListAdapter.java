package com.example.zimzik.budget.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Revenue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RevenueListAdapter extends RecyclerView.Adapter<RevenueListAdapter.ViewHolder> {
    private List<Revenue> mRevenues;
    private FinancialListAdapter.LongClick<Revenue> mLongClick;

    public RevenueListAdapter(List<Revenue> revenues, FinancialListAdapter.LongClick<Revenue> longClick) {
        mRevenues = revenues;
        this.mLongClick = longClick;
    }

    @NonNull
    @Override
    public RevenueListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.revenues_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Revenue revenue = mRevenues.get(position);
        holder.date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(revenue.getDate())));
        holder.description.setText(revenue.getDescription());
        holder.money.setText(String.valueOf(revenue.getSumm()));
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), holder.itemView);
            menu.inflate(R.menu.period_list_context_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cp_delete) {
                    mLongClick.call(revenue);
                }
                return true;
            });
            menu.show();
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return mRevenues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date, description, money;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tv_revenues_date);
            description = view.findViewById(R.id.tv_revenues_description);
            money = view.findViewById(R.id.tv_revenues_money);
        }
    }
}
