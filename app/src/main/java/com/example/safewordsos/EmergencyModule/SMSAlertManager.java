package com.example.safewordsos.EmergencyModule;

import android.telephony.SmsManager;
import android.util.Log;

public class SMSAlertManager {
    public void sendEmergencySMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SMSAlertManager", "Emergency SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e("SMSAlertManager", "Failed to send SMS: " + e.getMessage());
        }
    }
}