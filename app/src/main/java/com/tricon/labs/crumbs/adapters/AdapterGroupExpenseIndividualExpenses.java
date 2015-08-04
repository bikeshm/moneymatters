package com.tricon.labs.crumbs.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.interfaces.EntryClickedListener;
import com.tricon.labs.crumbs.interfaces.EntryLongClickedListener;
import com.tricon.labs.crumbs.models.GroupExpensesEntry;
import com.tricon.labs.crumbs.models.Person;

import java.util.List;

public class AdapterGroupExpenseIndividualExpenses extends RecyclerView.Adapter<AdapterGroupExpenseIndividualExpenses.ViewHolder> {

    private List<GroupExpensesEntry> mEntries;


    public AdapterGroupExpenseIndividualExpenses(List<GroupExpensesEntry> entries) {
        this.mEntries = entries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_group_expense_individual_expense_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupExpensesEntry entry = mEntries.get(position);

        holder.tvExpenseDate.setText(entry.expenseDate);
        holder.tvDescription.setText(entry.description );
        holder.tvSpentBy.setText(entry.spentBy.name);
        holder.tvPhone.setText( "( "+entry.spentBy.phone+" )" );
        holder.tvAmount.setText(entry.amount + "");
    }


    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvExpenseDate;
        TextView tvDescription;
        TextView tvSpentBy;
        TextView tvAmount;
        TextView tvPhone;
        LinearLayout llExpenseRow;



        public ViewHolder(View itemView) {
            super(itemView);

            tvExpenseDate= (TextView) itemView.findViewById(R.id.tv_date);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvSpentBy = (TextView) itemView.findViewById(R.id.tv_spent_by);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);


            llExpenseRow = (LinearLayout) itemView.findViewById(R.id.ll_expense_row);

            llExpenseRow.setOnClickListener(this);
            llExpenseRow.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_expense_row:
                    ((EntryClickedListener) v.getContext()).onEntryClicked(getAdapterPosition());
                    break;

                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.ll_expense_row:
                    ((EntryLongClickedListener) v.getContext()).onEntryLongClicked(getAdapterPosition());
                    break;

                default:
                    break;
            }
            return false;
        }
    }
}
