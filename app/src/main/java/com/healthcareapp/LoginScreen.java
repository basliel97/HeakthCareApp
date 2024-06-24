package com.healthcareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class LoginScreen extends Activity {

    EditText emailEditText;
    EditText passwordEditText;
    TextView loginButton;
    TextView signUp;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.login_screen);

        // Initialize Firebase Authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signupText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate inputs (optional)
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the provided email and password match the service provider's credentials
                if (email.equals("baslielgetu97@gmail.com") && password.equals("11879142b")) {
                    Intent intent = new Intent(LoginScreen.this, AddHealthTipActivity.class);
                    startActivity(intent);
                    return; // Skip the normal login process
                }

                // Normal login process
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Save login state
                                    saveLoginState();

                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginScreen.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    try {
                                        retriveImage();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    // Move to next activity or perform other actions
                                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginScreen.this, "Login failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void saveLoginState() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void retriveImage() throws IOException {
        StorageReference ref = FirebaseStorage.getInstance().getReference("UserProfile");
        File file = File.createTempFile("profile", "jpeg");
        ref.child(firebaseAuth.getUid()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                GetData.image = Uri.fromFile(file);
            }
        });
    }
}
