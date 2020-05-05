package com.cloudlevi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MarketItemAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mFavoritesReference;
    private DatabaseReference mUploadsReference;
    private String mUserID;
    private Boolean fragmentEmpty = true;
    private TextView emptyTV;

    private List<AddFragmentModel> mAddFragmentModels;

    private AddFragmentModel addFragmentModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        mRecyclerView = v.findViewById(R.id.market_item_favorites_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mUserID = FirebaseAuth.getInstance().getUid();

        emptyTV = v.findViewById(R.id.emptyTV);

        mAddFragmentModels = new ArrayList<>();

        mProgressCircle = v.findViewById(R.id.progress_circle_favorites);

        mFavoritesReference = FirebaseDatabase.getInstance().getReference("Users/" + mUserID + "/UserFavorites");
        mUploadsReference = FirebaseDatabase.getInstance().getReference("uploads");

        mFavoritesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if(postSnapshot.child("uploadID").getValue() != null) {
                        fragmentEmpty = false;
                        final String uploadID = postSnapshot.child("uploadID").getValue().toString();
                        mUploadsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                addFragmentModel = dataSnapshot.child(uploadID).getValue(AddFragmentModel.class);
                                mAddFragmentModels.add(addFragmentModel);

                                    mAdapter = new MarketItemAdapter(getContext(), mAddFragmentModels, "FavoritesFragment");

                                    mProgressCircle.setVisibility(View.INVISIBLE);
                                    mRecyclerView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                if(fragmentEmpty){
                    System.out.println("CALLED");
                    mProgressCircle.setVisibility(View.INVISIBLE);
                    emptyTV.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return v;
    }
}
