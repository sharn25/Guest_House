package com.sb.guesthouse.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.guesthouse.R;
import com.sb.guesthouse.model.Item;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Constants
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    //Elements
    private Context mContext;
    private List<Item> mData;
    private  OnRVClickListener onRVClickListener;

    private static final String TAG = "MTRecyclerViewAdapter";

    public ItemListAdapter(Context mContext, List<Item> data, OnRVClickListener onRVClickListener){
        this.mContext = mContext;
        this.mData = data;
        this.onRVClickListener = onRVClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LogUtil.l(TAG+"_onCreateViewHolder","viewType: " + viewType,false);

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardlayout_items, parent, false);
        return new MyViewHolder(view, onRVClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).tvItemName.setText(mData.get(position).getItemName());
            ((MyViewHolder) holder).tvItemAmount.setText("Rs." + mData.get(position).getItemAmount());
            ((MyViewHolder) holder).tvItemType.setText(mData.get(position).getItemType() + " : ");
            ((MyViewHolder) holder).tvUser.setText(mData.get(position).getUser());
            ((MyViewHolder) holder).tvDate.setText(mData.get(position).getDate());
            ((MyViewHolder) holder).ivItemIcon.setImageResource(getItemIcon(mData.get(position).getItemType()));
        }

    }

    private int getItemIcon(String type){
        switch (type){
            case "Milk":
                return R.drawable.milk_100;
            case "Vegetable":
                return R.drawable.veg_100;
            case "Grocery":
                return R.drawable.gros_100;
            default:
                return R.drawable.other_100;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvItemName, tvItemAmount, tvItemType, tvUser, tvDate;
        ImageView ivItemIcon;
        OnRVClickListener onRVClickListener;
        public MyViewHolder(View itemView, OnRVClickListener onRVClickListener){
            super(itemView);
            ivItemIcon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
            tvItemName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvItemAmount = (TextView) itemView.findViewById(R.id.tv_item_ammount);
            tvItemType = (TextView) itemView.findViewById(R.id.tv_item_type);
            tvUser = (TextView) itemView.findViewById(R.id.tv_user);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
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
