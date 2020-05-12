package com.JinxMarket;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MarketItemAdapter extends RecyclerView.Adapter<MarketItemAdapter.MarketItemViewHolder> {
    private Context mContext;
    private String mCallingFragment;
    private List<AddFragmentModel> mUploads;

    public MarketItemAdapter(Context context, List<AddFragmentModel> uploads, String callingFragment){
        mContext = context;
        mUploads = uploads;
        mCallingFragment = callingFragment;
    }

    @NonNull
    @Override
    public MarketItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.market_item, parent, false);
        return new MarketItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MarketItemViewHolder holder, int position) {
        AddFragmentModel itemCurrent = mUploads.get(position);

        DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + itemCurrent.getUserIdModel());
        mDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("imageURL").getValue().toString())
                        .resize(32, 32)
                        .into(holder.userCircleView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Picasso.get()
                .load(itemCurrent.getImageURL())
                .placeholder(R.drawable.progress_animation)
                .into(holder.imageView);
        holder.textViewUser.setText(itemCurrent.getUsernameModel());
        holder.textViewPrice.setText(itemCurrent.getPriceModel());
        holder.textViewBrand.setText(itemCurrent.getBrandModel());
        holder.textViewSize.setText(itemCurrent.getSizeModel());
        holder.textViewCondition.setText(itemCurrent.getConditionModel());

        Bundle bundle = new Bundle();
        bundle.putParcelable("item", itemCurrent);

        if(mCallingFragment.equals("HomeFragment")){
            holder.marketItemLayout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_marketItemFragment, bundle));
        } else{
            if(mCallingFragment.equals("FavoritesFragment")){
                holder.marketItemLayout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_favoritesFragment_to_marketItemFragment, bundle));
            }
        }
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
        public TextView textViewSize;
        public TextView textViewCondition;
        public LinearLayout marketItemLayout;
        public CircleImageView userCircleView;

        public MarketItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUser = itemView.findViewById(R.id.market_item_user);
            imageView = itemView.findViewById(R.id.market_item_image);
            textViewPrice = itemView.findViewById(R.id.market_item_price);
            textViewBrand = itemView.findViewById(R.id.market_item_brand);
            textViewSize = itemView.findViewById(R.id.market_item_size);
            textViewCondition = itemView.findViewById(R.id.market_item_condition);
            marketItemLayout = itemView.findViewById(R.id.market_item_layout);
            userCircleView = itemView.findViewById(R.id.market_item_userProfilePic);

        }
    }
}
