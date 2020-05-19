package com.JinxMarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private Context mContext;
    private List<UserModel> mUserModels;
    private NavController mNavController;

    private DatabaseReference mCurrentUserRef;
    private DatabaseReference mUserUploadsRef;

    private UserModel currentUser;

    public UsersListAdapter(Context context, List<UserModel> userModels, NavController navController){
        mContext = context;
        mUserModels = userModels;
        mNavController = navController;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_list_item, parent, false);
        return new UsersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        currentUser = mUserModels.get(position);
        mCurrentUserRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getId());
        mUserUploadsRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getId() + "/UserUploads");

        holder.username.setText(currentUser.getUsername());

        if(currentUser.getStatus().equals("online")){
            holder.userStatus.setVisibility(View.VISIBLE);
        }else{
            holder.userStatus.setVisibility(View.INVISIBLE);
        }

        Picasso.get()
                .load(currentUser.getImageURL())
                .placeholder(R.drawable.progress_animation)
                .into(holder.circleImageView);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userID", mUserModels.get(position).getId());
                mNavController.navigate(R.id.action_chatViewPagerAdapter_to_userPagerAdapterFragment, bundle);
            }
        });

        mCurrentUserRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                if(statusSnapshot.getValue() != null){
                    currentUser.setStatus(statusSnapshot.getValue().toString());

                    if(currentUser.getStatus().equals("online")){
                        holder.userStatus.setVisibility(View.VISIBLE);
                    }else{
                        holder.userStatus.setVisibility(View.INVISIBLE);
                    }
                }else{
                    holder.userStatus.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserUploadsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long uploadsCount = dataSnapshot.getChildrenCount();

                holder.itemCount.setText(Long.toString(uploadsCount) + " items");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView circleImageView;
        public TextView username;
        public TextView itemCount;
        public RelativeLayout layout;
        public ImageView userStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.UsersListProfileImage);
            username = itemView.findViewById(R.id.UsersListUserName);
            itemCount = itemView.findViewById(R.id.UsersListItemCount);
            layout = itemView.findViewById(R.id.UsersListLayout);
            userStatus = itemView.findViewById(R.id.UsersListUserStatus);

        }
    }

}
