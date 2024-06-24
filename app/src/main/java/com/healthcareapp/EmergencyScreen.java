package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmergencyScreen extends Activity {
    TextView addContact;
    private List<Contact> contactList;
    private ContactAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts);

        addContact = findViewById(R.id.addContact);
        contactList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Find the ListView
        ListView listView = findViewById(R.id.emergencycontactList);

        // Create the adapter to convert the array to views
        adapter = new ContactAdapter(this, R.layout.contact_card, contactList);

        // Attach the adapter to the ListView
        listView.setAdapter(adapter);

        // Load emergency contacts from Firebase
        loadEmergencyContacts();

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(EmergencyScreen.this, AddEmergencyContact.class);
                startActivity(intentobject);
            }
        });
    }

    private void loadEmergencyContacts() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("users").child(userId).child("emergency_contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear(); // Clear the previous list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EmergencyContact contact = snapshot.getValue(EmergencyContact.class);
                    if (contact != null) {
                        // Set the contact ID
                        String contactId = snapshot.getKey();
                        contactList.add(new Contact(contactId, contact.getName(), contact.getPhoneNumber()));
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EmergencyScreen.this, "Failed to load contacts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
