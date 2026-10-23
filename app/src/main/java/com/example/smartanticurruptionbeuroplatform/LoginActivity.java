package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Locale;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

/** Role-aware sign-in. Officer/admin emails must be explicitly allow-listed; this client-side
 * allowlist is suitable only for a classroom demo. Production must use server-verified claims. */
public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private RadioGroup rgLoginRole;
    private FirebaseAuth mAuth;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        rgLoginRole = findViewById(R.id.rgLoginRole);

        rgLoginRole.setOnCheckedChangeListener((group, checkedId) -> {
            boolean citizen = checkedId == R.id.rbCitizen;
            btnRegister.setEnabled(citizen);
            btnRegister.setAlpha(citizen ? 1f : 0.45f);
            btnRegister.setText(citizen ? "Create Citizen Account" : "Citizen registration only");
        });
        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> {
            if (rgLoginRole.getCheckedRadioButtonId() != R.id.rbCitizen) {
                Toast.makeText(this, "Only citizens can self-register. Officer/admin accounts must be provisioned by the administrator.", Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(this, RegisterActivity.class));
        });
        tvForgotPassword.setOnClickListener(v -> showForgotPassword());
    }

    
private void login() {
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString();

    int selected = rgLoginRole.getCheckedRadioButtonId();
    String role = selected == R.id.rbOfficer ? "OFFICER"
            : selected == R.id.rbAdmin ? "ADMIN" : "CITIZEN";

    if (email.isEmpty()) {
        etEmail.setError("Enter your registered email");
        return;
    }

    if (password.isEmpty()) {
        etPassword.setError("Enter your password");
        return;
    }

    btnLogin.setEnabled(false);

    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
        if (!task.isSuccessful() || mAuth.getCurrentUser() == null) {
            btnLogin.setEnabled(true);
            String message = task.getException() != null
                    ? task.getException().getLocalizedMessage()
                    : "Login failed";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String authEmail = mAuth.getCurrentUser().getEmail();

        if ("CITIZEN".equals(role)) {
            btnLogin.setEnabled(true);
            saveRoleAndOpenHome("CITIZEN", authEmail);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("User")
                .document(uid)
                .get()
                .addOnCompleteListener(this, roleTask -> {
            btnLogin.setEnabled(true);

            if (!roleTask.isSuccessful()
                    || roleTask.getResult() == null
                    || !roleTask.getResult().exists()) {
                mAuth.signOut();
                Toast.makeText(this,
                        "Staff role not found. Check Firestore User document and rules.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String savedRole = roleTask.getResult().getString("role");
            String savedEmail = roleTask.getResult().getString("email");

            boolean emailMatches = authEmail != null
                    && savedEmail != null
                    && authEmail.equalsIgnoreCase(savedEmail.trim())
                    && authEmail.equalsIgnoreCase(email.trim());

            if (!emailMatches || !role.equals(savedRole)) {
                mAuth.signOut();
                getSharedPreferences("ACB_PREFS", MODE_PRIVATE)
                        .edit().clear().apply();
                Toast.makeText(this,
                        "This account is not authorized for " + role,
                        Toast.LENGTH_LONG).show();
                return;
            }

            saveRoleAndOpenHome(role, authEmail);
        });
    });
}

private void saveRoleAndOpenHome(String role, String email) {
    getSharedPreferences("ACB_PREFS", MODE_PRIVATE)
            .edit()
            .putString("user_role", role)
            .putString("user_email", email)
            .apply();

    Toast.makeText(this, role + " login successful",
            Toast.LENGTH_SHORT).show();

    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}

    private void showForgotPassword() {
        EditText input = new EditText(this);
        input.setHint("Enter your registered email");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        new AlertDialog.Builder(this).setTitle("Reset password").setMessage("We will email a password reset link.").setView(input)
                .setPositiveButton("Send link", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (email.isEmpty()) { Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show(); return; }
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> Toast.makeText(this,
                            task.isSuccessful() ? "Reset link sent if the account exists" : "Could not send reset link. Check Firebase setup.", Toast.LENGTH_LONG).show());
                }).setNegativeButton("Cancel", null).show();
    }
}
