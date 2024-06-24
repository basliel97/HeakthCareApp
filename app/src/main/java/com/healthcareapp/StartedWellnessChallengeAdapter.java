package com.healthcareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StartedWellnessChallengeAdapter extends BaseAdapter {

    private Context context;
    private List<WellnessChallenge> challenges;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    public StartedWellnessChallengeAdapter(Context context, List<WellnessChallenge> challenges) {
        this.context = context;
        this.challenges = challenges;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("wellness_challenges");
    }

    @Override
    public int getCount() {
        return challenges.size();
    }

    @Override
    public Object getItem(int position) {
        return challenges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.started_wellnes_card, parent, false);
            holder = new ViewHolder();
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewDuration = convertView.findViewById(R.id.textViewDuration);
            holder.textViewDescription = convertView.findViewById(R.id.textViewDescription);
            holder.buttonLeave = convertView.findViewById(R.id.buttonLeave);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WellnessChallenge challenge = challenges.get(position);
        holder.textViewTitle.setText(challenge.getTitle());
        holder.textViewDuration.setText("Duration: " + challenge.getDuration());
        holder.textViewDescription.setText(challenge.getDescription());

        // Handle button click to leave challenge
        holder.buttonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    // User is not authenticated, handle accordingly (e.g., redirect to login)
                    Toast.makeText(context, "Please login to leave the challenge", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get challenge ID
                String challengeId = challenge.getChallengeId();

                // Remove current user from participants
                DatabaseReference challengeRef = databaseReference.child(challengeId).child("participants").child(currentUser.getUid());
                challengeRef.removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Left the challenge successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to leave the challenge", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView textViewTitle;
        TextView textViewDuration;
        TextView textViewDescription;
        Button buttonLeave;
    }
}
