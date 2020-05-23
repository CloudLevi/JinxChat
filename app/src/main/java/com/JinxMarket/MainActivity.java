package com.JinxMarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    EditText username,email,password;

    FirebaseAuth auth;
    DatabaseReference databasereference;
    StorageReference storageref;
    String profPicDownloadURL;

    ProgressBar progress;

    LinearLayout usernameForm;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0);
        SharedPreferences currentUserPrefs = getSharedPreferences("CurrentUserPrefs", 0);
        Boolean isNightModeOn = appSettingPrefs.getBoolean("NightMode", false);


        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        CardView loginbtn = findViewById(R.id.loginbtn);
        CardView registerbtn = findViewById(R.id.registerbtn);

        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        usernameForm = findViewById(R.id.usernameForm);

        progress = findViewById(R.id.activityMainProgress);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringLogin = email.getText().toString();
                String stringPass = password.getText().toString();

                if(stringLogin.isEmpty() || stringPass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fill in the blanks", Toast.LENGTH_SHORT).show();
                }
                else{
                    progress.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(stringLogin, stringPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameForm.getVisibility() == View.GONE){

                    Toast.makeText(MainActivity.this, "Please choose a username", Toast.LENGTH_SHORT).show();
                    View view = findViewById(R.id.usernameForm);
                    view.setVisibility(View.VISIBLE);

                }
                else {
                final String stringUsername = username.getText().toString().trim();
                final String stringLogin = email.getText().toString().trim();
                final String stringPass = password.getText().toString().trim();

                if(stringUsername.isEmpty() || stringLogin.isEmpty() || stringPass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fill in the blanks", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6){
                    Toast.makeText(MainActivity.this, "The password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    progress.setVisibility(View.VISIBLE);

                    DatabaseReference userNameReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    userNameReference.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean userNameExists = false;
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if(data.child("username").getValue().toString().equals(stringUsername)) {
                                            userNameExists = true;
                                        }
                                    }

                                    if(!userNameExists){
                                        register(stringUsername, stringLogin, stringPass);
                                    }
                                    else{
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MainActivity.this, "This username is unavailable", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            }
                    );
                }

            }}
        });


    }

    private void register(final String stringUsername, String stringLogin, String stringPass) {
        auth.createUserWithEmailAndPassword(stringLogin, stringPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            final String userId = firebaseUser.getUid();

                            databasereference = FirebaseDatabase.getInstance().getReference("Users").child(userId);


                            storageref = FirebaseStorage.getInstance().getReference().child("profilepics").child("DefaultProfilePic.png");
                            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    profPicDownloadURL = uri.toString();

                                    UserModel userModel = new UserModel(userId,
                                            stringUsername,
                                            profPicDownloadURL,
                                            "offline");

                                    databasereference.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });


                        } else {
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "These credentials are unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
