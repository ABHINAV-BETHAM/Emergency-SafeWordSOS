package com.example.safewordsos.EmergencyModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;

public class EmergencyEngine {

    private final Context context;
    private final SMSAlertManager smsAlertManager;
    private final LocationTracker locationTracker;
    private final EvidenceRecorder evidenceRecorder;
    private boolean isSosActive = false;

    public EmergencyEngine(Context context) {
        this.context = context;
        this.smsAlertManager = new SMSAlertManager();
        this.locationTracker = new LocationTracker(context);
        this.evidenceRecorder = new EvidenceRecorder(context);
    }

    public void triggerEmergency(String source) {
        if (isSosActive) return; // Prevent spamming SMS if they say the word twice
        isSosActive = true;

        Log.d("EmergencyEngine", "SOS ACTIVATED via: " + source);

        SharedPreferences prefs = context.getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE);
        String emergencyContact = prefs.getString("phone", "");

        // 1. Silent Vibrate
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(1000);

        // 2. Start Audio Recording Immediately
        evidenceRecorder.startRecording();

        // 3. Get Real GPS Location & Send SMS
        locationTracker.getEmergencyLocation(locationLink -> {
            String message = "SOS! I need help. Triggered by: " + source + ". Live Location: " + locationLink;
            if (!emergencyContact.isEmpty()) {
                smsAlertManager.sendEmergencySMS(emergencyContact, message);
            }

            // Simulate 2km Radius push notification to other users
            Log.d("EmergencyEngine", "Simulating Community Alert to nearby users within 2km.");
        });
    }

    public void stopEmergency() {
        // User pressed "Stop Listening", save the audio!
        isSosActive = false;
        evidenceRecorder.stopRecording();
    }
}