package com.example.safewordsos.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.safewordsos.CountdownActivity;
import com.example.safewordsos.TriggerModule.GestureDetector;
import com.example.safewordsos.TriggerModule.VoiceTriggerService;

public class SOSForegroundService extends Service implements VoiceTriggerService.TriggerCallback {

    private VoiceTriggerService voiceTriggerService;
    private GestureDetector gestureDetector;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If the Main Activity tells us to stop completely
        if (intent != null && "STOP_SERVICE".equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "SOS_CHANNEL")
                .setContentTitle("SafeWord SOS Shield Active")
                .setContentText("Listening for safe word...")
                .setSmallIcon(android.R.drawable.ic_secure) // Replace with your app icon if you have one
                .build();
        startForeground(1, notification);

        // Start listening
        startListeningEngine();

        return START_STICKY;
    }

    private void startListeningEngine() {
        if (voiceTriggerService != null) {
            voiceTriggerService.stop();
        }
        SharedPreferences prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE);
        String safeWord = prefs.getString("safeword", "help");

        voiceTriggerService = new VoiceTriggerService(this, safeWord, this);
        voiceTriggerService.startListening();

        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(this, () -> onTriggered("Shake"));
            gestureDetector.start();
        }
    }

    @Override
    public void onTriggered(String source) {
        // 1. RELEASE THE MICROPHONE INSTANTLY
        if (voiceTriggerService != null) {
            voiceTriggerService.stop();
            voiceTriggerService = null;
        }

        // 2. OPEN THE BLACK COUNTDOWN SCREEN
        Intent intent = new Intent(this, CountdownActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (voiceTriggerService != null) voiceTriggerService.stop();
        if (gestureDetector != null) gestureDetector.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SOS_CHANNEL", "SOS Monitor", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}