package com.example.smartanticurruptionbeuroplatform;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.util.Date;

public class TrackComplaintActivity extends AppCompatActivity {
    private EditText etComplaintId;
    private TextView tvTrackResult;
    private DatabaseHelper databaseHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_complaint);
        etComplaintId = findViewById(R.id.etComplaintId);
        Button btnTrack = findViewById(R.id.btnTrack);
        tvTrackResult = findViewById(R.id.tvTrackResult);
        databaseHelper = new DatabaseHelper(this);
        int complaintId = getIntent().getIntExtra("complaint_id", -1);
        if (complaintId > 0) etComplaintId.setText(String.valueOf(complaintId));
        btnTrack.setOnClickListener(v -> trackComplaint());
        if (complaintId > 0) trackComplaint();
    }

    private void trackComplaint() {
        String input = etComplaintId.getText().toString().trim();
        if (input.isEmpty()) { etComplaintId.setError("Enter complaint ID"); return; }
        final int id;
        try { id = Integer.parseInt(input); }
        catch (NumberFormatException e) { etComplaintId.setError("Complaint ID must be a number"); return; }
        if (id <= 0) { etComplaintId.setError("Enter a valid complaint ID"); return; }

        try (Cursor c = databaseHelper.getComplaintById(id)) {
            if (!c.moveToFirst()) { tvTrackResult.setText("No complaint found with reference #" + id + ". Check the ID and try again."); return; }
            String created = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(new Date(c.getLong(8)));
            String location = c.isNull(9) || c.isNull(10) ? "Location: not attached" :
                    String.format(java.util.Locale.US, "Location: %.6f, %.6f", c.getDouble(9), c.getDouble(10));
            String evidence = c.isNull(11) || c.getString(11).trim().isEmpty() ? "Photo evidence: not attached" :
                    "Photo evidence saved on this device: " + new java.io.File(c.getString(11)).getName();
            StringBuilder history = new StringBuilder();
            try (Cursor events = databaseHelper.getComplaintEvents(id)) {
                while (events.moveToNext()) {
                    String date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(new Date(events.getLong(4)));
                    String oldStatus = events.getString(0);
                    history.append("\\n• ").append(date).append(" — ")
                            .append(oldStatus == null || oldStatus.isEmpty() ? events.getString(1) : oldStatus + " → " + events.getString(1))
                            .append(" (").append(events.getString(2)).append(")");
                    if (events.getString(3) != null && !events.getString(3).isEmpty())
                        history.append("\\n  ").append(events.getString(3));
                }
            }
            tvTrackResult.setText("COMPLAINT TRACKING\\n\\n" +
                    "Reference ID: #" + c.getInt(0) + "\\n" +
                    "Title: " + c.getString(1) + "\\n" +
                    "Category: " + c.getString(2) + "\\n" +
                    "Department: " + c.getString(3) + "\\n" +
                    "District: " + c.getString(4) + "\\n\\n" +
                    "Current status: " + c.getString(7) + "\\n" +
                    "Submitted: " + created + "\\n" + location + "\\n" + evidence + "\\n\\n" +
                    "Description: " + c.getString(5) + "\\n\\n" +
                    (c.getInt(6) == 1 ? "Anonymous reporting requested" : "Reporter details not collected in this local prototype") +
                    "\\n\\nSTATUS HISTORY" + (history.length() == 0 ? "\\nNo history available." : history.toString()) +
                    "\\n\\nNote: Evidence is stored locally and is not uploaded to officers.");
        }
    }
}
