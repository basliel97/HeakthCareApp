package com.healthcareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Redirect to HomeScreen if user is already logged in
            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
            startActivity(intent);
            finish();
        } else {
            // Redirect to LoginScreen if no user is logged in
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(intent);
            finish();
        }
    }
}
