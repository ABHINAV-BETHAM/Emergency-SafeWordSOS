package com.example.safewordsos.TriggerModule;

import android.content.Context;
import android.util.Log;

// Simulating hardware trigger for the prototype.
// In a real app, this requires an AccessibilityService.
public class VolumeButtonTrigger {
    public void simulateHardwarePress(VoiceTriggerService.TriggerCallback callback) {
        Log.d("VolumeTrigger", "Hardware Button Sequence Detected");
        callback.onTriggered("Hardware Trigger");
    }
}