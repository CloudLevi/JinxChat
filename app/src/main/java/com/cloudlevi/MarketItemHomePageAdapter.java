package com.cloudlevi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MarketItemHomePageAdapter extends RecyclerView.Adapter<MarketItemHomePageAdapter.MarketItemHomePageViewHolder> {
    private Context mContext;
    private List<AddFragmentModel> mUploads;

    public MarketItemHomePageAdapter(Context context, List<AddFragmentModel> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public MarketItemHomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.market_item_homepage, parent, false);
        return new MarketItemHomePageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MarketItemHomePageViewHolder holder, int position) {
        AddFragmentModel itemCurrent = mUploads.get(position);
        Picasso.get()
                .load(itemCurrent.getImageURL())
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .fit()
                .into(holder.imageView);
        holder.textViewPrice.setText(itemCurrent.getPriceModel());
        holder.textViewBrand.setText(itemCurrent.getBrandModel());
        holder.textViewCondition.setText(itemCurrent.getConditionModel());

        final Bundle bundle = new Bundle();
        bundle.putParcelable("item", itemCurrent);

        holder.marketItemLayout.setOnClickListener(Navigation
                .createNavigateOnClickListener(R.id.action_userPagerAdapterFragment_to_marketItemFragment, bundle));
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class MarketItemHomePageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textViewPrice;
        public TextView textViewBrand;
        public TextView textViewCondition;
        public LinearLayout marketItemLayout;

        public MarketItemHomePageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.market_item_homepage_image);
            textViewPrice = itemView.findViewById(R.id.market_item_homepage_price);
            textViewBrand = itemView.findViewById(R.id.market_item_homepage_brand);
            textViewCondition = itemView.findViewById(R.id.market_item_homepage_condition);
            marketItemLayout = itemView.findViewById(R.id.market_item_homepage_layout);

        }
    }
}
