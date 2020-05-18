package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UserPageFragment extends Fragment {

    private MarketItemHomePageAdapter mAdapter;

    private CardView mContactUserBTN;

    private DatabaseReference mDataBaseUploadsRef;
    private DatabaseReference mDataBaseRef;
    private DatabaseReference mDataBaseUserRef;
    private DatabaseReference mDataBaseAllUsersRef;

    private RecyclerView mRecyclerView;
    private List<AddFragmentModel> mAddFragmentModels;

    private ProgressBar mProgressCircle;
    private String userID;

    private TextView usernameTextView;
    private CircleImageView userPic;
    private CircleImageView userStatus;
    private TextView itemCount;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserID = firebaseUser.getUid();

    private Bundle messageBundle = new Bundle();


    public UserPageFragment() {
        // Required empty public constructor
    }

    public UserPageFragment(String mUserID) {
        userID = mUserID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_page, container, false);


        mRecyclerView = v.findViewById(R.id.market_item_homepage_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        usernameTextView = v.findViewById(R.id.UserPageUsername_tv);
        userPic = v.findViewById(R.id.UserPageUserPic);
        userStatus = v.findViewById(R.id.UserPageUserStatus);

        mProgressCircle = v.findViewById(R.id.progress_homepage_circle);
        itemCount = v.findViewById(R.id.itemCount);

        mContactUserBTN = v.findViewById(R.id.contactUserBTN);

        mAddFragmentModels = new ArrayList<>();


        mDataBaseUploadsRef = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/UserUploads");
        mDataBaseUserRef = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        mDataBaseAllUsersRef = FirebaseDatabase.getInstance().getReference("Users");
        mDataBaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mContactUserBTN.setVisibility(View.GONE);
        mContactUserBTN.setEnabled(false);

        mDataBaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("imageURL").getValue().toString())
                        .into(userPic);

                usernameTextView.setText(dataSnapshot.child("username").getValue().toString());

                if(dataSnapshot.child("status").getValue().equals("online")){
                    userStatus.setVisibility(View.VISIBLE);
                }else{
                    userStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDataBaseUploadsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshotMain : dataSnapshot.getChildren()){
                    final String mUserUploadID = postSnapshotMain.child("uploadID").getValue().toString();

                    mDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                if(postSnapshot.child("uploadIDModel").getValue() != null && postSnapshot.child("uploadIDModel").getValue().toString().equals(mUserUploadID)){

                                    AddFragmentModel upload = postSnapshot.getValue(AddFragmentModel.class);
                                    mAddFragmentModels.add(upload);

                                    itemCount.setText(mAddFragmentModels.size() + " items");

                                    mAdapter = new MarketItemHomePageAdapter(getContext(), mAddFragmentModels);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            }

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


        mDataBaseAllUsersRef.child(currentUserID).child("UserChats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot currentChatSnapshot: dataSnapshot.getChildren()){
                    if(currentChatSnapshot.child("receiver").getValue().toString().equals(userID) ||
                            currentChatSnapshot.child("sender").getValue().toString().equals(userID)
                    ){
                        messageBundle.putString("userReceiverID", userID);
                        messageBundle.putString("chatID", currentChatSnapshot.child("chatID").getValue().toString());
                    }else{
                        messageBundle.putString("userReceiverID", userID);
                    }
                }
                mContactUserBTN.setEnabled(true);
                mContactUserBTN.setVisibility(View.VISIBLE);

                mContactUserBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.action_userPagerAdapterFragment_to_chatFragment, messageBundle);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProgressCircle.setVisibility(View.INVISIBLE);

    return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*mContactUserBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userReceiverID", userID);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_userPagerAdapterFragment_to_chatFragment, bundle);
            }
        });*/
    }
}
