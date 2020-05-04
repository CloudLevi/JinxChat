package com.cloudlevi;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class UserAboutMeFragment extends Fragment {
    private ImageView mUserImageView;
    private TextView mUserNameTextView;
    private TextView mGenderTextView;
    private TextView mBirthDayTextView;
    private TextView mLocationTextView;
    private TextView mBioTextView;

    private DatabaseReference mDataBaseRefPrimary;
    private DatabaseReference mDataBaseRefAdditional;
    private String userID;

    public static final String PAGE_TITLE = "About me";

    public UserAboutMeFragment(String mUserID) {
        userID = mUserID;
    }

    public UserAboutMeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_about_me, container, false);

        mUserImageView = v.findViewById(R.id.user_AboutMe_Image);
        mUserNameTextView = v.findViewById(R.id.user_AboutMe_UserName);
        mGenderTextView = v.findViewById(R.id.user_AboutMe_Gender);
        mBirthDayTextView = v.findViewById(R.id.user_AboutMe_BirthDay);
        mLocationTextView = v.findViewById(R.id.user_AboutMe_Location);
        mBioTextView = v.findViewById(R.id.user_AboutMe_Bio);

        mDataBaseRefPrimary = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        mDataBaseRefAdditional = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/UserDetails");

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataBaseRefPrimary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("imageURL").getValue().toString())
                        .into(mUserImageView);
                mUserNameTextView.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDataBaseRefAdditional.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAboutMeModel userInfo = dataSnapshot.getValue(UserAboutMeModel.class);

                if(userInfo != null){

                if(userInfo.getGenderModel() != null){mGenderTextView.append(" " + userInfo.getGenderModel());}
                if(userInfo.getBirthdayModel() != null){mBirthDayTextView.append(" " + userInfo.getBirthdayModel());}
                if(userInfo.getCountryModel() != null ){
                    mLocationTextView.append(" " + userInfo.getCountryModel());
                    if(userInfo.getCityModel() != null){
                        if(!userInfo.getCityModel().equals("")){
                        mLocationTextView.append(", " + userInfo.getCityModel());
                        }
                    }
                }else {
                    if(userInfo.getCityModel() != null ){
                        mLocationTextView.append(" " + userInfo.getCityModel());
                    }
                }
                if(userInfo.getBioModel() != null){mBioTextView.setText(" " + userInfo.getBioModel());}

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
