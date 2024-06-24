package com.healthcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddHealthTipActivity extends AppCompatActivity {

    private EditText editTextContent, editTextDate;
    private DatabaseReference databaseReference;
    private TextView checkAppointments;
    private TextView addWellness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_provider);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("healthtips");

        // Bind EditTexts and Button
        editTextContent = findViewById(R.id.editTextContent);
        editTextDate = findViewById(R.id.editTextTipTime);
        checkAppointments = findViewById(R.id.checkAppointments);
        addWellness = findViewById(R.id.addChallenges);
        findViewById(R.id.buttonSaveTip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSaveHealthTip();
            }
        });
        findViewById(R.id.checkAppointments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AddHealthTipActivity.this, AllAppointmentsActivity.class));
                ;
            }
        });
        findViewById(R.id.addChallenges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AddHealthTipActivity.this, AddWellnessChallengeActivity.class));
                ;
            }
        });

    }

    private void validateAndSaveHealthTip() {
        // Retrieve values from EditText fields
        String content = editTextContent.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        // Check if any of the fields are empty
        if (TextUtils.isEmpty(content)) {
            editTextContent.setError("Content is required");
            editTextContent.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            editTextDate.setError("Date is required");
            editTextDate.requestFocus();
            return;
        }

        // Create a unique ID for each health tip
        String tipId = databaseReference.push().getKey();

        // Create a HealthTip object
        HealthTip healthTip = new HealthTip(content, date);

        // Save the HealthTip object to Firebase Realtime Database
        if (tipId != null) {
            databaseReference.child(tipId).setValue(healthTip)
                    .addOnSuccessListener(aVoid -> {
                        // Data successfully saved
                        Toast.makeText(AddHealthTipActivity.this, "Health Tip added successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, you can finish the activity or clear the input fields here
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to save data
                        Toast.makeText(AddHealthTipActivity.this, "Failed to add health tip: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearFields() {
        editTextContent.setText("");
        editTextDate.setText("");
    }
}
