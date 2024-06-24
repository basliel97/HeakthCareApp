package com.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WellnessScreen extends Activity {
    private List<WellnessChallenge> challengeList;
    private WellnessChallengeAdapter adapter;
    private DatabaseReference databaseReference;
    TextView startedChallenges;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wellness_challenge);

        challengeList = new ArrayList<>();
        adapter = new WellnessChallengeAdapter(this, challengeList);
startedChallenges = findViewById(R.id.started_challenge);

        startedChallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic for setting a new photo
                Intent intentobject = new Intent(WellnessScreen.this, StartedChallengesActivity.class);
                startActivity(intentobject);
            }
        });
        // Find the ListView
        ListView listView = findViewById(R.id.challenge_list);

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("wellness_challenges");

        // Load wellness challenges from Firebase Database
        loadWellnessChallenges();
    }

    private void loadWellnessChallenges() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                challengeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WellnessChallenge challenge = snapshot.getValue(WellnessChallenge.class);
                    if (challenge != null) {
                        challengeList.add(challenge);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WellnessScreen.this, "Failed to load challenges: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
