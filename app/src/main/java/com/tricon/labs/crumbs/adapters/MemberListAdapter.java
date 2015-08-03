package com.tricon.labs.crumbs.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.interfaces.RemoveMemberListener;
import com.tricon.labs.crumbs.models.Contact;

import java.util.Collection;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    private List<Contact> mMembers;

    public MemberListAdapter(List<Contact> members) {
        this.mMembers = members;
    }

    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_member_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MemberListAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(mMembers.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public Contact getItem(int position) {
        return mMembers.get(position);
    }

    public void addMember(Contact member) {
        mMembers.add(0, member);
        notifyItemInserted(0);
    }

    public void addMembers(Collection<Contact> members) {
        mMembers.addAll(0, members);
        notifyItemRangeInserted(0, members.size());
    }

    public Contact removeMember(int position) {
        Contact member = mMembers.remove(position);
        notifyItemRemoved(position);
        return member;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        ImageButton btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            btnRemove = (ImageButton) itemView.findViewById(R.id.btn_remove);

            btnRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_remove:
                    ((RemoveMemberListener) v.getContext()).removeMember(getAdapterPosition());
                    break;

                default:
                    break;
            }
        }
    }
}
