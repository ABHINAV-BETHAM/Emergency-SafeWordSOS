package com.example.safewordsos.EmergencyModule;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EvidenceRecorder {
    private MediaRecorder mediaRecorder;
    private final Context context;
    private boolean isRecording = false;

    public EvidenceRecorder(Context context) {
        this.context = context;
    }

    public void startRecording() {
        if (isRecording) return; // Don't start twice

        // Save safely to App's external Music directory
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

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
            Log.d("EvidenceRecorder", "AUDIO RECORDING STARTED. Saving to: " + filePath);
        } catch (IOException e) {
            Log.e("EvidenceRecorder", "Recording failed: " + e.getMessage());
        }
    }

    public void stopRecording() {
        if (isRecording && mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                Log.d("EvidenceRecorder", "AUDIO RECORDING STOPPED AND SAVED.");
            } catch (Exception e) {
                Log.e("EvidenceRecorder", "Error stopping recorder: " + e.getMessage());
            }
        }
    }
}