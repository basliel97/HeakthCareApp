package com.healthcareapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingScreen extends Activity {
    TextView profile;
    TextView wellness;
    TextView health;
    TextView emergency;
    TextView notification;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);

        profile = findViewById(R.id.profile);
        wellness = findViewById(R.id.wellness_challenge);
        health = findViewById(R.id.health_recodrs);
        emergency = findViewById(R.id.emergency_contact);
        logout = findViewById(R.id.logout);
        notification = findViewById(R.id.notification);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentObject = new Intent(SettingScreen.this, ProfileActivity.class);
                startActivity(intentObject);
            }
        });

        wellness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(SettingScreen.this, WellnessScreen.class);
                startActivity(intentobject);
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(SettingScreen.this, HealthRecordScreen.class);
                startActivity(intentobject);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(SettingScreen.this, EmergencyScreen.class);
                startActivity(intentobject);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobject = new Intent(SettingScreen.this, NotificationScreen.class);
                startActivity(intentobject);
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logoutUser();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss dialog, do nothing
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logoutUser() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirect to LoginScreen
        Intent intent = new Intent(SettingScreen.this, LoginScreen.class);
        startActivity(intent);
        finish(); // Close current activity
    }
}
