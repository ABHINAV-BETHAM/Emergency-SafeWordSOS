package com.example.safewordsos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safewordsos.service.SOSForegroundService;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etSafeWord = findViewById(R.id.etSafeWord);
        EditText etPhone = findViewById(R.id.etPhone);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnEvidence = findViewById(R.id.btnEvidence);

        SharedPreferences prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE);
        etSafeWord.setText(prefs.getString("safeword", ""));
        etPhone.setText(prefs.getString("phone", ""));

        requestPermissions();

        btnStart.setOnClickListener(v -> {
            String word = etSafeWord.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (word.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Enter phone and safe word", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit().putString("safeword", word).putString("phone", phone).apply();

            Intent intent = new Intent(this, SOSForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(this, "Listening Started!", Toast.LENGTH_SHORT).show();
        });

        btnStop.setOnClickListener(v -> {
            // PROPERLY KILL THE BACKGROUND SERVICE
            Intent intent = new Intent(this, SOSForegroundService.class);
            intent.setAction("STOP_SERVICE");
            startService(intent);
            Toast.makeText(this, "Listening Completely Stopped.", Toast.LENGTH_SHORT).show();
        });

        btnEvidence.setOnClickListener(v -> {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            Toast.makeText(this, "Audio saved in: " + dir.getAbsolutePath(), Toast.LENGTH_LONG).show();
        });
    }

    private void requestPermissions() {
        String[] perms = { Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS };
        ActivityCompat.requestPermissions(this, perms, 100);
    }
}