package com.example.smartanticurruptionbeuroplatform;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyComplaintsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ListView listComplaints;
    private TextView tvEmpty;
    private final ArrayList<Integer> complaintIds = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);
        listComplaints = findViewById(R.id.listComplaints);
        tvEmpty = findViewById(R.id.tvEmpty);
        databaseHelper = new DatabaseHelper(this);
        listComplaints.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < complaintIds.size()) {
                android.content.Intent intent = new android.content.Intent(this, TrackComplaintActivity.class);
                intent.putExtra("complaint_id", complaintIds.get(position));
                startActivity(intent);
            }
        });
        loadComplaints();
    }

    private void loadComplaints() {
        ArrayList<String> rows = new ArrayList<>();
        complaintIds.clear();
        try (Cursor c = databaseHelper.getAllComplaints()) {
            while (c.moveToNext()) {
                int id = c.getInt(0); complaintIds.add(id);
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(c.getLong(5)));
                rows.add("#" + id + "  •  " + c.getString(4) + "\n" + c.getString(1) +
                        "\n" + c.getString(2) + " — " + c.getString(3) + "\nSubmitted: " + date + "\nTap to view details");
            }
        }
        listComplaints.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows));
        tvEmpty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override protected void onResume() { super.onResume(); if (databaseHelper != null) loadComplaints(); }
}
