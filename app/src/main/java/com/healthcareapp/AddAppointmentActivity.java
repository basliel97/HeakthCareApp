package com.healthcareapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddAppointmentActivity extends Activity {

    private static final String CHANNEL_ID = "appointment_notifications";

    private EditText editTextDOA, editTextTime, editTextNotes;
    private TextView buttonBook;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_add_screen);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Bind EditTexts and Button
        editTextDOA = findViewById(R.id.editTextDOA);
        editTextTime = findViewById(R.id.editTextTime);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonBook = findViewById(R.id.buttonBook);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Set onClick listener for the book button
        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSaveAppointment();
                finish();
            }
        });
    }

    private void validateAndSaveAppointment() {
        // Retrieve values from EditText fields
        String date = editTextDOA.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        // Check if any of the fields are empty
        if (TextUtils.isEmpty(date)) {
            editTextDOA.setError("Date is required");
            editTextDOA.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            editTextTime.setError("Time is required");
            editTextTime.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(notes)) {
            editTextNotes.setError("Notes are required");
            editTextNotes.requestFocus();
            return;
        }

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Generate unique ID for the appointment
        String appointmentId = databaseReference.child("appointments").push().getKey();
        if (appointmentId == null) {
            Toast.makeText(this, "Failed to generate appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an Appointment object with the ID
        Appointment appointment = new Appointment(appointmentId, userId, date, time, notes);

        // Save the Appointment object to Firebase Realtime Database
        databaseReference.child("appointments").child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully saved
                    Toast.makeText(AddAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can finish the activity or clear the input fields here
                    clearFields();
                    // Send notification
                    sendNotification(userId, appointmentId, "Appointment Booked", "You have successfully booked an appointment for " + date + " at " + time + ".");
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                    Toast.makeText(AddAppointmentActivity.this, "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        editTextDOA.setText("");
        editTextTime.setText("");
        editTextNotes.setText("");
    }

    private void sendNotification(String userId, String appointmentId, String title, String message) {
        // Send a local notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.weellness_challenge)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to post notifications if not granted
            // This block is here as an example, you should handle permission request appropriately
            return;
        }
        notificationManager.notify(0, builder.build());

        // Save notification data to Firebase
        String notificationId = databaseReference.child("notifications").push().getKey();
        if (notificationId != null) {
            // Format timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date());

            Notification notification = new Notification(notificationId, userId, title, message, formattedDate);
            databaseReference.child("notifications").child(notificationId).setValue(notification);
        }
    }
}
