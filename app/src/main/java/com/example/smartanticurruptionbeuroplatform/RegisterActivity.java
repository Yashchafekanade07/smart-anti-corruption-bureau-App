package com.example.smartanticurruptionbeuroplatform;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth auth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        if (name.length() < 2) { etName.setError("Enter your full name"); etName.requestFocus(); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Enter a valid email"); etEmail.requestFocus(); return; }
        if (password.length() < 6) { etPassword.setError("Use at least 6 characters"); etPassword.requestFocus(); return; }
        btnRegister.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                btnRegister.setEnabled(true);
                String message = task.getException() != null ? task.getException().getLocalizedMessage() : "Registration failed";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return;
            }
            if (auth.getCurrentUser() != null) {
                auth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build())
                        .addOnCompleteListener(profileTask -> openHome());
            } else openHome();
        });
    }

    private void openHome() {
        getSharedPreferences("ACB_PREFS", MODE_PRIVATE).edit()
                .putString("user_role", "CITIZEN")
                .putString("user_email", auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "")
                .apply();
        Toast.makeText(this, "Citizen account created successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i); finish();
    }
}
