package com.example.zimzik.budget.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Divergence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DivergenceListAdapter extends RecyclerView.Adapter<DivergenceListAdapter.ViewHolder> {

    private List<Divergence> mDivergences;
    private FinancialListAdapter.LongClick<Divergence> mLongClick;

    public DivergenceListAdapter(List<Divergence> divergences, FinancialListAdapter.LongClick<Divergence> longClick) {
        mDivergences = divergences;
        this.mLongClick = longClick;
    }

    @NonNull
    @Override
    public DivergenceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.divergence_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Divergence divergence = mDivergences.get(position);
        holder.date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(divergence.getDate())));
        holder.description.setText(divergence.getDescription());
        holder.money.setText(String.valueOf(divergence.getSumm()));
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), holder.itemView);
            menu.inflate(R.menu.period_list_context_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cp_delete) {
                    mLongClick.call(divergence);
                }
                return true;
            });
            menu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mDivergences.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date, description, money;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tv_divergence_date);
            description = view.findViewById(R.id.tv_divergence_description);
            money = view.findViewById(R.id.tv_divergence_money);
        }
    }
}
