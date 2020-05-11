package com.cloudlevi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountFragment extends Fragment {

    private EditText mPassEditText;
    private CardView mDeleteButton;

    private ProgressBar mProgressBar;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = firebaseUser.getUid();

    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferenceUploads;

    private FirebaseStorage fireBaseStorage;

    private String profilePicImageURL;

    private String defaultImageURL;

    public DeleteAccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_account, container, false);

        mPassEditText = v.findViewById(R.id.delete_PassEditText);
        mDeleteButton = v.findViewById(R.id.delete_confirmBTN);

        mProgressBar = v.findViewById(R.id.deleteFragmentProgress);

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceUploads = FirebaseDatabase.getInstance().getReference("uploads");

        defaultImageURL = "https://firebasestorage.googleapis.com/v0/b/my-application-af75c.appspot.com/o/profilepics%2FDefaultProfilePic.png?alt=media&token=017b6c59-f031-4588-8732-d79c2738317a";

        fireBaseStorage = FirebaseStorage.getInstance();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mUserPassword = mPassEditText.getText().toString();

                if(!mUserPassword.isEmpty()){
                    mProgressBar.setVisibility(View.VISIBLE);

                    AuthCredential credentials = EmailAuthProvider.getCredential(firebaseUser.getEmail(), mUserPassword);

                    firebaseUser.reauthenticate(credentials).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                databaseReferenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        profilePicImageURL = dataSnapshot.child("imageURL").getValue().toString();


                                        databaseReferenceUsers.child(userID).child("UserUploads").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                for(DataSnapshot currentUploadItem: dataSnapshot.getChildren()){

                                                    System.out.println("Inside loop -----");

                                                    final Object currentChild = currentUploadItem.child("uploadID").getValue();
                                                    if(currentChild != null){
                                                        databaseReferenceUploads.child(currentChild.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot uploadItem) {

                                                                String itemImageURL = uploadItem.child("imageURL").getValue().toString();
                                                                fireBaseStorage.getReferenceFromUrl(itemImageURL).delete();

                                                                databaseReferenceUploads.child(currentChild.toString()).removeValue();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }

                                                System.out.println("After loop -----");
                                                deleteUserRecords();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        System.out.println("Before deleting -----");

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthInvalidCredentialsException){
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mPassEditText.setError("Invalid Password");
                                mPassEditText.requestFocus();
                            } else {
                                Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else{
                    mPassEditText.setError("Password Required");
                }
            }
        });
    }

    private void deleteUserRecords(){
        if(!profilePicImageURL.equals(defaultImageURL)){fireBaseStorage.getReferenceFromUrl(profilePicImageURL).delete();}
        databaseReferenceUsers.child(userID).removeValue();

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
