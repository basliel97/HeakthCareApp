package com.healthcareapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllAppointmentsActivity extends Activity {

    private ListView listView;
    private AllAppointmentsAdapter appointmentAdapter;
    private List<AllAppointments> appointmentList;
    private DatabaseReference databaseReference;
    private ValueEventListener appointmentsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_appointments);

        listView = findViewById(R.id.appointmentsListView);
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AllAppointmentsAdapter(this, appointmentList);
        listView.setAdapter(appointmentAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("appointments");

        loadAppointmentsFromDatabase();
    }

    private void loadAppointmentsFromDatabase() {
        appointmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AllAppointments appointment = snapshot.getValue(AllAppointments.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllAppointmentsActivity.this, "Failed to load appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AllAppointmentsActivity", "Database error: " + databaseError.getMessage());
            }
        };

        databaseReference.addValueEventListener(appointmentsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && appointmentsListener != null) {
            databaseReference.removeEventListener(appointmentsListener);
        }
    }
}
