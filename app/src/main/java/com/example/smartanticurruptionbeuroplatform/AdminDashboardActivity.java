package com.example.smartanticurruptionbeuroplatform;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/** Local classroom demo only. This PIN is not security; production needs server-side admin roles. */
public class AdminDashboardActivity extends AppCompatActivity {
    private static final String[] STATUSES = {"Submitted", "Under Review", "In Progress", "Resolved", "Rejected"};
    private DatabaseHelper db;
    private ListView list;
    private TextView empty;
    private final ArrayList<Integer> ids = new ArrayList<>();
    private final ArrayList<String> labels = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        db = new DatabaseHelper(this);
        list = findViewById(R.id.listAdminComplaints);
        empty = findViewById(R.id.tvAdminEmpty);
        findViewById(R.id.btnAdminRefresh).setOnClickListener(v -> load());
        list.setOnItemClickListener((parent, view, position, itemId) -> {
            if (position >= 0 && position < ids.size()) showCaseActions(ids.get(position), labels.get(position));
        });
        load();
    }

    private void load() {
        ids.clear(); labels.clear();
        try (Cursor c = db.getComplaintDetailsForAdmin()) {
            while (c.moveToNext()) {
                ids.add(c.getInt(0));
                labels.add(c.getString(1));
                labels.set(labels.size() - 1, c.getString(4));
            }
        }
        ArrayList<String> rows = new ArrayList<>();
        try (Cursor c = db.getComplaintDetailsForAdmin()) {
            while (c.moveToNext()) rows.add("#" + c.getInt(0) + "  •  " + c.getString(4) + "\n" + c.getString(1) + "\n" + c.getString(2) + " — " + c.getString(3) + "\nTap to change status");
        }
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows));
        empty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
        ((TextView)findViewById(R.id.tvAdminCount)).setText(
                "Total: " + db.getComplaintCount() +
                "   •   Submitted: " + db.getComplaintCountForStatus("Submitted") +
                "   •   In progress: " + db.getComplaintCountForStatus("In Progress") +
                "   •   Resolved: " + db.getComplaintCountForStatus("Resolved"));
    }

    private void showCaseActions(int complaintId, String currentStatus) {
        new AlertDialog.Builder(this)
                .setTitle("Case #" + complaintId + " — " + currentStatus)
                .setItems(new String[]{"Update status", "View status history"}, (dialog, which) -> {
                    if (which == 0) showStatusDialog(complaintId, currentStatus);
                    else showHistory(complaintId);
                }).setNegativeButton("Close", null).show();
    }

    private void showStatusDialog(int complaintId, String currentStatus) {
        new AlertDialog.Builder(this).setTitle("Update complaint #" + complaintId)
                .setSingleChoiceItems(STATUSES, indexOf(currentStatus), (dialog, which) -> {
                    boolean updated = db.updateComplaintStatus(complaintId, STATUSES[which]);
                    Toast.makeText(this, updated ? "Status updated to " + STATUSES[which] : "Could not update status", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); load();
                }).setNegativeButton("Cancel", null).show();
    }

    private void showHistory(int complaintId) {
        ArrayList<String> events = new ArrayList<>();
        try (Cursor c = db.getComplaintEvents(complaintId)) {
            while (c.moveToNext()) {
                String date = java.text.DateFormat.getDateTimeInstance(
                        java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT)
                        .format(new java.util.Date(c.getLong(4)));
                String transition = c.getString(0) == null || c.getString(0).isEmpty()
                        ? c.getString(1) : c.getString(0) + " → " + c.getString(1);
                events.add(date + "\\n" + transition + "\\n" + c.getString(2) + " — " + c.getString(3));
            }
        }
        if (events.isEmpty()) events.add("No history recorded yet.");
        new AlertDialog.Builder(this).setTitle("Audit trail — Case #" + complaintId)
                .setItems(events.toArray(new String[0]), null)
                .setPositiveButton("Close", null).show();
    }

    private int indexOf(String status) {
        for (int i = 0; i < STATUSES.length; i++) if (STATUSES[i].equals(status)) return i;
        return 0;
    }
}
