package com.example.scoreup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SettingFragment settingFragment = new SettingFragment();
    AccountFragment accountFragment = new AccountFragment();
    String name, email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");


        bottomNavigationView = findViewById(R.id.bottom_nav_main);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle data = new Bundle();
        data.putString("username",name);
        data.putString("email",email);
        accountFragment.setArguments(data);
        fragmentTransaction.replace(R.id.container_main,homeFragment).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container_main,homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.txtHome) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_main, homeFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.txtSetting) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_main, settingFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.txtAccount) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_main, accountFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}