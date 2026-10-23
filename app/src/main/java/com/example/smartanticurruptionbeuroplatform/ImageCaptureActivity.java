package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
public class ImageCaptureActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private TextView tvImageStatus;
    private Bitmap capturedBitmap;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

                imagePreview = findViewById(R.id.imagePreview);
                tvImageStatus = findViewById(R.id.tvImageStatus);
                Button btnCapture = findViewById(R.id.btnCapture);
                Button btnSaveImage = findViewById(R.id.btnSaveImage);

                cameraLauncher = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null
                                    && result.getData().getExtras() != null) {

                                Object data = result.getData().getExtras().get("data");

                                if (data instanceof Bitmap) {
                                    capturedBitmap = (Bitmap) data;
                                    imagePreview.setImageBitmap(capturedBitmap);
                                    tvImageStatus.setText("Image captured successfully!");
                                }
                            }
                        });

                cameraPermissionLauncher = registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        granted -> {
                            if (granted) {
                                openCamera();
                            } else {
                                Toast.makeText(this,
                                        "Camera permission denied",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                btnCapture.setOnClickListener(v -> {
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

                btnSaveImage.setOnClickListener(v -> saveImage());
            }

            private void openCamera() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    cameraLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Camera app not available",
                            Toast.LENGTH_SHORT).show();
                }
            }

            private void saveImage() {
                if (capturedBitmap == null) {
                    Toast.makeText(this, "First capture an image",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                File directory = new File(getFilesDir(), "ACB_Images");

                if (!directory.exists() && !directory.mkdirs()) {
                    Toast.makeText(this, "Could not create folder",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                File imageFile = new File(directory,
                        "ACB_Image_" + System.currentTimeMillis() + ".jpg");

                try (OutputStream output = new FileOutputStream(imageFile)) {
                    capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);
                    output.flush();

                    tvImageStatus.setText("Image saved in internal storage!");
                    Toast.makeText(this, "Image saved successfully",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Error saving image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
