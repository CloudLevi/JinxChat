package com.cloudlevi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private DatabaseReference mDataBaseRef;
    private DatabaseReference mDataBaseUserRef;

    private RecyclerView mRecyclerView;
    private List<AddFragmentModel> mAddFragmentModels;

    private ProgressBar mProgressCircle;
    private String userID;

    private TextView usernameTextView;
    private CircleImageView userPic;
    private TextView itemCount;


    public UserPageFragment() {
        // Required empty public constructor
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

        mProgressCircle = v.findViewById(R.id.progress_homepage_circle);
        itemCount = v.findViewById(R.id.itemCount);

        mAddFragmentModels = new ArrayList<>();

        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }


        System.out.println(userID);

        mDataBaseRef = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/UserUploads");
        mDataBaseUserRef = FirebaseDatabase.getInstance().getReference("Users/" + userID);

        mDataBaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("imageURL").getValue().toString())
                        .into(userPic);
                usernameTextView.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    AddFragmentModel upload = postSnapshot.getValue(AddFragmentModel.class);
                    mAddFragmentModels.add(upload);
                }

                itemCount.setText(mAddFragmentModels.size() + " items");
                mAdapter = new MarketItemHomePageAdapter(getContext(), mAddFragmentModels);


                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return v;
    }
}
