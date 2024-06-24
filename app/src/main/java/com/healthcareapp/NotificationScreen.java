package com.healthcareapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationScreen extends Activity {
    private ListView listViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private DatabaseReference databaseReference;
    private TextView clearAllTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notivication_screen);

        listViewNotifications = findViewById(R.id.notificatioListView);
        clearAllTextView = findViewById(R.id.clear_all);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        listViewNotifications.setAdapter(notificationAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Fetch notifications from Firebase
        fetchNotifications();

        // Set click listener for Clear All TextView
        clearAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllNotifications();
            }
        });
    }

    private void fetchNotifications() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Notification notification = postSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(NotificationScreen.this, "Failed to fetch notifications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllNotifications() {
        // Clear the list view
        notificationList.clear();
        notificationAdapter.notifyDataSetChanged();

        // Clear notifications from Firebase
        databaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // Error handling
                    Toast.makeText(NotificationScreen.this, "Failed to clear notifications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationScreen.this, "Notifications cleared successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
