package com.example.smartanticurruptionbeuroplatform;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

public class ComplaintActivity extends Activity {
    private static final int REQ_CAMERA = 701;
    private static final int REQ_LOCATION = 702;
    private EditText etTitle, etDepartment, etDistrict, etDescription;
    private Spinner spCategory;
    private CheckBox cbAnonymous;
    private Button btnSubmit, btnCapture, btnLocation;
    private TextView tvEvidence, tvLocation;
    private DatabaseHelper databaseHelper;
    private Double latitude = null, longitude = null;
    private String evidencePath = null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        etTitle = findViewById(R.id.etComplaintTitle);
        etDepartment = findViewById(R.id.etDepartment);
        etDistrict = findViewById(R.id.etDistrict);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        cbAnonymous = findViewById(R.id.cbAnonymous);
        btnSubmit = findViewById(R.id.btnSubmitComplaint);
        btnCapture = findViewById(R.id.btnCaptureEvidence);
        btnLocation = findViewById(R.id.btnGetLocation);
        tvEvidence = findViewById(R.id.tvEvidenceStatus);
        tvLocation = findViewById(R.id.tvLocationStatus);
        databaseHelper = new DatabaseHelper(this);

        String[] categories = {"Select Category", "Bribery", "Public Works Fraud", "Land Grabbing",
                "Police Misconduct", "Health Department", "Education Department", "Tender Fraud",
                "Scheme Fraud", "Revenue Department", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnCapture.setOnClickListener(v -> captureEvidence());
        btnLocation.setOnClickListener(v -> getLocation());
        btnSubmit.setOnClickListener(v -> submitComplaint());
    }

    private void captureEvidence() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
            return;
        }
        launchCamera();
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No camera app available on this device", Toast.LENGTH_LONG).show();
            return;
        }
        startActivityForResult(intent, REQ_CAMERA);
    }

    private void getLocation() {
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!fine && !coarse) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOCATION);
            return;
        }
        try {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location best = null;
            for (String provider : manager.getProviders(true)) {
                if ((provider.equals(LocationManager.GPS_PROVIDER) && !fine) ||
                    (provider.equals(LocationManager.NETWORK_PROVIDER) && !coarse)) continue;
                Location candidate = manager.getLastKnownLocation(provider);
                if (candidate != null && (best == null || candidate.getTime() > best.getTime())) best = candidate;
            }
            if (best == null) {
                tvLocation.setText("Location not available yet. Turn on Location and try again outdoors.");
                Toast.makeText(this, "No recent location fix found. Try again in a moment.", Toast.LENGTH_LONG).show();
                return;
            }
            latitude = best.getLatitude();
            longitude = best.getLongitude();
            tvLocation.setText(String.format(java.util.Locale.US, "Location attached: %.6f, %.6f", latitude, longitude));
        } catch (SecurityException ex) {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQ_CAMERA) launchCamera();
            else if (requestCode == REQ_LOCATION) getLocation();
        } else {
            Toast.makeText(this, "Permission denied. You can still submit without this optional attachment.", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Object image = data.getExtras().get("data");
            if (!(image instanceof Bitmap)) {
                Toast.makeText(this, "Camera did not return an image. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                File folder = new File(getFilesDir(), "complaint_evidence");
                if (!folder.exists() && !folder.mkdirs()) throw new Exception("Could not create evidence folder");
                File file = new File(folder, "evidence_" + System.currentTimeMillis() + ".jpg");
                try (FileOutputStream out = new FileOutputStream(file)) {
                    ((Bitmap) image).compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
                evidencePath = file.getAbsolutePath();
                tvEvidence.setText("Photo evidence attached: " + file.getName() + " (stored privately on this device)");
            } catch (Exception e) {
                Toast.makeText(this, "Could not save photo evidence", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void submitComplaint() {
        String title = etTitle.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        if (title.length() < 4) { etTitle.setError("Enter a title with at least 4 characters"); etTitle.requestFocus(); return; }
        if (spCategory.getSelectedItemPosition() == 0) { Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show(); return; }
        if (department.isEmpty()) { etDepartment.setError("Enter department name"); etDepartment.requestFocus(); return; }
        if (district.isEmpty()) { etDistrict.setError("Enter district"); etDistrict.requestFocus(); return; }
        if (description.length() < 15) { etDescription.setError("Please provide at least 15 characters of detail"); etDescription.requestFocus(); return; }

        btnSubmit.setEnabled(false);
        long id = databaseHelper.addComplaint(title, spCategory.getSelectedItem().toString(), department,
                district, description, cbAnonymous.isChecked(), latitude, longitude, evidencePath);
        btnSubmit.setEnabled(true);
        if (id > 0) {
            String attachmentSummary = (latitude == null ? "\nLocation: not attached" :
                    String.format(java.util.Locale.US, "\nLocation: %.6f, %.6f", latitude, longitude)) +
                    (evidencePath == null ? "\nPhoto evidence: not attached" : "\nPhoto evidence: attached");
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Complaint submitted")
                    .setMessage("Your local complaint reference is #" + id + ". Save this number to track status.\n\nStatus: Submitted" + attachmentSummary)
                    .setPositiveButton("Done", (dialog, which) -> finish())
                    .show();
        } else {
            Toast.makeText(this, "Could not save complaint. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
