package com.example.safewordsos.TriggerModule;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GestureDetector implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final OnShakeListener listener;

    private long lastTime = 0;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 800;

    public interface OnShakeListener {
        void onShakeDetected();
    }

    public GestureDetector(Context context, OnShakeListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            accelerometer = null;
        }
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTime) > 100) {
            long diffTime = currentTime - lastTime;
            lastTime = currentTime;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                listener.onShakeDetected();
            }
            lastX = x; lastY = y; lastZ = z;
        }
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}