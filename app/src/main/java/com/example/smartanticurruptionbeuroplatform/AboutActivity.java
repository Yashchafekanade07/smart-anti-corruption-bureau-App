package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

                Button btnAboutInfo = findViewById(R.id.btnAboutInfo);

                btnAboutInfo.setOnClickListener(v -> {
                    new AlertDialog.Builder(AboutActivity.this)
                            .setTitle("Project Information")
                            .setMessage(
                                    "Project: Smart Anti Corruption Bureau\n\n" +
                                            "Platform: Android\n" +
                                            "Language: Java\n" +
                                            "Database: SQLite\n\n" +
                                            "This is an educational project prototype."
                            )
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            }
        }