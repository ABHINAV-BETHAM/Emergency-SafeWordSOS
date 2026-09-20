package com.example.safewordsos.EmergencyModule;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationTracker {
    private final FusedLocationProviderClient fusedLocationClient;

    public interface LocationCallback {
        void onLocationFound(String locationLink);
    }

    public LocationTracker(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getEmergencyLocation(LocationCallback callback) {
        // Gets REAL-TIME location, not just cached
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String mapsLink = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        callback.onLocationFound(mapsLink);
                    } else {
                        callback.onLocationFound("Location unavailable. Trying to reconnect GPS...");
                    }
                }).addOnFailureListener(e -> callback.onLocationFound("GPS Signal Lost."));
    }
}