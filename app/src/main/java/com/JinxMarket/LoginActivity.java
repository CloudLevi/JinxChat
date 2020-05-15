package com.JinxMarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private NavController navController;

    private BottomNavigationView bottom_nav;

    private DatabaseReference userRef;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private String userStatus;

    public Boolean hey;

    public DeletingViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid());

        bottom_nav = findViewById(R.id.bottom_nav);
        setupViews();

        viewModel = new ViewModelProvider(this).get(DeletingViewModel.class);
        viewModel.setData(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userRef.child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!viewModel.getData().getValue()){
            userRef.child("status").setValue("offline");
        }
    }

    private void setupViews(){
        navController = Navigation.findNavController(LoginActivity.this, R.id.fragNavHost);

        NavigationUI.setupWithNavController(bottom_nav, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.chatFragment){
                    bottom_nav.setVisibility(View.GONE);
                } else{
                    bottom_nav.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
