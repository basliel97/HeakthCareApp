package com.healthcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends Activity {

    EditText nameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText ageEditText;
    TextView createButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        ageEditText = findViewById(R.id.age);
        passwordEditText = findViewById(R.id.password);
        createButton = findViewById(R.id.createButton);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        createButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (name.isEmpty() || email.isEmpty() || age.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String userId = firebaseUser.getUid();

                                    // Initialize user with a default image URL
                                    String defaultImageUrl = ""; // or you can use a default image URL
                                    User user = new User(name, email, age, password, defaultImageUrl);
                                    databaseReference.child("users").child(userId).child("profile").setValue(user)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Intent intentobject = new Intent(SignUp.this, HomeScreen.class);
                                                    startActivity(intentobject);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUp.this, "Failed to save user data: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
