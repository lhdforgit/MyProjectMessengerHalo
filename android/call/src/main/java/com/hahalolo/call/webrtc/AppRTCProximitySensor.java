package com.hahalolo.call.webrtc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.ThreadUtils;

class AppRTCProximitySensor implements SensorEventListener
{
    private static final String TAG;
    private static final Logger LOGGER;
    private final ThreadUtils.ThreadChecker threadChecker;
    private final Runnable onSensorStateListener;
    private final SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean lastStateReportIsNear;
    
    static AppRTCProximitySensor create(final Context context, final Runnable sensorStateListener) {
        return new AppRTCProximitySensor(context, sensorStateListener);
    }
    
    @SuppressLint("WrongConstant")
    private AppRTCProximitySensor(final Context context, final Runnable sensorStateListener) {
        this.threadChecker = new ThreadUtils.ThreadChecker();
        this.proximitySensor = null;
        this.lastStateReportIsNear = false;
        AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, AppRTCUtils.getThreadInfo());
        this.onSensorStateListener = sensorStateListener;
        this.sensorManager = (SensorManager)context.getSystemService("sensor");
    }
    
    boolean start() {
        this.threadChecker.checkIsOnValidThread();
        AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, "start" + AppRTCUtils.getThreadInfo());
        if (!this.initDefaultSensor()) {
            return false;
        }
        this.sensorManager.registerListener((SensorEventListener)this, this.proximitySensor, 3);
        return true;
    }
    
    void stop() {
        this.threadChecker.checkIsOnValidThread();
        AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, "stop" + AppRTCUtils.getThreadInfo());
        if (this.proximitySensor == null) {
            return;
        }
        this.sensorManager.unregisterListener((SensorEventListener)this, this.proximitySensor);
    }
    
    boolean sensorReportsNearState() {
        this.threadChecker.checkIsOnValidThread();
        return this.lastStateReportIsNear;
    }
    
    public final void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        this.threadChecker.checkIsOnValidThread();
        if (sensor.getType() != 8) {
            AppRTCProximitySensor.LOGGER.e(AppRTCProximitySensor.TAG, "Accuracy changed for unexpected sensor");
            return;
        }
        if (accuracy == 0) {
            AppRTCProximitySensor.LOGGER.e(AppRTCProximitySensor.TAG, "The values returned by this sensor cannot be trusted");
        }
    }
    
    public final void onSensorChanged(final SensorEvent event) {
        this.threadChecker.checkIsOnValidThread();
        if (event.sensor.getType() != 8) {
            AppRTCProximitySensor.LOGGER.e(AppRTCProximitySensor.TAG, "Sensor changed for unexpected sensor");
            return;
        }
        final float distanceInCentimeters = event.values[0];
        if (distanceInCentimeters < this.proximitySensor.getMaximumRange()) {
            AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, "Proximity sensor => NEAR state");
            this.lastStateReportIsNear = true;
        }
        else {
            AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, "Proximity sensor => FAR state");
            this.lastStateReportIsNear = false;
        }
        if (this.onSensorStateListener != null) {
            this.onSensorStateListener.run();
        }
        AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, "onSensorChanged" + AppRTCUtils.getThreadInfo() + ": accuracy=" + event.accuracy + ", timestamp=" + event.timestamp + ", distance=" + event.values[0]);
    }
    
    private boolean initDefaultSensor() {
        if (this.proximitySensor != null) {
            return true;
        }
        this.proximitySensor = this.sensorManager.getDefaultSensor(8);
        if (this.proximitySensor == null) {
            return false;
        }
        this.logProximitySensorInfo();
        return true;
    }
    
    private void logProximitySensorInfo() {
        if (this.proximitySensor == null) {
            return;
        }
        final StringBuilder info = new StringBuilder("Proximity sensor: ");
        info.append("name=").append(this.proximitySensor.getName());
        info.append(", vendor: ").append(this.proximitySensor.getVendor());
        info.append(", power: ").append(this.proximitySensor.getPower());
        info.append(", resolution: ").append(this.proximitySensor.getResolution());
        info.append(", max range: ").append(this.proximitySensor.getMaximumRange());
        info.append(", min delay: ").append(this.proximitySensor.getMinDelay());
        if (Build.VERSION.SDK_INT >= 20) {
            info.append(", type: ").append(this.proximitySensor.getStringType());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            info.append(", max delay: ").append(this.proximitySensor.getMaxDelay());
            info.append(", reporting mode: ").append(this.proximitySensor.getReportingMode());
            info.append(", isWakeUpSensor: ").append(this.proximitySensor.isWakeUpSensor());
        }
        AppRTCProximitySensor.LOGGER.d(AppRTCProximitySensor.TAG, info.toString());
    }
    
    static {
        TAG = AppRTCProximitySensor.class.getSimpleName();
        LOGGER = Logger.getInstance(com.hahalolo.call.webrtc.BaseClient.TAG);
    }
}
