package com.JinxMarket;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.JinxMarket.Notifications.APIService;
import com.JinxMarket.Notifications.Client;
import com.JinxMarket.Notifications.Data;
import com.JinxMarket.Notifications.MyResponse;
import com.JinxMarket.Notifications.Sender;
import com.JinxMarket.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private CircleImageView mReceiverProfileImage;

    private TextView mReceiverUserName;
    private String mReceiverUserID;
    private String mReceiverStatus;

    private ValueEventListener seenListener;

    private ImageView mReceiverStatusImage;
    private TextView mReceiverStatusText;
    private RelativeLayout mReceiverLayout;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference databaseUserReceiverReference;
    private DatabaseReference databaseUserSenderReference;
    private DatabaseReference currentMessageReference;

    private DatabaseReference databaseChatReference;

    private ImageButton mSendMessageBTN;
    private EditText mEditMessage;

    private Boolean userDeleted = false;
    private Boolean messageStatusListening = true;

    private String mainChatID;

    private MessageAdapter mMessageAdapter;
    private List<ChatMessageModel> mMessageModels;

    private RecyclerView mRecyclerView;

    private String receiverImageURL;

    private APIService apiService;

    private boolean notify = false;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        if(getArguments() != null){
            mReceiverUserID = getArguments().getString("userReceiverID");
            mainChatID = getArguments().getString("chatID");
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
        mReceiverStatusImage = v.findViewById(R.id.receiverStatusImage);
        mReceiverStatusText = v.findViewById(R.id.receiverStatusText);
        mReceiverLayout = v.findViewById(R.id.receiverLayout);

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
                String snapshotUsername;
                if(dataSnapshot.child("username").getValue() != null){
                    snapshotUsername = dataSnapshot.child("username").getValue().toString();
                    receiverImageURL = dataSnapshot.child("imageURL").getValue().toString();
                    mReceiverStatus = dataSnapshot.child("status").getValue().toString();
                } else{
                    snapshotUsername = "[deleted user]";
                    userDeleted = true;
                    receiverImageURL = "https://firebasestorage.googleapis.com/v0/b/my-application-af75c.appspot.com/o/profilepics%2FDefaultProfilePic.png?alt=media&token=017b6c59-f031-4588-8732-d79c2738317a";
                    mReceiverStatus = "deleted";
                }

                mReceiverUserName.setText(snapshotUsername);

                Picasso.get()
                        .load(receiverImageURL)
                        .into(mReceiverProfileImage);
                switch(mReceiverStatus){
                    case "online":
                        mReceiverStatusImage.setVisibility(View.VISIBLE);

                        mReceiverStatusText.setTextColor(Color.parseColor("#29A15B"));
                        mReceiverStatusText.setText(R.string.online);
                        mReceiverStatusText.setVisibility(View.VISIBLE);
                        break;
                    case "offline":
                        mReceiverStatusImage.setVisibility(View.INVISIBLE);

                        mReceiverStatusText.setTextColor(Color.parseColor("#606060"));
                        mReceiverStatusText.setText(R.string.offline);
                        mReceiverStatusText.setVisibility(View.VISIBLE);
                }
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
                        getMessages();
                        messageSeen(mReceiverUserID);
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
                notify = true;
                if(!userDeleted){
                    String message = mEditMessage.getText().toString().trim();
                    if(!message.equals("")){
                        sendMessage(firebaseUser.getUid(), mReceiverUserID, message);
                    }else{
                        Toast.makeText(getContext(), "Empty Message", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "User is deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mReceiverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!userDeleted){
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", mReceiverUserID);

                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_chatFragment_to_userPagerAdapterFragment, bundle);
                }
                else{
                    Toast.makeText(getContext(), "User is deleted", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        messageStatusListening = true;
        if(mainChatID != null){messageSeen(mReceiverUserID);}
    }

    private void messageSeen(final String userID){

            currentMessageReference = databaseChatReference.child(mainChatID);

            seenListener = currentMessageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(messageStatusListening){
                    for (DataSnapshot currentMessage : dataSnapshot.getChildren()) {

                        ChatMessageModel messageModel = currentMessage.getValue(ChatMessageModel.class);

                        if (messageModel.getReceiver().equals(firebaseUser.getUid()) && messageModel.getSender().equals(userID)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isRead", true);
                            currentMessage.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message){

        long messageTime = -1 * System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isRead", false);

        if(mainChatID == null){
            mainChatID = databaseChatReference.push().getKey();

            HashMap<String, Object> userHashMap = new HashMap<>();
            userHashMap.put("sender", sender);
            userHashMap.put("receiver", receiver);
            userHashMap.put("chatID", mainChatID);
            userHashMap.put("lastMessage", message);
            userHashMap.put("time", messageTime);

            databaseUserSenderReference.child("UserChats").child(mainChatID).setValue(userHashMap);
            databaseUserReceiverReference.child("UserChats").child(mainChatID).setValue(userHashMap);

        }

        databaseChatReference.child(mainChatID).push().setValue(hashMap);

        databaseUserSenderReference.child("UserChats").child(mainChatID).child("lastMessage").setValue(message);
        databaseUserSenderReference.child("UserChats").child(mainChatID).child("time").setValue(messageTime);
        databaseUserReceiverReference.child("UserChats").child(mainChatID).child("lastMessage").setValue(message);
        databaseUserReceiverReference.child("UserChats").child(mainChatID).child("time").setValue(messageTime);

        mEditMessage.setText("");

        final String msg = message;
        databaseUserSenderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if(notify){
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokenReference.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Token currentToken = postSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+":  "+message, "New Message",
                            mReceiverUserID);

                    Sender sender = new Sender(data, currentToken.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(getContext(), "Failure!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
        messageStatusListening = false;
    }
}
