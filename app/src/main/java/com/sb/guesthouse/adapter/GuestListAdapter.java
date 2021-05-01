package com.sb.guesthouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.guesthouse.R;
import com.sb.guesthouse.model.Guest;
import com.sb.guesthouse.model.Item;
import com.sb.guesthouse.utils.LogUtil;

import java.util.List;

public class GuestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "GuestListAdapter";
    //Elements
    private Context mContext;
    private List<Guest> mData;
    private GuestListAdapter.OnRVClickListener onRVClickListener;

    //Constractor
    public GuestListAdapter(Context mContext, List<Guest> data, GuestListAdapter.OnRVClickListener onRVClickListener){
        this.mContext = mContext;
        this.mData = data;
        this.onRVClickListener = onRVClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.layout_list_guest, parent, false);
        return new GuestListAdapter.MyViewHolder(view, onRVClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof GuestListAdapter.MyViewHolder) {
            ((MyViewHolder) holder).tvGuestName.setText(mData.get(position).getGuestName());
            ((MyViewHolder) holder).tvStatus.setText(mData.get(position).getStatus());
            ((MyViewHolder) holder).tvStay.setText(mData.get(position).getDays());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvGuestName, tvStatus, tvStay;
        //ImageView ivItemIcon;
        OnRVClickListener onRVClickListener;
        public MyViewHolder(View itemView, GuestListAdapter.OnRVClickListener onRVClickListener){
            super(itemView);
            tvGuestName = (TextView) itemView.findViewById(R.id.tv_rv_guest_name);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_Status);
            tvStay = (TextView) itemView.findViewById(R.id.tv_stay);
            this.onRVClickListener = onRVClickListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onRVClickListener.OnRVClick(getAdapterPosition());
        }
    }

    public interface OnRVClickListener{
        void OnRVClick(int position);
    }
}
