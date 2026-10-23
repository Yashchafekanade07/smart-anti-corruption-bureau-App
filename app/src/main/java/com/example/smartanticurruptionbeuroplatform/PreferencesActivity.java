package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity {

    EditText etPreferenceName;
    TextView tvSavedName;
    Button btnSavePreference, btnLoadPreference,
            btnClearPreference;

    SharedPreferences preferences;

    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_NAME = "saved_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

                etPreferenceName = findViewById(R.id.etPreferenceName);
                tvSavedName = findViewById(R.id.tvSavedName);
                btnSavePreference = findViewById(R.id.btnSavePreference);
                btnLoadPreference = findViewById(R.id.btnLoadPreference);
                btnClearPreference = findViewById(R.id.btnClearPreference);

                preferences = getSharedPreferences(
                        PREF_NAME, MODE_PRIVATE);

                btnSavePreference.setOnClickListener(v -> {
                    String name = etPreferenceName.getText()
                            .toString().trim();

                    if (name.isEmpty()) {
                        etPreferenceName.setError("Enter your name");
                        return;
                    }

                    preferences.edit()
                            .putString(KEY_NAME, name)
                            .apply();

                    tvSavedName.setText("Saved Name: " + name);

                    Toast.makeText(this, "Name saved successfully",
                            Toast.LENGTH_SHORT).show();
                });

                btnLoadPreference.setOnClickListener(v -> {
                    String name = preferences.getString(KEY_NAME, "");

                    if (name.isEmpty()) {
                        tvSavedName.setText("No saved name found");
                    } else {
                        tvSavedName.setText("Saved Name: " + name);
                        etPreferenceName.setText(name);
                    }
                });

                btnClearPreference.setOnClickListener(v -> {
                    preferences.edit().remove(KEY_NAME).apply();

                    etPreferenceName.setText("");
                    tvSavedName.setText("Saved name will appear here");

                    Toast.makeText(this, "Saved name cleared",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }
