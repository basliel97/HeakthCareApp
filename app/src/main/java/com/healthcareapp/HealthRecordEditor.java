package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthRecordEditor extends Activity {

    EditText editTextName;
    EditText editTextDOB;
    EditText editTextAllergies;
    EditText editTextMedications;
    EditText editTextImmunizations;
    EditText editTextLabResults;
    TextView buttonSave;

    DatabaseReference databaseReference;
    String userId;
    HealthcareInfo currentHealthRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_record_editor);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Bind EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextAllergies = findViewById(R.id.editTextAllergies);
        editTextMedications = findViewById(R.id.editTextMedications);
        editTextImmunizations = findViewById(R.id.editTextImmunizations);
        editTextLabResults = findViewById(R.id.editTextLabResults);

        // Bind Button
        buttonSave = findViewById(R.id.buttonSave);

        // Example of using button click listener
        buttonSave.setOnClickListener(view -> {
            // Perform save operation
            saveDataToDatabase();
            Intent intentobject = new Intent(HealthRecordEditor.this, HealthRecordScreen.class);
            startActivity(intentobject);
        });

        // Load existing healthcare information if available
        loadHealthRecordFromDatabase();
    }

    private void loadHealthRecordFromDatabase() {
        databaseReference.child("health_records").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentHealthRecord = dataSnapshot.getValue(HealthcareInfo.class);
                    if (currentHealthRecord != null) {
                        editTextName.setText(currentHealthRecord.getName());
                        editTextDOB.setText(currentHealthRecord.getDateOfBirth());
                        editTextAllergies.setText(currentHealthRecord.getAllergies());
                        editTextMedications.setText(currentHealthRecord.getCurrentMedication());
                        editTextImmunizations.setText(currentHealthRecord.getImmunization());
                        editTextLabResults.setText(currentHealthRecord.getLabResults());
                    }
                } else {
                    currentHealthRecord = null;
                    // If no data exists, you may choose to clear EditText fields or leave them as is
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HealthRecordEditor.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataToDatabase() {
        String name = editTextName.getText().toString();
        String dob = editTextDOB.getText().toString();
        String allergies = editTextAllergies.getText().toString();
        String medications = editTextMedications.getText().toString();
        String immunizations = editTextImmunizations.getText().toString();
        String labResults = editTextLabResults.getText().toString();

        if (currentHealthRecord == null) {
            currentHealthRecord = new HealthcareInfo(name, dob, allergies, medications, immunizations, labResults);
        } else {
            currentHealthRecord.setName(name);
            currentHealthRecord.setDateOfBirth(dob);
            currentHealthRecord.setAllergies(allergies);
            currentHealthRecord.setCurrentMedication(medications);
            currentHealthRecord.setImmunization(immunizations);
            currentHealthRecord.setLabResults(labResults);
        }

        databaseReference.child("health_records").child(userId).setValue(currentHealthRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HealthRecordEditor.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HealthRecordEditor.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
