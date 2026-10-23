
package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ACB_PREFS";
    private static final String KEY_ROLE = "user_role";
    private static final int NOTIFICATION_PERMISSION_REQUEST = 100;

    private AirplaneModeReceiver airplaneModeReceiver;

    private SharedPreferences getRolePrefs() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private String getCurrentRole() {
        return getRolePrefs().getString(KEY_ROLE, "GUEST");
    }

    private boolean isSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private boolean isCitizen() {
        return isSignedIn() && "CITIZEN".equals(getCurrentRole());
    }

    private boolean isStaff() {
        String role = getCurrentRole();

        return isSignedIn()
                && ("OFFICER".equals(role) || "ADMIN".equals(role));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Smart ACB");
        }

        updateHomeScreen();

        if (getIntent().getBooleanExtra("show_notification", false)) {
            getIntent().removeExtra("show_notification");
            showACBNotification();
        }

        // MAD Practicals
        findViewById(R.id.btnMadPracticals).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        PracticalsActivity.class
                ))
        );

        // Login button
        findViewById(R.id.btnLogin).setOnClickListener(v ->
                openLoginForRole()
        );

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            getRolePrefs().edit().clear().apply();

            Toast.makeText(
                    MainActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        // Staff Dashboard: Admin and Officer
        findViewById(R.id.btnAdminDashboard).setOnClickListener(v -> {
            if (!isSignedIn()) {
                getRolePrefs().edit().clear().apply();
                openLoginForRole();
                return;
            }

            String currentRole = getCurrentRole();

            if (!"OFFICER".equals(currentRole)
                    && !"ADMIN".equals(currentRole)) {

                Toast.makeText(
                        MainActivity.this,
                        "Please sign in using an approved staff account",
                        Toast.LENGTH_LONG
                ).show();

                openLoginForRole();
                return;
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Staff Case Management")
                    .setMessage(
                            "You are signed in as " + currentRole
                                    + ". This prototype stores complaints "
                                    + "on this device only."
                    )
                    .setPositiveButton("Open dashboard", (dialog, which) ->
                            startActivity(new Intent(
                                    MainActivity.this,
                                    AdminDashboardActivity.class
                            ))
                    )
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // My Complaints
        findViewById(R.id.btnMyComplaints).setOnClickListener(v -> {
            if (!isCitizen()) {
                openLoginForRole();
                return;
            }

            startActivity(new Intent(
                    MainActivity.this,
                    MyComplaintsActivity.class
            ));
        });

        // File Complaint
        findViewById(R.id.btnFileComplaint).setOnClickListener(v -> {
            if (!isCitizen()) {
                openLoginForRole();
                return;
            }

            startActivity(new Intent(
                    MainActivity.this,
                    ComplaintActivity.class
            ));
        });

        // Track Complaint
        findViewById(R.id.btnTrackComplaint).setOnClickListener(v -> {
            if (!isCitizen()) {
                openLoginForRole();
                return;
            }

            startActivity(new Intent(
                    MainActivity.this,
                    TrackComplaintActivity.class
            ));
        });

        // About
        findViewById(R.id.btnAbout).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        AboutActivity.class
                ))
        );
    }

    private void updateHomeScreen() {
        boolean signedIn = isSignedIn();
        boolean citizen = isCitizen();
        boolean staff = isStaff();

        findViewById(R.id.btnFileComplaint)
                .setVisibility(citizen ? View.VISIBLE : View.GONE);

        findViewById(R.id.btnMyComplaints)
                .setVisibility(citizen ? View.VISIBLE : View.GONE);

        findViewById(R.id.btnTrackComplaint)
                .setVisibility(citizen ? View.VISIBLE : View.GONE);

        findViewById(R.id.btnAdminDashboard)
                .setVisibility(staff ? View.VISIBLE : View.GONE);

        findViewById(R.id.btnLogin)
                .setVisibility(signedIn ? View.GONE : View.VISIBLE);

        findViewById(R.id.btnLogout)
                .setVisibility(signedIn ? View.VISIBLE : View.GONE);
    }

    private void openLoginForRole() {
        Toast.makeText(
                this,
                "Please sign in to access your portal",
                Toast.LENGTH_SHORT
        ).show();

        startActivity(new Intent(this, LoginActivity.class));
    }

    public void showACBNotification() {
        String channelId = "acb_notifications";

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (manager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "ACB Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Anti-Corruption Bureau updates");
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST
            );

            Toast.makeText(
                    this,
                    "Allow notification permission and try again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Smart ACB")
                .setContentText("Your app notification is working!")
                .setAutoCancel(true);

        manager.notify(101, builder.build());

        Toast.makeText(
                this,
                "Notification sent",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        airplaneModeReceiver = new AirplaneModeReceiver();

        IntentFilter filter = new IntentFilter(
                Intent.ACTION_AIRPLANE_MODE_CHANGED
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                    airplaneModeReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(airplaneModeReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (airplaneModeReceiver != null) {
            unregisterReceiver(airplaneModeReceiver);
            airplaneModeReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuHome) {
            return true;

        } else if (id == R.id.menuAbout) {
            startActivity(new Intent(
                    MainActivity.this,
                    AboutActivity.class
            ));
            return true;

        } else if (id == R.id.menuLogin) {
            openLoginForRole();
            return true;

        } else if (id == R.id.menuExit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
