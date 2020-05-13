package com.JinxMarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ChatMessageModel> mMessageModels;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(Context context, List<ChatMessageModel> chatMessageModels){
        mContext = context;
        mMessageModels = chatMessageModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_message_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_message_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatMessageModel messageModel = mMessageModels.get(position);

        holder.userMessage.setText(messageModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageModels.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userMessage = itemView.findViewById(R.id.userMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mMessageModels.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
