package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

                TextView tvSessionUser = findViewById(R.id.tvSessionUser);
                Button btnLogout = findViewById(R.id.btnLogout);

                SharedPreferences preferences =
                        getSharedPreferences("ACB_PREFS", MODE_PRIVATE);

                String username = preferences.getString("username", "");

                tvSessionUser.setText("Welcome, " + username);

                btnLogout.setOnClickListener(v -> {
                    preferences.edit()
                            .remove("username")
                            .putBoolean("isLoggedIn", false)
                            .apply();

                    Toast.makeText(this,
                            "Logged out successfully",
                            Toast.LENGTH_SHORT).show();

                    finish();
                });
            }
        }
