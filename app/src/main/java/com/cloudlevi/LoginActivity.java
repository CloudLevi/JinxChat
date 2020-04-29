package com.cloudlevi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class LoginActivity extends AppCompatActivity {

    private BottomNavigationView bottom_nav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bottom_nav = findViewById(R.id.bottom_nav);
        setupViews();

    }

    private void setupViews(){
        NavController navController = Navigation.findNavController(LoginActivity.this, R.id.fragNavHost);

        NavigationUI.setupWithNavController(bottom_nav, navController);

    }
}
