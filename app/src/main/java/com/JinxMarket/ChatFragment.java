package com.JinxMarket;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private CircleImageView mReceiverProfileImage;
    private Bitmap mReceiverImageBitmap;

    private TextView mReceiverUserName;
    private String mReceiverUserID;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference databaseUserReceiverReference;
    private DatabaseReference databaseUserSenderReference;

    private DatabaseReference databaseChatReference;

    private ImageButton mSendMessageBTN;
    private EditText mEditMessage;

    private String mainChatID;

    private MessageAdapter mMessageAdapter;
    private List<ChatMessageModel> mMessageModels;

    private RecyclerView mRecyclerView;

    private String receiverImageURL;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        if(getArguments() != null){
            mReceiverUserID = getArguments().getString("userReceiverID");
        }

        Toolbar toolbar = v.findViewById(R.id.fragmentChatToolbar);

        LoginActivity activity = (LoginActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        

        mReceiverProfileImage = v.findViewById(R.id.receiverPicture);
        mReceiverUserName = v.findViewById(R.id.receiverUserName);

        mSendMessageBTN = v.findViewById(R.id.sendBTN);
        mEditMessage = v.findViewById(R.id.messageEditText);

        mRecyclerView = v.findViewById(R.id.messageRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        databaseUserReceiverReference = FirebaseDatabase.getInstance().getReference("Users").child(mReceiverUserID);
        databaseUserSenderReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseChatReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseUserReceiverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String snapshotUsername = dataSnapshot.child("username").getValue().toString();
                receiverImageURL = dataSnapshot.child("imageURL").getValue().toString();

                mReceiverUserName.setText(snapshotUsername);

                Picasso.get()
                        .load(receiverImageURL)
                        .into(mReceiverProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseUserSenderReference.child("UserChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if(postSnapshot.child("chatID").getValue() != null
                            && (postSnapshot.child("sender").getValue().toString().equals(mReceiverUserID)
                            || postSnapshot.child("receiver").getValue().toString().equals(mReceiverUserID))
                    ){
                        mainChatID = postSnapshot.child("chatID").getValue().toString();
                        getMessages();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSendMessageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditMessage.getText().toString().trim();
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(), mReceiverUserID, message);
                }else{
                    Toast.makeText(getContext(), "Empty Message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        if(mainChatID == null){
            mainChatID = databaseChatReference.push().getKey();

            HashMap<String, Object> userHashMap = new HashMap<>();
            userHashMap.put("sender", sender);
            userHashMap.put("receiver", receiver);
            userHashMap.put("chatID", mainChatID);

            databaseUserSenderReference.child("UserChats").child(mainChatID).setValue(userHashMap);
            databaseUserReceiverReference.child("UserChats").child(mainChatID).setValue(userHashMap);

        }

        databaseChatReference.child(mainChatID).push().setValue(hashMap);

        mEditMessage.setText("");

    }

    private void getMessages(){
            mMessageModels = new ArrayList<>();

        databaseChatReference.child(mainChatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessageModels.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    ChatMessageModel messageModel = postSnapshot.getValue(ChatMessageModel.class);

                    mMessageModels.add(messageModel);

                    mMessageAdapter = new MessageAdapter(getContext(), mMessageModels);
                    mRecyclerView.setAdapter(mMessageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
