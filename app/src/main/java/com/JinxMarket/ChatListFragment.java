package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.JinxMarket.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    public static final CharSequence PAGE_TITLE = "Chats";
    private RecyclerView recyclerView;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserChatsRef;
    private DatabaseReference mSecondUserRef;
    private DatabaseReference mChatsRef;

    private FirebaseUser firebaseUser;

    private String firstUserID;
    private String secondUserID;

    private String secondUserUsername;
    private String secondUserImageURL;
    private String secondUserStatus;
    private String lastMessage;
    private String chatID;

    private List<ChatListModel> mChatListModels;

    private ChatListAdapter mChatListAdapter;

    private NavController finalNavController;
    private int chatPositionDeletion;

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

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserChatsRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/UserChats");
        mSecondUserRef = FirebaseDatabase.getInstance().getReference("Users");
        mChatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        updateToke(FirebaseInstanceId.getInstance().getToken());

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerForContextMenu(recyclerView);
        finalNavController = Navigation.findNavController(view);

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot rootSnapshot) {

                mRootRef.child("Users").child(firebaseUser.getUid()).child("UserChats").orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mChatListModels.clear();
                        for(final DataSnapshot currentUserChatSnapshot: dataSnapshot.getChildren()){

                            if(firebaseUser.getUid().equals(currentUserChatSnapshot.child("receiver").getValue().toString())){
                                firstUserID = currentUserChatSnapshot.child("receiver").getValue().toString();
                                secondUserID = currentUserChatSnapshot.child("sender").getValue().toString();
                            }else{
                                if(firebaseUser.getUid().equals(currentUserChatSnapshot.child("sender").getValue().toString())){
                                    firstUserID = currentUserChatSnapshot.child("sender").getValue().toString();
                                    secondUserID = currentUserChatSnapshot.child("receiver").getValue().toString();
                                }
                            }


                            if(rootSnapshot.child("Users").child(secondUserID).getValue() != null){


                                secondUserImageURL = rootSnapshot.child("Users").child(secondUserID).child("imageURL").getValue().toString();
                                secondUserUsername = rootSnapshot.child("Users").child(secondUserID).child("username").getValue().toString();
                                secondUserStatus = rootSnapshot.child("Users").child(secondUserID).child("status").getValue().toString();


                            }else{
                                secondUserImageURL = "https://firebasestorage.googleapis.com/v0/b/my-application-af75c.appspot.com/o/profilepics%2FDefaultProfilePic.png?alt=media&token=017b6c59-f031-4588-8732-d79c2738317a";
                                secondUserUsername = "[deleted user]";
                                secondUserStatus = "offline";
                            }

                            lastMessage = currentUserChatSnapshot.child("lastMessage").getValue().toString();
                            chatID = currentUserChatSnapshot.child("chatID").getValue().toString();

                            final ChatListModel chatModel = new ChatListModel(chatID, firstUserID, secondUserID, secondUserImageURL, secondUserUsername, lastMessage, secondUserStatus);
                            mChatListModels.add(chatModel);

                            mChatListAdapter = new ChatListAdapter(getContext(), mChatListModels, finalNavController);

                            recyclerView.setAdapter(mChatListAdapter);

                            currentUserChatSnapshot.getRef().addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    int mChatListModelsIndex = 0;

                                    for (int i = 0; i < mChatListModels.size(); i++) {

                                        if (mChatListModels.get(i).getChatID().equals(currentUserChatSnapshot.child("chatID").getValue().toString())) {
                                            mChatListModelsIndex = i;
                                        }
                                    }

                                    mChatListAdapter.swapItems(mChatListModelsIndex, 0);
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case 121:
                mChatListAdapter.openChat(item.getGroupId());
                return true;
            case 122:
                chatPositionDeletion = item.getGroupId();
                DeleteChatDialog dialog = new DeleteChatDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "DeleteItemDialog");
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void updateToke(String token){
        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        tokenReference.child(firebaseUser.getUid()).setValue(token1);
    }

    public void deleteChat(){
        mChatListAdapter.deleteChat(chatPositionDeletion);
    }
}
