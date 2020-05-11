package com.cloudlevi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RelativeLayout userForm;
    private RelativeLayout editProfileForm;
    private RelativeLayout emailEditForm;
    private RelativeLayout passwordEditForm;

    private TextView themeChange;
    private TextView userNameTextView;
    private CircleImageView userImageView;

    private TextView emailTextView;

    private DatabaseReference mDataBaseRef;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String firebaseUserName;

    private CardView logout;
    private CardView delete;
    private CardView uploadPic;

    private NavController navController;

    private Uri mImageUri;


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameTextView = v.findViewById(R.id.ProfileFragUsername_tv);
        userImageView = v.findViewById(R.id.ProfileFragUserPic);
        emailTextView = v.findViewById(R.id.ProfileFragEmail_tv);

        logout = v.findViewById(R.id.logoutbtn);
        delete = v.findViewById(R.id.deletebtn);
        uploadPic = v.findViewById(R.id.profilePicBTN);

        userForm = v.findViewById(R.id.ProfileFragUserForm);
        editProfileForm = v.findViewById(R.id.ProfileFrag_Edit_Form);
        emailEditForm = v.findViewById(R.id.ProfileFrag_Email_Form);
        passwordEditForm = v.findViewById(R.id.ProfileFrag_Pass_Form);

        SharedPreferences appSettingPrefs = this.getActivity().getSharedPreferences("AppSettingPrefs", 0);
        final SharedPreferences.Editor sharedPrefsEdit = appSettingPrefs.edit();
        final Boolean isNightModeOn = appSettingPrefs.getBoolean("NightMode", false);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("profilepics/");
        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + firebaseUser.getUid());
        mDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("imageURL").getValue().toString())
                        .into(userImageView);

                firebaseUserName = dataSnapshot.child("username").getValue().toString();
                userNameTextView.setText(firebaseUserName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        emailTextView.setText(firebaseUser.getEmail());


        themeChange = v.findViewById(R.id.ProfileFragTheme_tv);

        themeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefsEdit.putBoolean("NightMode", false);
                    sharedPrefsEdit.apply();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefsEdit.putBoolean("NightMode", true);
                    sharedPrefsEdit.apply();
                }
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_deleteAccountFragment);
            }
        });

        userForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putString("userID", firebaseUser.getUid());
                navController.navigate(R.id.action_profileFragment_to_userPagerAdapterFragment, bundle);
            }
        });

        editProfileForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putString("userID", firebaseUser.getUid());
                navController.navigate(R.id.action_profileFragment_to_userAboutMeEditFragment, bundle);
            }
        });



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        emailEditForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_emailChangeFragment);
            }
        });

        passwordEditForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_passwordChangeFragment);
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.get()
                    .load(mImageUri)
                    .into(userImageView);
            uploadFile();
        }
    }

    private void uploadFile(){
        if(mImageUri != null){

            mStorageRef.child(firebaseUserName + "ProfilePic").delete();
            final StorageReference fileReference = mStorageRef.child(firebaseUserName +
                    "ProfilePic");


            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mDataBaseRef.child("imageURL").setValue(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_SHORT).show();
        }

    }
}
