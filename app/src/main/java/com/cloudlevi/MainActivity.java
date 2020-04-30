package com.cloudlevi;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    EditText username,email,password;

    FirebaseAuth auth;
    DatabaseReference reference;

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

        CardView loginbtn = findViewById(R.id.loginbtn);
        CardView registerbtn = findViewById(R.id.registerbtn);

        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        usernameForm = findViewById(R.id.usernameForm);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringLogin = email.getText().toString();
                String stringPass = password.getText().toString();

                if(TextUtils.isEmpty(stringLogin) || TextUtils.isEmpty(stringPass)){
                    Toast.makeText(MainActivity.this, "Fill in the blanks", Toast.LENGTH_SHORT).show();
                }
                else{
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

                if(TextUtils.isEmpty(stringUsername) || TextUtils.isEmpty(stringLogin) || TextUtils.isEmpty(stringPass)){
                    Toast.makeText(MainActivity.this, "Fill in the blanks", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6){
                    Toast.makeText(MainActivity.this, "The password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference userNameReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    userNameReference.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int count = 0;
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        System.out.println(data);
                                        System.out.println(data.child("username"));
                                        System.out.println(data.child("username").getValue());
                                        if(data.child("username").getValue().toString().equals(stringUsername)) {
                                            System.out.println("YEAH");
                                            count++;
                                        }
                                    }

                                    if(count == 0){
                                        register(stringUsername, stringLogin, stringPass);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "This username is unavailable", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println(databaseError.getMessage());
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
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", stringUsername);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "These credentials are unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
