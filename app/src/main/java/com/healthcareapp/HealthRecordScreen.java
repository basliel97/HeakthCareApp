package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthRecordScreen extends Activity {

    TextView nameTextView;
    TextView dateOfBirthTextView;
    TextView allergiesTextView;
    TextView currentMedicationTextView;
    TextView immunizationTextView;
    TextView labResultsTextView;
    TextView addButton;
    TextView editButton;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_record_history);

        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // Bind TextViews
        nameTextView = findViewById(R.id.name);
        dateOfBirthTextView = findViewById(R.id.dateOfBirth);
        allergiesTextView = findViewById(R.id.allergies);
        currentMedicationTextView = findViewById(R.id.currentMedication);
        immunizationTextView = findViewById(R.id.immunization);
        labResultsTextView = findViewById(R.id.labResults);

        // Bind buttons
        addButton = findViewById(R.id.add);
        editButton = findViewById(R.id.edit);

        // Check if healthcare info exists for the current user
        checkHealthCareInfoExists();

        // Set click listeners for buttons
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(HealthRecordScreen.this, HealthRecordEditor.class);
            startActivity(intent);
        });

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(HealthRecordScreen.this, HealthRecordEditor.class);
            startActivity(intent);
        });
    }

    private void checkHealthCareInfoExists() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Check if healthcare info exists for the current user in Firebase Realtime Database
        databaseReference.child("health_records").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has healthcare info, disable Add button and enable Edit button
                    addButton.setEnabled(false);
                    editButton.setEnabled(true);

                    // Populate TextViews with existing data
                    HealthcareInfo healthRecord = dataSnapshot.getValue(HealthcareInfo.class);
                    if (healthRecord != null) {
                        nameTextView.setText("Name: " + healthRecord.getName());
                        dateOfBirthTextView.setText("Date of Birth: " + healthRecord.getDateOfBirth());
                        allergiesTextView.setText("Allergies: " + healthRecord.getAllergies());
                        currentMedicationTextView.setText("Current Medication: " + healthRecord.getCurrentMedication());
                        immunizationTextView.setText("Immunization: " + healthRecord.getImmunization());
                        labResultsTextView.setText("Lab Results: " + healthRecord.getLabResults());
                    }
                } else {
                    // User does not have healthcare info, enable Add button and disable Edit button
                    addButton.setEnabled(true);
                    editButton.setEnabled(false);

                    // Clear TextViews
                    clearTextViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(HealthRecordScreen.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearTextViews() {
        nameTextView.setText("");
        dateOfBirthTextView.setText("");
        allergiesTextView.setText("");
        currentMedicationTextView.setText("");
        immunizationTextView.setText("");
        labResultsTextView.setText("");
    }
}
