package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

public class IntentDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_demo);

                TextView tvIntentMessage =
                        findViewById(R.id.tvIntentMessage);
                Button btnIntentBack =
                        findViewById(R.id.btnIntentBack);

                Intent receivedIntent = getIntent();
                String message = receivedIntent.getStringExtra("message");

                if (message != null) {
                    tvIntentMessage.setText(message);
                }

                btnIntentBack.setOnClickListener(v -> finish());
            }
        }
