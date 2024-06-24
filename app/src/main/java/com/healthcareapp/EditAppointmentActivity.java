package com.healthcareapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAppointmentActivity extends Activity {

    private EditText editTextDOA, editTextTime, editTextNotes;
    private TextView buttonSave;
    private DatabaseReference databaseReference;
    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_editor);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Bind EditTexts and Button
        editTextDOA = findViewById(R.id.editTextApointmentDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextNotes = findViewById(R.id.editTextNote);
        buttonSave = findViewById(R.id.editAppointment);

        // Retrieve appointment data from Intent
        appointmentId = getIntent().getStringExtra("appointmentId");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String notes = getIntent().getStringExtra("notes");

        // Populate EditTexts with existing data
        editTextDOA.setText(date);
        editTextTime.setText(time);
        editTextNotes.setText(notes);

        // Set onClick listener for the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSaveAppointment();
            }
        });
    }

    private void validateAndSaveAppointment() {
        // Retrieve values from EditText fields
        String date = editTextDOA.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        // Check if any of the fields are empty
        if (TextUtils.isEmpty(date)) {
            editTextDOA.setError("Date is required");
            editTextDOA.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            editTextTime.setError("Time is required");
            editTextTime.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(notes)) {
            editTextNotes.setError("Notes are required");
            editTextNotes.requestFocus();
            return;
        }

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Create an updated Appointment object
        Appointment updatedAppointment = new Appointment(appointmentId, userId, date, time, notes);

        // Save the updated Appointment object to Firebase Realtime Database
        databaseReference.child("appointments").child(appointmentId).setValue(updatedAppointment)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully saved
                    Toast.makeText(EditAppointmentActivity.this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                    Toast.makeText(EditAppointmentActivity.this, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
