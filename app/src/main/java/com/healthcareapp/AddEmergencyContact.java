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

public class AddEmergencyContact extends Activity {

    private EditText editTextName, editTextPhoneNumber, editTextRelationship;
    private TextView buttonSaveContact;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_add);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Bind views
        editTextName = findViewById(R.id.editName);
        editTextPhoneNumber = findViewById(R.id.editPhoneNumber);
        editTextRelationship = findViewById(R.id.editRelation);
        buttonSaveContact = findViewById(R.id.buttonSave);

        // Save contact on button click
        buttonSaveContact.setOnClickListener(view -> {
            saveEmergencyContact();
        });
    }

    private void saveEmergencyContact() {
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

        // Generate a unique ID for each contact
        String contactId = databaseReference.child("users").child(userId).child("emergency_contacts").push().getKey();

        // Create an EmergencyContact object
        EmergencyContact contact = new EmergencyContact(name, phoneNumber, relationship);

        // Save the contact under the user ID in the database
        databaseReference.child("users").child(userId).child("emergency_contacts").child(contactId).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddEmergencyContact.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity or navigate as needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddEmergencyContact.this, "Failed to save contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
