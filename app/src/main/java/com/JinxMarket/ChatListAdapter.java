package com.JinxMarket;

import android.content.Context;
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
import com.google.firebase.database.snapshot.BooleanNode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatListModel> mChatListModels;

    private String defaultImageURL = "https://firebasestorage.googleapis.com/v0/b/my-application-af75c.appspot.com/o/profilepics%2FDefaultProfilePic.png?alt=media&token=017b6c59-f031-4588-8732-d79c2738317a";

    public ChatListAdapter(Context context, List<ChatListModel> chatListModels){
        mContext = context;
        mChatListModels = chatListModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull List<Object> payloads) {
        if(!payloads.isEmpty()){
            if(payloads.get(0) instanceof Boolean){
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavController navController = Navigation.findNavController(v);
                        Bundle bundle = new Bundle();
                        bundle.putString("userReceiverID", mChatListModels.get(position).getSecondUserID());
                        bundle.putString("chatID", mChatListModels.get(position).getChatID());
                        navController.navigate(R.id.action_chatListFragment_to_chatFragment, bundle);
                    }
                });
            }
        }else{
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ChatListModel chatModel = mChatListModels.get(position);

        Picasso.get()
                .load(chatModel.getImageURL())
                .placeholder(R.drawable.progress_animation_bigger)
                .into(holder.circleImageView);

        holder.username.setText(chatModel.getUsername());
        holder.lastMessage.setText(chatModel.getLastMessage());

        DatabaseReference firstUserReference = FirebaseDatabase.getInstance().getReference("Users").child(mChatListModels.get(position).getFirstUserID());
        DatabaseReference secondUserReference = FirebaseDatabase.getInstance().getReference("Users").child(mChatListModels.get(position).getSecondUserID());

        firstUserReference.child("UserChats").child(mChatListModels.get(position).getChatID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot lastMessageSnapshot) {
                if(lastMessageSnapshot.getValue() != null){
                    chatModel.setLastMessage(lastMessageSnapshot.child("lastMessage").getValue().toString());

                    holder.lastMessage.setText(chatModel.getLastMessage());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        secondUserReference.child("imageURL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                if(imageSnapshot.getValue() != null){
                    chatModel.setImageURL(imageSnapshot.getValue().toString());

                    Picasso.get()
                            .load(chatModel.getImageURL())
                            .placeholder(R.drawable.progress_animation_bigger)
                            .into(holder.circleImageView);
                }else{
                    chatModel.setImageURL(defaultImageURL);

                    Picasso.get()
                            .load(chatModel.getImageURL())
                            .placeholder(R.drawable.progress_animation_bigger)
                            .into(holder.circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        secondUserReference.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                if(usernameSnapshot.getValue() != null){
                    chatModel.setUsername(usernameSnapshot.getValue().toString());

                    holder.username.setText(chatModel.getUsername());

                }else{
                    chatModel.setUsername("[deleted user]");

                    holder.username.setText(chatModel.getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        secondUserReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                if(statusSnapshot.getValue() != null){
                    chatModel.setUserStatus(statusSnapshot.getValue().toString());

                    if(chatModel.getUserStatus().equals("online")){
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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putString("userReceiverID", mChatListModels.get(position).getSecondUserID());
                bundle.putString("chatID", mChatListModels.get(position).getChatID());
                navController.navigate(R.id.action_chatListFragment_to_chatFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView circleImageView;
        public TextView username;
        public TextView lastMessage;
        public RelativeLayout layout;
        public ImageView userStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.ChatListProfileImage);
            username = itemView.findViewById(R.id.ChatListUserName);
            lastMessage = itemView.findViewById(R.id.ChatListLastMessage);
            layout = itemView.findViewById(R.id.ChatListLayout);
            userStatus = itemView.findViewById(R.id.userStatus);
        }
    }

    public void swapItems(int fromPosition,int toPosition){

        ChatListModel chatModelCopy = mChatListModels.get(fromPosition);
        mChatListModels.remove(fromPosition);
        mChatListModels.add(toPosition, chatModelCopy);

        notifyItemMoved(fromPosition, toPosition);
        Boolean update = true;

        for(int i = 0; i < mChatListModels.size(); i++){
            notifyItemChanged(i, Boolean.valueOf(true));
        }
    }

    public ChatListModel getItem(int position){
        return mChatListModels.get(position);
    }


}
