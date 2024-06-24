package com.healthcareapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditEmergencyContactActivity extends Activity {

    private EditText editTextName, editTextPhoneNumber, editTextRelationship;
    private TextView buttonSaveContact;
    private DatabaseReference databaseReference;
    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_edit);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Bind views
        editTextName = findViewById(R.id.editTextName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextRelationship = findViewById(R.id.editTextRelationship);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);

        // Retrieve the contact ID and other details passed from the previous activity
        contactId = getIntent().getStringExtra("contactId");
        String name = getIntent().getStringExtra("name");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String relationship = getIntent().getStringExtra("relationship");

        // Populate EditTexts with existing data
        editTextName.setText(name);
        editTextPhoneNumber.setText(phoneNumber);
        editTextRelationship.setText(relationship);

        // Save contact on button click
        buttonSaveContact.setOnClickListener(view -> {
            updateEmergencyContact();
        });
    }

    private void updateEmergencyContact() {
        // Retrieve values from EditText fields
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String relationship = editTextRelationship.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(relationship)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID (assuming you have implemented user authentication)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create an EmergencyContact object
        EmergencyContact contact = new EmergencyContact(name, phoneNumber, relationship);

        // Update the contact under the user ID in the database
        databaseReference.child("users").child(userId).child("emergency_contacts").child(contactId).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEmergencyContactActivity.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity or navigate as needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditEmergencyContactActivity.this, "Failed to update contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

