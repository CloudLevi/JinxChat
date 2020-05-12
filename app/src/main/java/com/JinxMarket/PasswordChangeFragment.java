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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangeFragment extends Fragment {

    private EditText oldPassEditText;
    private EditText newPassEditText;

    private CardView updateBTN;

    private String email;
    private String oldPassword;
    private String newPassword;

    private ProgressBar progress;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public PasswordChangeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_password_change, container, false);

        oldPassEditText = v.findViewById(R.id.oldPassEditText);
        newPassEditText = v.findViewById(R.id.newPassEditText);

        updateBTN = v.findViewById(R.id.pass_confirm_Btn);

        progress = v.findViewById(R.id.changePassFragmentProgress);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

            updateBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldPassword =  oldPassEditText.getText().toString().trim();
                    newPassword = newPassEditText.getText().toString().trim();
                    if(oldPassword.isEmpty()){
                        oldPassEditText.setError("Password Required");
                        if(newPassword.isEmpty()){
                            newPassEditText.setError("Password Required");
                        }
                    }
                    else{
                        progress.setVisibility(View.VISIBLE);
                        AuthCredential credentials = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);

                        firebaseUser.reauthenticate(credentials)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            firebaseUser.updatePassword(newPassword)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getContext(), "Changed successfully", Toast.LENGTH_SHORT).show();
                                                        navController.navigate(R.id.action_passwordChangeFragment_to_profileFragment);
                                                    }else{
                                                        progress.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.setVisibility(View.INVISIBLE);
                                if (e instanceof FirebaseAuthInvalidCredentialsException){
                                    oldPassEditText.setError("Invalid Password");
                                    oldPassEditText.requestFocus();
                                }else{
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
    }
}
