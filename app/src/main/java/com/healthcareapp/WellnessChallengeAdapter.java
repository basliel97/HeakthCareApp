package com.healthcareapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WellnessChallengeAdapter extends BaseAdapter {

    private Context context;
    private List<WellnessChallenge> challenges;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    public WellnessChallengeAdapter(Context context, List<WellnessChallenge> challenges) {
        this.context = context;
        this.challenges = challenges;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("wellness_challenges");

        createNotificationChannel();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.wellness_card, parent, false);
            holder = new ViewHolder();
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewDuration = convertView.findViewById(R.id.textViewDuration);
            holder.textViewDescription = convertView.findViewById(R.id.textViewDescription);
            holder.buttonJoin = convertView.findViewById(R.id.buttonJoin);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WellnessChallenge challenge = challenges.get(position);
        holder.textViewTitle.setText(challenge.getTitle());
        holder.textViewDuration.setText("Duration: " + challenge.getDuration());
        holder.textViewDescription.setText(challenge.getDescription());

        // Handle button click to join challenge
        holder.buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    // User is not authenticated, handle accordingly (e.g., redirect to login)
                    Toast.makeText(context, "Please login to join the challenge", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get challenge ID
                String challengeId = challenge.getChallengeId();

                // Add current user to participants
                DatabaseReference challengeRef = databaseReference.child(challengeId).child("participants").child(currentUser.getUid());
                challengeRef.setValue(true)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Joined the challenge successfully", Toast.LENGTH_SHORT).show();
                                // Send notification to the user
                                sendNotification("You have joined the challenge: " + challenge.getTitle());
                                // Optionally, save the notification to Firebase Realtime Database
                                saveNotificationToDatabase(currentUser.getUid(), "Joined Challenge", "You have joined the challenge: " + challenge.getTitle());
                            } else {
                                Toast.makeText(context, "Failed to join the challenge", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return convertView;
    }

    private void sendNotification(String message) {
        // Send a local notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.weellness_challenge)
                .setContentTitle("Healthcare App")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void saveNotificationToDatabase(String userId, String title, String message) {
        // Save notification data to Firebase
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference().child("notifications");
        String notificationId = notificationsRef.push().getKey();
        if (notificationId != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date());
            Notification notification = new Notification(notificationId, userId, title, message, formattedDate);
            notificationsRef.child(notificationId).setValue(notification);
        }
    }

    static class ViewHolder {
        TextView textViewTitle;
        TextView textViewDuration;
        TextView textViewDescription;
        Button buttonJoin;
    }
}
