package com.JinxMarket;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatListModel> mChatListModels;
    private ArrayList<String> mUserIDList;

    public ChatListAdapter(Context context, List<ChatListModel> chatListModels, ArrayList<String> userIDList){
        mContext = context;
        mChatListModels = chatListModels;
        mUserIDList = userIDList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        ChatListModel chatModel = mChatListModels.get(position);

        Picasso.get()
                .load(chatModel.getImageURL())
                .placeholder(R.drawable.progress_animation_bigger)
                .into(holder.circleImageView);

        holder.username.setText(chatModel.getUsername());
        holder.lastMessage.setText(chatModel.getLastMessage());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                System.out.println(mUserIDList.get(position) + " BEFORE CALLING");
                bundle.putString("userReceiverID", mUserIDList.get(position));
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.ChatListProfileImage);
            username = itemView.findViewById(R.id.ChatListUserName);
            lastMessage = itemView.findViewById(R.id.ChatListLastMessage);
            layout = itemView.findViewById(R.id.ChatListLayout);
        }
    }
}
