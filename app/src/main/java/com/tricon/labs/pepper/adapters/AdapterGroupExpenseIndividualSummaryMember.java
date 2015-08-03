package com.tricon.labs.pepper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.interfaces.EntryClickedListener;
import com.tricon.labs.pepper.interfaces.EntryLongClickedListener;
import com.tricon.labs.pepper.models.Member;
import com.tricon.labs.pepper.models.Person;

import java.util.List;

public class AdapterGroupExpenseIndividualSummaryMember extends RecyclerView.Adapter<AdapterGroupExpenseIndividualSummaryMember.ViewHolder> {

    private List<Member> mEntries;


    public AdapterGroupExpenseIndividualSummaryMember(List<Member> entries) {
        this.mEntries = entries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_group_expense_individual_summary_member_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Member entry = mEntries.get(position);

        holder.tvName.setText(entry.name);
        holder.tvAmountSpent.setText(entry.amountSpent + "");
        holder.tvAmountBalance.setText(entry.amountBalance + "");

        if (entry.status == Person.STATUS_GIVE) {
            holder.tvAmountBalance.setTextColor(holder.tvAmountBalance.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvAmountBalance.setTextColor(holder.tvAmountBalance.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvName;
        TextView tvAmountSpent;
        TextView tvAmountBalance;
        LinearLayout llMemberRow;



        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAmountSpent = (TextView) itemView.findViewById(R.id.tv_amount_spent);
            tvAmountBalance = (TextView) itemView.findViewById(R.id.tv_amount_balance);

            llMemberRow = (LinearLayout) itemView.findViewById(R.id.ll_member_row);

            llMemberRow.setOnClickListener(this);
            llMemberRow.setOnLongClickListener(this);
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
