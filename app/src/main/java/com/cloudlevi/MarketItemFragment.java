package com.cloudlevi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private TextView mFavoritesTextView;

    private ImageView mFavoritesImageView;

    private CardView mFavoritesButton;

    private DatabaseReference mDataBaseRef;
    private DatabaseReference mFavoritesDataBaseRef;
    private String userID;
    private String currentUserID;

    private RelativeLayout mUserForm;

    private AddFragmentModel addFragmentModel;

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
        mFavoritesTextView = v.findViewById(R.id.favorites_tv);
        mFavoritesImageView = v.findViewById(R.id.favorites_image);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFavoritesButton = v.findViewById(R.id.addToFavoritesBtn);

        mUserForm = v.findViewById(R.id.userForm);

        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFavoritesDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + currentUserID + "/UserFavorites");


        if(getArguments() != null){
            addFragmentModel = getArguments().getParcelable("item");
            Picasso.get()
                    .load(addFragmentModel.getImageURL())
                    .into(mImageView);

            userID = addFragmentModel.getUserIdModel();

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

            mFavoritesDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isInFavorites = false;
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        if(postSnapshot.child("uploadID").getValue().equals(addFragmentModel.getUploadIDModel())){
                            isInFavorites = true;
                        }
                    }
                    if(isInFavorites){setFavoritesButtonRemoval();}
                    else{
                        setFavoritesButtonAdding();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).child("uploadID").setValue(addFragmentModel.getUploadIDModel()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        mFavoritesButton.setEnabled(false);
                        mFavoritesTextView.setText(R.string.remove_from_favorites);
                        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.PlainTextColor2, null));
                    }
                });
            }
        });

        mUserForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_marketItemFragment_to_userPagerAdapterFragment, bundle);
            }
        });
    }

    private void setFavoritesButtonRemoval(){
        mFavoritesImageView.setVisibility(View.GONE);
        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.PlainTextColor2, null));
        mFavoritesTextView.setText(R.string.remove_from_favorites);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        setFavoritesButtonAdding();
                    }
                });
            }
        });
    }

    private void setFavoritesButtonAdding(){
        mFavoritesImageView.setVisibility(View.VISIBLE);
        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        mFavoritesTextView.setText(R.string.add_to_favorites);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).child("uploadID").setValue(addFragmentModel.getUploadIDModel()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        setFavoritesButtonRemoval();
                    }
                });
            }
        });
    }
}
