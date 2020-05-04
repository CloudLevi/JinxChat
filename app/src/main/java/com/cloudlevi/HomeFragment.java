package com.cloudlevi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class HomeFragment extends Fragment {

    private HomeFragmentViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private MarketItemAdapter mAdapter;

    private DatabaseReference mDataBaseRef;
    private List<AddFragmentModel> mAddFragmentModels;

    private ProgressBar mProgressCircle;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String fireBaseUserId;

    private int mScrollPositionY;
    private HomeFragmentViewModel viewModel;
    

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = v.findViewById(R.id.market_item_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mProgressCircle = v.findViewById(R.id.progress_circle);

        mAddFragmentModels = new ArrayList<>();

        fireBaseUserId = firebaseUser.getUid();
        mDataBaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        viewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);

        if(viewModel.getData().getValue() != null){
            final HomeFragmentModel homeFragmentModel = viewModel.getData().getValue();

            mRecyclerView.postDelayed(new Runnable() {
                @Override public void run()
                { mRecyclerView.smoothScrollToPosition(homeFragmentModel.getScrollPositionY());
                } }, 150);

        }

        mDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    AddFragmentModel upload = postSnapshot.getValue(AddFragmentModel.class);
                    mAddFragmentModels.add(upload);
                }

                mAdapter = new MarketItemAdapter(getContext(), mAddFragmentModels);

                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mScrollPositionY = mRecyclerView.computeVerticalScrollOffset();
        HomeFragmentModel homeFragmentModel = new HomeFragmentModel();
        homeFragmentModel.setScrollPositionY(mScrollPositionY);

        viewModel.setData(homeFragmentModel);

    }

}
