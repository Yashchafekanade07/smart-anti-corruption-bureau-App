package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentRecordsActivity extends AppCompatActivity {

    EditText etStudentName, etStudentCourse, etStudentMarks;
    Button btnSaveStudent;
    ListView listStudents;

    StudentDatabaseHelper databaseHelper;
    ArrayList<String> studentList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_records);

                etStudentName = findViewById(R.id.etStudentName);
                etStudentCourse = findViewById(R.id.etStudentCourse);
                etStudentMarks = findViewById(R.id.etStudentMarks);
                btnSaveStudent = findViewById(R.id.btnSaveStudent);
                listStudents = findViewById(R.id.listStudents);

                databaseHelper = new StudentDatabaseHelper(this);

                studentList = new ArrayList<>();
                adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        studentList
                );
                listStudents.setAdapter(adapter);


        listStudents.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    String selected = studentList.get(position);

                    int studentId = Integer.parseInt(
                            selected.split("\n")[0].replace("ID: ", "")
                    );

                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Student Record")
                            .setItems(new String[]{"Edit", "Delete"},
                                    (dialog, which) -> {

                                        if (which == 0) {
                                            editStudent(studentId, selected);
                                        } else {
                                            new android.app.AlertDialog.Builder(this)
                                                    .setTitle("Delete Student")
                                                    .setMessage("Are you sure you want to delete this student record?")
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setNegativeButton("Cancel",
                                                            (d, w) -> {
                                                                Toast.makeText(
                                                                        StudentRecordsActivity.this,
                                                                        "Delete cancelled",
                                                                        Toast.LENGTH_SHORT
                                                                ).show();
                                                            })
                                                    .setPositiveButton("Delete",
                                                            (d, w) -> {
                                                                boolean deleted =
                                                                        databaseHelper.deleteStudent(studentId);

                                                                if (deleted) {
                                                                    loadStudents();
                                                                    Toast.makeText(
                                                                            StudentRecordsActivity.this,
                                                                            "Student deleted successfully",
                                                                            Toast.LENGTH_SHORT
                                                                    ).show();
                                                                } else {
                                                                    Toast.makeText(
                                                                            StudentRecordsActivity.this,
                                                                            "Unable to delete student",
                                                                            Toast.LENGTH_SHORT
                                                                    ).show();
                                                                }
                                                            })
                                                    .show();
                                        }
                                    })
                            .show();

                    return true;
                });

        loadStudents();

        btnSaveStudent.setOnClickListener(v -> saveStudent());
    }

    private void saveStudent() {
                String name = etStudentName.getText().toString().trim();
                String course = etStudentCourse.getText().toString().trim();
                String marksText = etStudentMarks.getText().toString().trim();

                if (name.isEmpty() || course.isEmpty() || marksText.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double marks = Double.parseDouble(marksText);

                    if (marks < 0 || marks > 100) {
                        etStudentMarks.setError("Enter marks between 0 and 100");
                        return;
                    }

                    boolean saved = databaseHelper.addStudent(name, course, marks);

                    if (saved) {
                        Toast.makeText(this, "Student saved successfully",
                                Toast.LENGTH_SHORT).show();

                        etStudentName.setText("");
                        etStudentCourse.setText("");
                        etStudentMarks.setText("");

                        loadStudents();
                    } else {
                        Toast.makeText(this, "Unable to save student",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    etStudentMarks.setError("Enter valid marks");
                }
            }

            private void loadStudents() {
                studentList.clear();

                try (Cursor cursor = databaseHelper.getAllStudents()) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);
                        String course = cursor.getString(2);
                        double marks = cursor.getDouble(3);

                        studentList.add(
                                "ID: " + id +
                                        "\nName: " + name +
                                        "\nCourse: " + course +
                                        "\nMarks: " + marks
                        );
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onDestroy() {
                super.onDestroy();
                if (databaseHelper != null) {
                    databaseHelper.close();
                }
            }

    private void editStudent(int id, String selected) {
        String[] lines = selected.split("\n");

        EditText nameInput = new EditText(this);
        nameInput.setHint("Student Name");
        nameInput.setText(lines[1].replace("Name: ", ""));

        EditText courseInput = new EditText(this);
        courseInput.setHint("Course");
        courseInput.setText(lines[2].replace("Course: ", ""));

        EditText marksInput = new EditText(this);
        marksInput.setHint("Marks");
        marksInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );
        marksInput.setText(lines[3].replace("Marks: ", ""));

        android.widget.LinearLayout layout =
                new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 0);

        layout.addView(nameInput);
        layout.addView(courseInput);
        layout.addView(marksInput);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Edit Student")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", (dialog, which) -> {
                    try {
                        String name = nameInput.getText().toString().trim();
                        String course = courseInput.getText().toString().trim();
                        double marks = Double.parseDouble(
                                marksInput.getText().toString().trim()
                        );

                        if (name.isEmpty() || course.isEmpty()
                                || marks < 0 || marks > 100) {
                            Toast.makeText(this, "Enter valid details",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (databaseHelper.updateStudent(
                                id, name, course, marks)) {
                            loadStudents();
                            Toast.makeText(this, "Student updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Enter valid marks",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
        }
