package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserChatsRef;
    private DatabaseReference mSecondUserRef;
    private DatabaseReference mChatsRef;

    private FirebaseUser firebaseUser;

    private String secondUserID;

    private String secondUserUsername;
    private String secondUserImageURL;
    private String lastMessage;
    private String chatID;

    private DataSnapshot updatedSnapshot;

    private List<ChatListModel> mChatListModels;

    private ArrayList<String> mUserIDList;

    private ChatListAdapter mChatListAdapter;

    public ChatListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerView = v.findViewById(R.id.chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mChatListModels = new ArrayList<>();
        mUserIDList = new ArrayList<>();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserChatsRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/UserChats");
        mSecondUserRef = FirebaseDatabase.getInstance().getReference("Users");
        mChatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot rootSnapshot) {

                mRootRef.child("Users").child(firebaseUser.getUid()).child("UserChats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mChatListModels.clear();
                        mUserIDList.clear();
                        for(DataSnapshot currentUserChatSnapshot: dataSnapshot.getChildren()){

                            System.out.println(currentUserChatSnapshot.getKey() + "   KEY");

                            if(firebaseUser.getUid().equals(currentUserChatSnapshot.child("receiver").getValue().toString())){
                                secondUserID = currentUserChatSnapshot.child("sender").getValue().toString();
                            }else{
                                if(firebaseUser.getUid().equals(currentUserChatSnapshot.child("sender").getValue().toString())){
                                    secondUserID = currentUserChatSnapshot.child("receiver").getValue().toString();
                                }
                            }


                            secondUserImageURL = rootSnapshot.child("Users").child(secondUserID).child("imageURL").getValue().toString();
                            secondUserUsername = rootSnapshot.child("Users").child(secondUserID).child("username").getValue().toString();

                            lastMessage = currentUserChatSnapshot.child("lastMessage").getValue().toString();
                            System.out.println(lastMessage);
                            System.out.println(currentUserChatSnapshot);

                            ChatListModel chatModel = new ChatListModel(secondUserImageURL, secondUserUsername, lastMessage);
                            mChatListModels.add(chatModel);

                            mUserIDList.add(secondUserID);

                            mChatListAdapter = new ChatListAdapter(getContext(), mChatListModels, mUserIDList);

                            recyclerView.setAdapter(mChatListAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }


}
