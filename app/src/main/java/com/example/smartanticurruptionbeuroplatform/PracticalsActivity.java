
package com.example.smartanticurruptionbeuroplatform;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practicals);

        Button intentDemo = findViewById(R.id.btnIntentDemo);
        Button fragmentDemo = findViewById(R.id.btnFragmentDemo);
        Button cameraDemo = findViewById(R.id.btnCameraDemo);
        Button locationDemo = findViewById(R.id.btnLocationDemo);

        Button preferencesDemo = findViewById(R.id.btnPreferencesDemo);
        Button notificationDemo = findViewById(R.id.btnNotificationDemo);
        Button popupMenuDemo = findViewById(R.id.btnPopupMenuDemo);
        Button sqliteDemo = findViewById(R.id.btnSqliteDemo);


        intentDemo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PracticalsActivity.this,
                    IntentDemoActivity.class
            );
            intent.putExtra("message", "Welcome to Intent Demo Practical!");
            startActivity(intent);
        });


        fragmentDemo.setOnClickListener(v ->
                startActivity(new Intent(
                        PracticalsActivity.this,
                        FragmentDemoActivity.class
                ))
        );

        cameraDemo.setOnClickListener(v ->
                startActivity(new Intent(
                        PracticalsActivity.this,
                        ImageCaptureActivity.class
                ))
        );

        locationDemo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PracticalsActivity.this,
                    LocationActivity.class
            );
            startActivity(intent);
        });



        preferencesDemo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PracticalsActivity.this,
                    PreferencesActivity.class
            );
            startActivity(intent);
        });

        notificationDemo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PracticalsActivity.this,
                    MainActivity.class
            );

            intent.putExtra("show_notification", true);
            startActivity(intent);
        });

        popupMenuDemo.setOnClickListener(v -> {
            android.widget.PopupMenu popup =
                    new android.widget.PopupMenu(
                            PracticalsActivity.this,
                            popupMenuDemo
                    );

            popup.getMenu().add("Home");
            popup.getMenu().add("My Complaints");
            popup.getMenu().add("About");

            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(
                        PracticalsActivity.this,
                        item.getTitle() + " selected",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            });

            popup.show();
        });


        sqliteDemo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PracticalsActivity.this,
                    StudentRecordsActivity.class
            );
            startActivity(intent);
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
