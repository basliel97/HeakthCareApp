package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class HomeScreen extends Activity {
    ImageView home;
    ImageView appointment;
    ImageView setting;
    private ListView listView;

    private HealthTipsAdapter adapter;
    private List<HealthTip> healthTipList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        home = findViewById(R.id.home);
        appointment = findViewById(R.id.appointment);
        setting = findViewById(R.id.setting);

        listView = findViewById(R.id.healthTipListView);
        healthTipList = new ArrayList<>();
        adapter = new HealthTipsAdapter(this, healthTipList);
        listView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("healthtips");

        // Load health tips from the database
        loadHealthTipsFromDatabase();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(HomeScreen.this, SettingScreen.class);
                startActivity(intentobject);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Home button click logic
            }
        });

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(HomeScreen.this, AppointmentScreen.class);
                startActivity(intentobject);
            }
        });
    }

    private void loadHealthTipsFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                healthTipList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HealthTip healthTip = snapshot.getValue(HealthTip.class);
                    if (healthTip != null) {
                        healthTipList.add(healthTip);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeScreen.this, "Failed to load health tips: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
