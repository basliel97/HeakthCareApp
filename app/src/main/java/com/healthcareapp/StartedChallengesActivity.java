package com.healthcareapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class StartedChallengesActivity extends AppCompatActivity {

    private ListView listViewStartedChallenges;
    private List<String> joinedChallengeIds; // List to store IDs of challenges joined by the current user
    private List<WellnessChallenge> startedChallengesList;
    private StartedWellnessChallengeAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.started_challenges);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize ListView
        listViewStartedChallenges = findViewById(R.id.startedChallenge_list);

        // Initialize lists
        joinedChallengeIds = new ArrayList<>();
        startedChallengesList = new ArrayList<>();

        // Initialize adapter
        adapter = new StartedWellnessChallengeAdapter(this, startedChallengesList);

        // Set adapter to ListView
        listViewStartedChallenges.setAdapter(adapter);

        // Retrieve IDs of challenges joined by the current user
        retrieveJoinedChallengeIds();
    }

    private void retrieveJoinedChallengeIds() {
        // Check if user is authenticated
        if (currentUser == null) {
            return; // Return if user is not authenticated
        }

        // Query to retrieve IDs of challenges joined by the current user
        DatabaseReference userChallengesRef = databaseReference.child("wellness_challenges");

        userChallengesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                joinedChallengeIds.clear();

                for (DataSnapshot challengeSnapshot : dataSnapshot.getChildren()) {
                    String challengeId = challengeSnapshot.getKey();
                    if (challengeId != null) {
                        // Check if the current user is a participant in this challenge
                        if (challengeSnapshot.child("participants").hasChild(currentUser.getUid())) {
                            joinedChallengeIds.add(challengeId);
                        }
                    }
                }

                // Retrieve details of joined challenges
                retrieveStartedChallenges();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void retrieveStartedChallenges() {
        startedChallengesList.clear();

        // Retrieve details of challenges joined by the current user
        for (String challengeId : joinedChallengeIds) {
            DatabaseReference challengeRef = databaseReference.child("wellness_challenges").child(challengeId);

            challengeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    WellnessChallenge challenge = dataSnapshot.getValue(WellnessChallenge.class);
                    if (challenge != null) {
                        startedChallengesList.add(challenge);
                        adapter.notifyDataSetChanged(); // Notify adapter of data change
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }
}
