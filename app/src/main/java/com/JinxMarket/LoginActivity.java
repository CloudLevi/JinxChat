package com.JinxMarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class LoginActivity extends AppCompatActivity {

    private NavController navController;

    private BottomNavigationView bottom_nav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bottom_nav = findViewById(R.id.bottom_nav);
        setupViews();
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
