package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class EmailChangeFragment extends Fragment {

    private EditText passEditText;
    private EditText emailEditText;

    private RelativeLayout emailLayout;

    private CardView confirmBTN;
    private TextView confirmText;

    private String email;
    private String password;

    private ProgressBar progress;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public EmailChangeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_email_change, container, false);

        passEditText = v.findViewById(R.id.passwordEditText);
        emailEditText = v.findViewById(R.id.emailEditText);
        emailLayout = v.findViewById(R.id.emailLayout);

        confirmBTN = v.findViewById(R.id.confirm_Btn);
        confirmText = v.findViewById(R.id.confirmText);

        progress = v.findViewById(R.id.changeFragmentProgress);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        if(emailLayout.getVisibility() == View.GONE){
            confirmBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password =  passEditText.getText().toString();
                    if(password.isEmpty()){
                        passEditText.setError("Password Required");
                    }
                    else{
                       progress.setVisibility(View.VISIBLE);
                       AuthCredential credentials = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

                       firebaseUser.reauthenticate(credentials)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           progress.setVisibility(View.INVISIBLE);
                                           emailLayout.setVisibility(View.VISIBLE);
                                           confirmText.setText("Confirm new E-mail");
                                           confirmBTN.setOnClickListener(updateEmailListener);
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               progress.setVisibility(View.INVISIBLE);
                               if (e instanceof FirebaseAuthInvalidCredentialsException){
                                   passEditText.setError("Invalid Password");
                                   passEditText.requestFocus();
                               }else{
                                   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                    }
                }
            });}
        }

        private View.OnClickListener updateEmailListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(v);
                email = emailEditText.getText().toString().trim();

                if(email.isEmpty()){
                    emailEditText.setError("Email Required");
                    emailEditText.requestFocus();
                }
                else{
                    progress.setVisibility(View.VISIBLE);
                    firebaseUser.updateEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Changed successfully", Toast.LENGTH_SHORT).show();
                                        navController.navigate(R.id.action_emailChangeFragment_to_profileFragment);
                                    }else{
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        };
    }
