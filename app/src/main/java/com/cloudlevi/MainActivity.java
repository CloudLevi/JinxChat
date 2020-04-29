package com.cloudlevi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    EditText login,password;

    FirebaseAuth auth;
    DatabaseReference reference;

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

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringLogin = login.getText().toString();
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
                                        Toast.makeText(MainActivity.this, "Authentication falied", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringLogin = login.getText().toString().trim();
                String stringPass = password.getText().toString().trim();

                if(TextUtils.isEmpty(stringLogin) || TextUtils.isEmpty(stringPass)){
                    Toast.makeText(MainActivity.this, "Fill in the blanks", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6){
                    Toast.makeText(MainActivity.this, "The password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    register(stringLogin, stringPass);
                }

            }
        });


    }

    private void register(String stringLogin, String stringPass) {
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
