package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppointmentScreen extends Activity {


    private TextView book;
    private ListView listView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseReference;
    private ValueEventListener appointmentsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);

        book = findViewById(R.id.bookAppointment);
        listView = findViewById(R.id.appointmentsListView);

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        listView.setAdapter(appointmentAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Set onClickListener for the book button
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppointmentScreen.this, AddAppointmentActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAppointmentsFromDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove the ValueEventListener to prevent memory leaks
        if (appointmentsListener != null) {
            databaseReference.removeEventListener(appointmentsListener);
        }
    }

    private void loadAppointmentsFromDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Query appointmentsQuery = databaseReference.child("appointments").orderByChild("userId").equalTo(userId);

        appointmentsListener = appointmentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppointmentScreen.this, "Failed to load appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AppointmentScreen", "Failed to load appointments: " + databaseError.getMessage());
            }
        });
    }
}