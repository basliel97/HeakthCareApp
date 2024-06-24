package com.healthcareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWellnessChallengeActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDuration, editTextDescription;
    private Button buttonAddChallenge;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_wellness_challenge);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("wellness_challenges");

        // Initialize EditText fields
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextDescription = findViewById(R.id.editTextDescription);

        // Initialize Add Challenge Button
        buttonAddChallenge = findViewById(R.id.buttonAddChallenge);
        buttonAddChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWellnessChallenge();
            }
        });
    }

    private void addWellnessChallenge() {
        // Get input values
        String title = editTextTitle.getText().toString().trim();
        String duration = editTextDuration.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validate inputs (Title, Duration, and Description are required)
        if (title.isEmpty() || duration.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for the challenge
        String challengeId = databaseReference.push().getKey();

        // Create challenge object
        WellnessChallenge wellnessChallenge = new WellnessChallenge(challengeId, title, duration, description);

        // Save challenge to Firebase
        if (challengeId != null) {
            databaseReference.child(challengeId).setValue(wellnessChallenge)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddWellnessChallengeActivity.this, "Wellness challenge added successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(AddWellnessChallengeActivity.this, "Failed to add wellness challenge: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        editTextTitle.setText("");
        editTextDuration.setText("");
        editTextDescription.setText("");
    }
}
