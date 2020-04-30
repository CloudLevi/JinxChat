package com.cloudlevi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarketItemAdapter extends RecyclerView.Adapter<MarketItemAdapter.MarketItemViewHolder> {
    private Context mContext;
    private List<AddFragmentModel> mUploads;

    public MarketItemAdapter(Context context, List<AddFragmentModel> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public MarketItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.market_item, parent, false);
        return new MarketItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketItemViewHolder holder, int position) {
        AddFragmentModel itemCurrent = mUploads.get(position);
        Picasso.get()
                .load(itemCurrent.getImageURL())
                .centerCrop()
                .placeholder(R.mipmap.ic_round_160_green)
                .fit()
                .into(holder.imageView);
        holder.textViewUser.setText(itemCurrent.getUsernameModel());
        holder.textViewPrice.setText(itemCurrent.getPriceModel());
        holder.textViewBrand.setText(itemCurrent.getBrandModel());
        holder.textViewCondition.setText(itemCurrent.getConditionModel());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class MarketItemViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewUser;
        public ImageView imageView;
        public TextView textViewPrice;
        public TextView textViewBrand;
        public TextView textViewCondition;

        public MarketItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUser = itemView.findViewById(R.id.market_item_user);
            imageView = itemView.findViewById(R.id.market_item_image);
            textViewPrice = itemView.findViewById(R.id.market_item_price);
            textViewBrand = itemView.findViewById(R.id.market_item_brand);
            textViewCondition = itemView.findViewById(R.id.market_item_condition);
        }
    }
}
