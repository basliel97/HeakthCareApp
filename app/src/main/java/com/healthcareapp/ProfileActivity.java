package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends Activity {

    private ImageView userImage;
    private TextView nameTextView, emailTextView, ageTextView, passwordTextView;
    private TextView editButton, cancelButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Initialize views
        userImage = findViewById(R.id.userImage);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        ageTextView = findViewById(R.id.age);
        passwordTextView = findViewById(R.id.password);
        editButton = findViewById(R.id.editbtn);
        cancelButton = findViewById(R.id.cancelbtn);

        // Load user profile data from Firebase
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId).child("profile");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String age = dataSnapshot.child("age").getValue(String.class);
                        String password = dataSnapshot.child("password").getValue(String.class);
                        String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                        // Set data to TextViews
                        nameTextView.setText(name);
                        emailTextView.setText(email);

                        if (age != null) {
                            ageTextView.setText(age);
                        }
                        if (password != null) {
                            passwordTextView.setText(password);
                        }

                        // Load the image using Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.default_profile_image) // default image if none is available
                                    .into(userImage);
                        } else {
                            userImage.setImageResource(R.drawable.default_profile_image); // set a default image if URL is empty
                        }

                    } else {
                        Toast.makeText(ProfileActivity.this, "User profile data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ProfileActivity", "Error loading user profile: " + databaseError.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user is not authenticated
        }

        // Handle edit button click
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement edit profile logic here
                Intent intentobject = new Intent(ProfileActivity.this, UserProfileScreen.class);
                startActivity(intentobject);
            }
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement cancel/back logic here
                finish(); // Close the activity
            }
        });
    }
}
