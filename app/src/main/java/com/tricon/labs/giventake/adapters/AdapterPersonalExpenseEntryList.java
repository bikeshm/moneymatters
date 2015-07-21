package com.tricon.labs.giventake.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.interfaces.EntryClickedListener;
import com.tricon.labs.giventake.interfaces.EntryLongClickedListener;
import com.tricon.labs.giventake.models.PersonalExpenseEntry;

import java.util.List;

public class AdapterPersonalExpenseEntryList extends RecyclerView.Adapter<AdapterPersonalExpenseEntryList.ViewHolder> {

    private List<PersonalExpenseEntry> mEntries;

    public AdapterPersonalExpenseEntryList(List<PersonalExpenseEntry> entries) {
        this.mEntries = entries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_personal_expense_entry_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonalExpenseEntry entry = mEntries.get(position);

        holder.tvDate.setText(entry.date);
        holder.tvDescription.setText(entry.description);
        holder.tvAmount.setText(entry.amount + "");
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvDate;
        TextView tvDescription;
        TextView tvAmount;
        LinearLayout llEntry;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);
            llEntry = (LinearLayout) itemView.findViewById(R.id.ll_entry);

            llEntry.setOnClickListener(this);
            llEntry.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_entry:
                    ((EntryClickedListener) v.getContext()).onEntryClicked(getAdapterPosition());
                    break;

                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.ll_entry:
                    ((EntryLongClickedListener) v.getContext()).onEntryLongClicked(getAdapterPosition());
                    break;

                default:
                    break;
            }
            return false;
        }
    }
}
