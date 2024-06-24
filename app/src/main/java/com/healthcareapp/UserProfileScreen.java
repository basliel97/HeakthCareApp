package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UserProfileScreen extends Activity {

    EditText nameEditText, emailEditText, ageEditText, passwordEditText;
    TextView setNewPhoto, saveTextView, cancelTextView, clearAllTextView;
    ImageView profilePicture;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        ageEditText = findViewById(R.id.age);
        passwordEditText = findViewById(R.id.password);
        profilePicture = findViewById(R.id.userImage);
        setNewPhoto = findViewById(R.id.setImage);
        saveTextView = findViewById(R.id.notification);
        cancelTextView = findViewById(R.id.mental_health);
        clearAllTextView = findViewById(R.id.about);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            databaseReference.child("users").child(userId).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        GetData.user = user;
                        if (user != null) {
                            nameEditText.setText(user.getName());
                            emailEditText.setText(user.getEmail());
                            ageEditText.setText(user.getAge());
                            passwordEditText.setText(user.getPassword());
                            if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                                // Load the image using your preferred image loading library, e.g., Glide
                                // Glide.with(UserProfileScreen.this).load(user.getImageUrl()).into(profilePicture);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }

        // Set click listeners for TextViews
        setNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic for setting a new photo
                selectAnImage();
            }
        });

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement save logic
                try {
                    saveUserProfile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finish();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement cancel logic
                finish(); // Close the activity
            }
        });

        clearAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement clear all fields logic
                clearAllFields();
            }
        });
    }

    private void selectAnImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 100 && resultCode == RESULT_OK) {
            uri = data.getData();
            profilePicture.setImageURI(uri);
        }
    }

    private void saveUserProfile() throws IOException {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newAge = ageEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId).child("profile");

            if (uri != null) {
                StorageReference ref = storageReference.child("UserProfile/" + GetData.user.getEmail());
                ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String imageUrl = downloadUri.toString();
                                userRef.child("imageUrl").setValue(imageUrl);
                                saveProfileData(userRef, newName, newEmail, newAge, newPassword);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfileScreen.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileScreen.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                saveProfileData(userRef, newName, newEmail, newAge, newPassword);
            }
        }
    }

    private void saveProfileData(DatabaseReference userRef, String name, String email, String age, String password) {
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("age").setValue(age);
        userRef.child("password").setValue(password)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfileScreen.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileScreen.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearAllFields() {
        nameEditText.setText("");
        emailEditText.setText("");
        ageEditText.setText("");
        passwordEditText.setText("");
    }
}
