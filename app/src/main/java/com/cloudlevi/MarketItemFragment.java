package com.cloudlevi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MarketItemFragment extends Fragment {

    private ImageView mImageView;
    private CircleImageView mUserPicImageView;
    private TextView mUserNameTextView;
    private TextView mTitleTextView;
    private TextView mBrandTextView;
    private TextView mConditionTextView;
    private TextView mPriceTextView;
    private TextView mDescriptionTextView;

    private DatabaseReference mDataBaseRef;
    private String userID;

    private RelativeLayout mUserForm;

    public MarketItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_item, container, false);

        mImageView = v.findViewById(R.id.marketFragmentImage);
        mUserPicImageView = v.findViewById(R.id.marketFragmentUserPic);

        mUserNameTextView = v.findViewById(R.id.marketFragmentUsername_tv);
        mTitleTextView = v.findViewById(R.id.marketFragmentTitle_tv);
        mBrandTextView = v.findViewById(R.id.marketFragmentBrand_tv);
        mConditionTextView = v.findViewById(R.id.marketFragmentCondition_tv);
        mPriceTextView = v.findViewById(R.id.marketFragmentPrice_tv);
        mDescriptionTextView = v.findViewById(R.id.marketFragmentDescrDetailed_tv);

        mUserForm = v.findViewById(R.id.userForm);

        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users");


        if(getArguments() != null){
            AddFragmentModel addFragmentModel = getArguments().getParcelable("item");
            Picasso.get()
                    .load(addFragmentModel.getImageURL())
                    .into(mImageView);

            userID = addFragmentModel.getUserIdModel();

            mUserForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", userID);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_marketItemFragment_to_userPagerAdapterFragment, bundle);
                }
            });

            mDataBaseRef.child(addFragmentModel.getUserIdModel()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.get()
                            .load(dataSnapshot.child("imageURL").getValue().toString())
                            .into(mUserPicImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mUserNameTextView.setText(addFragmentModel.getUsernameModel());
            mTitleTextView.setText(addFragmentModel.getTitleModel());
            mBrandTextView.setText(addFragmentModel.getBrandModel());
            mConditionTextView.setText(addFragmentModel.getConditionModel());
            mPriceTextView.setText(addFragmentModel.getPriceModel());
            mDescriptionTextView.setText(addFragmentModel.getDescriptionModel());

        }

        return v;
    }
}
