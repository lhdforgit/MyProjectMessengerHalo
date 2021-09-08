package com.hahalolo.call.webrtc;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.ThreadUtils;

import java.util.List;
import java.util.Set;

class AppRTCBluetoothManager
{
    private static final String TAG;
    private static final Logger LOGGER;
    private static final int BLUETOOTH_SCO_TIMEOUT_MS = 4000;
    private static final int MAX_SCO_CONNECTION_ATTEMPTS = 2;
    private final Context apprtcContext;
    private final com.hahalolo.call.webrtc.AppRTCAudioManager apprtcAudioManager;
    private final AudioManager audioManager;
    private final Handler handler;
    int scoConnectionAttempts;
    private State bluetoothState;
    private final BluetoothProfile.ServiceListener bluetoothServiceListener;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothHeadset bluetoothHeadset;
    private BluetoothDevice bluetoothDevice;
    private final BroadcastReceiver bluetoothHeadsetReceiver;
    private final Runnable bluetoothTimeoutRunnable;
    
    static AppRTCBluetoothManager create(final Context context, final com.hahalolo.call.webrtc.AppRTCAudioManager audioManager) {
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "create" + com.hahalolo.call.webrtc.AppRTCUtils.getThreadInfo());
        return new AppRTCBluetoothManager(context, audioManager);
    }
    
    private AppRTCBluetoothManager(final Context context, final com.hahalolo.call.webrtc.AppRTCAudioManager audioManager) {
        this.bluetoothTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                AppRTCBluetoothManager.this.bluetoothTimeout();
            }
        };
        ThreadUtils.checkIsOnMainThread();
        this.apprtcContext = context;
        this.apprtcAudioManager = audioManager;
        this.audioManager = this.getAudioManager(context);
        this.bluetoothState = State.UNINITIALIZED;
        this.bluetoothServiceListener = (BluetoothProfile.ServiceListener)new BluetoothServiceListener();
        this.bluetoothHeadsetReceiver = new BluetoothHeadsetBroadcastReceiver();
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    State getState() {
        ThreadUtils.checkIsOnMainThread();
        return this.bluetoothState;
    }
    
    @SuppressLint({ "MissingPermission" })
    void start() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "start");
        if (!this.hasPermission(this.apprtcContext)) {
            AppRTCBluetoothManager.LOGGER.w(AppRTCBluetoothManager.TAG, "Process (pid=" + Process.class + ") lacks BLUETOOTH permission");
            return;
        }
        if (this.bluetoothState != State.UNINITIALIZED) {
            AppRTCBluetoothManager.LOGGER.w(AppRTCBluetoothManager.TAG, "Invalid BT state");
            return;
        }
        this.bluetoothHeadset = null;
        this.bluetoothDevice = null;
        this.scoConnectionAttempts = 0;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            AppRTCBluetoothManager.LOGGER.w(AppRTCBluetoothManager.TAG, "Device does not support Bluetooth");
            return;
        }
        if (!this.audioManager.isBluetoothScoAvailableOffCall()) {
            AppRTCBluetoothManager.LOGGER.e(AppRTCBluetoothManager.TAG, "Bluetooth SCO audio is not available off call");
            return;
        }
        this.logBluetoothAdapterInfo(this.bluetoothAdapter);
        if (!this.getBluetoothProfileProxy(this.apprtcContext, this.bluetoothServiceListener, 1)) {
            AppRTCBluetoothManager.LOGGER.e(AppRTCBluetoothManager.TAG, "BluetoothAdapter.getProfileProxy(HEADSET) failed");
            return;
        }
        final IntentFilter bluetoothHeadsetFilter = new IntentFilter();
        bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
        this.registerReceiver(this.bluetoothHeadsetReceiver, bluetoothHeadsetFilter);
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "HEADSET profile state: " + this.stateToString(this.bluetoothAdapter.getProfileConnectionState(1)));
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "Bluetooth proxy for headset profile has started");
        this.bluetoothState = State.HEADSET_UNAVAILABLE;
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "start done: BT state=" + this.bluetoothState);
    }
    
    void stop() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "stop: BT state=" + this.bluetoothState);
        if (this.bluetoothAdapter == null) {
            return;
        }
        this.stopScoAudio();
        if (this.bluetoothState == State.UNINITIALIZED) {
            return;
        }
        this.unregisterReceiver(this.bluetoothHeadsetReceiver);
        this.cancelTimer();
        if (this.bluetoothHeadset != null) {
            this.bluetoothAdapter.closeProfileProxy(1, (BluetoothProfile)this.bluetoothHeadset);
            this.bluetoothHeadset = null;
        }
        this.bluetoothAdapter = null;
        this.bluetoothDevice = null;
        this.bluetoothState = State.UNINITIALIZED;
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "stop done: BT state=" + this.bluetoothState);
    }
    
    boolean startScoAudio() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "startSco: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + this.isScoOn());
        if (this.scoConnectionAttempts >= 2) {
            AppRTCBluetoothManager.LOGGER.e(AppRTCBluetoothManager.TAG, "BT SCO connection fails - no more attempts");
            return false;
        }
        if (this.bluetoothState != State.HEADSET_AVAILABLE) {
            AppRTCBluetoothManager.LOGGER.e(AppRTCBluetoothManager.TAG, "BT SCO connection fails - no headset available");
            return false;
        }
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "Starting Bluetooth SCO and waits for ACTION_AUDIO_STATE_CHANGED...");
        this.bluetoothState = State.SCO_CONNECTING;
        this.audioManager.startBluetoothSco();
        this.audioManager.setBluetoothScoOn(true);
        ++this.scoConnectionAttempts;
        this.startTimer();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "startScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + this.isScoOn());
        return true;
    }
    
    void stopScoAudio() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "stopScoAudio: BT state=" + this.bluetoothState + ", SCO is on: " + this.isScoOn());
        if (this.bluetoothState != State.SCO_CONNECTING && this.bluetoothState != State.SCO_CONNECTED) {
            return;
        }
        this.cancelTimer();
        this.audioManager.stopBluetoothSco();
        this.audioManager.setBluetoothScoOn(false);
        this.bluetoothState = State.SCO_DISCONNECTING;
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "stopScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + this.isScoOn());
    }
    
    @SuppressLint({ "MissingPermission" })
    void updateDevice() {
        if (this.bluetoothState == State.UNINITIALIZED || this.bluetoothHeadset == null) {
            return;
        }
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "updateDevice");
        final List<BluetoothDevice> devices = (List<BluetoothDevice>)this.bluetoothHeadset.getConnectedDevices();
        if (devices.isEmpty()) {
            this.bluetoothDevice = null;
            this.bluetoothState = State.HEADSET_UNAVAILABLE;
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "No connected bluetooth headset");
        }
        else {
            this.bluetoothDevice = devices.get(0);
            this.bluetoothState = State.HEADSET_AVAILABLE;
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "Connected bluetooth headset: name=" + this.bluetoothDevice.getName() + ", state=" + this.stateToString(this.bluetoothHeadset.getConnectionState(this.bluetoothDevice)) + ", SCO audio=" + this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice));
        }
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "updateDevice done: BT state=" + this.bluetoothState);
    }
    
    @SuppressLint("WrongConstant")
    private AudioManager getAudioManager(final Context context) {
        return (AudioManager)context.getSystemService("audio");
    }
    
    private void registerReceiver(final BroadcastReceiver receiver, final IntentFilter filter) {
        this.apprtcContext.registerReceiver(receiver, filter);
    }
    
    private void unregisterReceiver(final BroadcastReceiver receiver) {
        this.apprtcContext.unregisterReceiver(receiver);
    }
    
    private boolean getBluetoothProfileProxy(final Context context, final BluetoothProfile.ServiceListener listener, final int profile) {
        return this.bluetoothAdapter.getProfileProxy(context, listener, profile);
    }
    
    @SuppressLint("WrongConstant")
    private boolean hasPermission(final Context context) {
        return true/*this.apprtcContext.checkPermission("android.permission.BLUETOOTH", Process.myPid(), Process.myUid()) == 0*/;
    }
    
    @SuppressLint({ "MissingPermission" })
    private void logBluetoothAdapterInfo(final BluetoothAdapter localAdapter) {
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "BluetoothAdapter: enabled=" + localAdapter.isEnabled() + ", state=" + this.stateToString(localAdapter.getState()) + ", name=" + localAdapter.getName() + ", address=" + localAdapter.getAddress());
        final Set<BluetoothDevice> pairedDevices = (Set<BluetoothDevice>)localAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "paired devices:");
            for (final BluetoothDevice device : pairedDevices) {
                AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, " name=" + device.getName() + ", address=" + device.getAddress());
            }
        }
    }
    
    private void updateAudioDeviceState() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "updateAudioDeviceState");
        this.apprtcAudioManager.updateAudioDeviceState();
    }
    
    private void startTimer() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "startTimer");
        this.handler.postDelayed(this.bluetoothTimeoutRunnable, 4000L);
    }
    
    private void cancelTimer() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "cancelTimer");
        this.handler.removeCallbacks(this.bluetoothTimeoutRunnable);
    }
    
    @SuppressLint({ "MissingPermission" })
    private void bluetoothTimeout() {
        ThreadUtils.checkIsOnMainThread();
        if (this.bluetoothState == State.UNINITIALIZED || this.bluetoothHeadset == null) {
            return;
        }
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "bluetoothTimeout: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + this.isScoOn());
        if (this.bluetoothState != State.SCO_CONNECTING) {
            return;
        }
        boolean scoConnected = false;
        final List<BluetoothDevice> devices = (List<BluetoothDevice>)this.bluetoothHeadset.getConnectedDevices();
        if (devices.size() > 0) {
            this.bluetoothDevice = devices.get(0);
            if (this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice)) {
                AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "SCO connected with " + this.bluetoothDevice.getName());
                scoConnected = true;
            }
            else {
                AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "SCO is not connected with " + this.bluetoothDevice.getName());
            }
        }
        if (scoConnected) {
            this.bluetoothState = State.SCO_CONNECTED;
            this.scoConnectionAttempts = 0;
        }
        else {
            AppRTCBluetoothManager.LOGGER.w(AppRTCBluetoothManager.TAG, "BT failed to connect after timeout");
            this.stopScoAudio();
        }
        this.updateAudioDeviceState();
        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "bluetoothTimeout done: BT state=" + this.bluetoothState);
    }
    
    private boolean isScoOn() {
        return this.audioManager.isBluetoothScoOn();
    }
    
    private String stateToString(final int state) {
        switch (state) {
            case 0: {
                return "DISCONNECTED";
            }
            case 2: {
                return "CONNECTED";
            }
            case 1: {
                return "CONNECTING";
            }
            case 3: {
                return "DISCONNECTING";
            }
            case 10: {
                return "OFF";
            }
            case 12: {
                return "ON";
            }
            case 13: {
                return "TURNING_OFF";
            }
            case 11: {
                return "TURNING_ON";
            }
            default: {
                return "INVALID";
            }
        }
    }
    
    static {
        TAG = AppRTCBluetoothManager.class.getSimpleName();
        LOGGER = Logger.getInstance(com.hahalolo.call.webrtc.BaseClient.TAG);
    }
    
    public enum State
    {
        UNINITIALIZED, 
        ERROR, 
        HEADSET_UNAVAILABLE, 
        HEADSET_AVAILABLE, 
        SCO_DISCONNECTING, 
        SCO_CONNECTING, 
        SCO_CONNECTED;
    }
    
    private class BluetoothServiceListener implements BluetoothProfile.ServiceListener
    {
        public void onServiceConnected(final int profile, final BluetoothProfile proxy) {
            if (profile != 1 || AppRTCBluetoothManager.this.bluetoothState == State.UNINITIALIZED) {
                return;
            }
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceConnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            AppRTCBluetoothManager.this.bluetoothHeadset = (BluetoothHeadset)proxy;
            AppRTCBluetoothManager.this.updateAudioDeviceState();
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "onServiceConnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
        }
        
        public void onServiceDisconnected(final int profile) {
            if (profile != 1 || AppRTCBluetoothManager.this.bluetoothState == State.UNINITIALIZED) {
                return;
            }
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceDisconnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            AppRTCBluetoothManager.this.stopScoAudio();
            AppRTCBluetoothManager.this.bluetoothHeadset = null;
            AppRTCBluetoothManager.this.bluetoothDevice = null;
            AppRTCBluetoothManager.this.bluetoothState = State.HEADSET_UNAVAILABLE;
            AppRTCBluetoothManager.this.updateAudioDeviceState();
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "onServiceDisconnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
        }
    }
    
    private class BluetoothHeadsetBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (AppRTCBluetoothManager.this.bluetoothState == State.UNINITIALIZED) {
                return;
            }
            final String action = intent.getAction();
            if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                final int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(state) + ", sb=" + this.isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                if (state == 2) {
                    AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                    AppRTCBluetoothManager.this.updateAudioDeviceState();
                }
                else if (state != 1) {
                    if (state != 3) {
                        if (state == 0) {
                            AppRTCBluetoothManager.this.stopScoAudio();
                            AppRTCBluetoothManager.this.updateAudioDeviceState();
                        }
                    }
                }
            }
            else if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                final int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(state) + ", sb=" + this.isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                if (state == 12) {
                    AppRTCBluetoothManager.this.cancelTimer();
                    if (AppRTCBluetoothManager.this.bluetoothState == State.SCO_CONNECTING) {
                        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connected");
                        AppRTCBluetoothManager.this.bluetoothState = State.SCO_CONNECTED;
                        AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    }
                    else {
                        AppRTCBluetoothManager.LOGGER.w(AppRTCBluetoothManager.TAG, "Unexpected state BluetoothHeadset.STATE_AUDIO_CONNECTED");
                    }
                }
                else if (state == 11) {
                    AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connecting...");
                }
                else if (state == 10) {
                    AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now disconnected");
                    if (this.isInitialStickyBroadcast()) {
                        AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "Ignore STATE_AUDIO_DISCONNECTED initial sticky broadcast.");
                        return;
                    }
                    AppRTCBluetoothManager.this.updateAudioDeviceState();
                }
            }
            AppRTCBluetoothManager.LOGGER.d(AppRTCBluetoothManager.TAG, "onReceive done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
        }
    }
}
