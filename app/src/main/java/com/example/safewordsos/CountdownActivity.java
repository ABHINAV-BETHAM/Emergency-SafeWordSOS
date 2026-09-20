package com.example.safewordsos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build; // <-- THIS IS THE MISSING LINE THAT FIXED THE ERROR
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.example.safewordsos.service.SOSForegroundService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CountdownActivity extends AppCompatActivity {

    private TextView tvCountdown;
    private Button btnCancel;
    private CountDownTimer timer;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_countdown);

        tvCountdown = findViewById(R.id.tvCountdown);
        btnCancel = findViewById(R.id.btnCancel);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(1000);

        startCountdown();

        btnCancel.setOnClickListener(view -> {
            if (isRecording) {
                stopAudioRecording();
                resumeBackgroundListeningAndClose();
            } else {
                timer.cancel();
                Toast.makeText(this, "SOS Cancelled", Toast.LENGTH_SHORT).show();
                resumeBackgroundListeningAndClose();
            }
        });
    }

    private void startCountdown() {
        timer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("Sending SOS in " + (millisUntilFinished / 1000) + " seconds...");
            }
            public void onFinish() {
                executeEmergencyProtocol();
            }
        }.start();
    }

    private void executeEmergencyProtocol() {
        tvCountdown.setText("SOS ACTIVE\nRECORDING AUDIO...");
        tvCountdown.setTextColor(0xFFFF0000);
        btnCancel.setText("I AM SAFE (STOP RECORDING)");
        btnCancel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));

        sendSMSAndLocation();
        startContinuousAudioRecording();

        // Simulating 2km radius community alert
        Toast.makeText(this, "Alerting users within 2km radius...", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("MissingPermission")
    private void sendSMSAndLocation() {
        SharedPreferences prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE);
        String phone = prefs.getString("phone", "");

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        CancellationTokenSource cts = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    String mapsLink = location != null ?
                            "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude()
                            : "Location unavailable.";

                    String message = "SOS! I need help. Live Location: " + mapsLink;

                    if (!phone.isEmpty()) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phone, null, message, null, null);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
    }

    private void startContinuousAudioRecording() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir != null && !dir.exists()) dir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filePath = dir.getAbsolutePath() + "/SOS_Audio_" + timeStamp + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                Toast.makeText(this, "Audio Saved to Music Folder", Toast.LENGTH_LONG).show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void resumeBackgroundListeningAndClose() {
        // Tells the background service to start listening for the safe word again
        Intent resumeIntent = new Intent(this, SOSForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(resumeIntent);
        } else {
            startService(resumeIntent);
        }
        finish();
    }
}