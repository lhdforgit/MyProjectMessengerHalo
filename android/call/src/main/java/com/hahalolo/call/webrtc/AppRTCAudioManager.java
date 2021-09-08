package com.hahalolo.call.webrtc;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.ThreadUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AppRTCAudioManager
{
    private static final String TAG;
    private static final Logger LOGGER;
    private OnWiredHeadsetStateListener wiredHeadsetStateListener;
    private BluetoothAudioDeviceStateListener bluetoothAudioDeviceStateListener;
    private OnAudioManagerStateListener onAudioStateChangeListener;
    private boolean manageHeadsetByDefault;
    private boolean manageBluetoothByDefault;
    private boolean manageSpeakerPhoneByProximity;
    private final Context apprtcContext;
    private AudioManager audioManager;
    private AudioManagerEvents audioManagerEvents;
    private AudioManagerState amState;
    private int savedAudioMode;
    private boolean savedIsSpeakerPhoneOn;
    private boolean savedIsMicrophoneMute;
    private boolean hasWiredHeadset;
    private AudioDevice defaultAudioDevice;
    private AudioDevice selectedAudioDevice;
    private AudioDevice userSelectedAudioDevice;
    private com.hahalolo.call.webrtc.AppRTCProximitySensor proximitySensor;
    private final AppRTCBluetoothManager bluetoothManager;
    private Set<AudioDevice> audioDevices;
    private BroadcastReceiver wiredHeadsetReceiver;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    
    private void onProximitySensorChangedState() {
        if (!this.manageSpeakerPhoneByProximity) {
            return;
        }
        if (this.audioDevices.size() == 2 && this.audioDevices.contains(AudioDevice.EARPIECE) && this.audioDevices.contains(AudioDevice.SPEAKER_PHONE)) {
            if (this.proximitySensor.sensorReportsNearState()) {
                this.setAudioDeviceInternal(this.userSelectedAudioDevice = AudioDevice.EARPIECE);
            }
            else {
                this.setAudioDeviceInternal(this.userSelectedAudioDevice = AudioDevice.SPEAKER_PHONE);
            }
            if (this.audioManagerEvents != null) {
                this.audioManagerEvents.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }
    }
    
    public static AppRTCAudioManager create(final Context context) {
        return new AppRTCAudioManager(context);
    }
    
    @Deprecated
    public static AppRTCAudioManager create(final Context context, final OnAudioManagerStateListener onAudioManagerStateListener) {
        return new AppRTCAudioManager(context, onAudioManagerStateListener);
    }
    
    private AppRTCAudioManager(final Context context, final OnAudioManagerStateListener onAudioManagerStateListener) {
        this(context);
        this.onAudioStateChangeListener = onAudioManagerStateListener;
    }
    
    @SuppressLint("WrongConstant")
    private AppRTCAudioManager(final Context context) {
        this.manageHeadsetByDefault = true;
        this.manageBluetoothByDefault = true;
        this.manageSpeakerPhoneByProximity = false;
        this.savedAudioMode = -2;
        this.savedIsSpeakerPhoneOn = false;
        this.savedIsMicrophoneMute = false;
        this.hasWiredHeadset = false;
        this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
        this.proximitySensor = null;
        this.audioDevices = new HashSet<AudioDevice>();
        ThreadUtils.checkIsOnMainThread();
        this.apprtcContext = context.getApplicationContext();
        this.audioManager = (AudioManager)context.getSystemService("audio");
        this.bluetoothManager = AppRTCBluetoothManager.create(context, this);
        this.wiredHeadsetReceiver = new WiredHeadsetReceiver();
        this.amState = AudioManagerState.UNINITIALIZED;
        this.proximitySensor = com.hahalolo.call.webrtc.AppRTCProximitySensor.create(context, this::onProximitySensorChangedState);
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "defaultAudioDevice: " + this.defaultAudioDevice);
        AppRTCUtils.logDeviceInfo(AppRTCAudioManager.TAG);
    }
    
    @Deprecated
    public void init() {
        this.start(new AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(final AudioDevice selectedAudioDevice, final Set<AudioDevice> availableAudioDevices) {
                AppRTCAudioManager.this.onAudioManagerChangedState(selectedAudioDevice);
            }
        });
    }
    
    @SuppressLint("WrongConstant")
    public void start(final AudioManagerEvents audioManagerEvents) {
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "start");
        ThreadUtils.checkIsOnMainThread();
        if (this.amState == AudioManagerState.RUNNING) {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "AudioManager is already active");
            return;
        }
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "AudioManager starts...");
        this.audioManagerEvents = audioManagerEvents;
        this.amState = AudioManagerState.RUNNING;
        this.savedAudioMode = this.audioManager.getMode();
        this.savedIsSpeakerPhoneOn = this.audioManager.isSpeakerphoneOn();
        this.savedIsMicrophoneMute = this.audioManager.isMicrophoneMute();
        this.hasWiredHeadset = this.hasWiredHeadset();
        this.audioFocusChangeListener = (focusChange -> {
            String typeOfChange = null;
            switch (focusChange) {
                case 1: {
                    typeOfChange = "AUDIOFOCUS_GAIN";
                    break;
                }
                case 2: {
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT";
                    break;
                }
                case 4: {
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
                    break;
                }
                case 3: {
                    typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                    break;
                }
                case -1: {
                    typeOfChange = "AUDIOFOCUS_LOSS";
                    break;
                }
                case -2: {
                    typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                }
                case -3: {
                    typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    break;
                }
                default: {
                    typeOfChange = "AUDIOFOCUS_INVALID";
                    break;
                }
            }
            AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "onAudioFocusChange: " + typeOfChange);
        });
        final int result = this.audioManager.requestAudioFocus(this.audioFocusChangeListener, 0, 2);
        if (result == 1) {
            AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "Audio focus request granted for VOICE_CALL streams");
        }
        else {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Audio focus request failed");
        }
        this.audioManager.setMode(3);
        this.setMicrophoneMute(false);
        this.userSelectedAudioDevice = AudioDevice.NONE;
        this.selectedAudioDevice = AudioDevice.NONE;
        this.audioDevices.clear();
        this.bluetoothManager.start();
        this.proximitySensor.start();
        this.updateAudioDeviceState();
        this.registerReceiver(this.wiredHeadsetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "AudioManager started");
    }
    
    public AudioManager getAndroidAudioManager() {
        return this.audioManager;
    }
    
    @Deprecated
    public void setOnAudioManagerStateListener(final OnAudioManagerStateListener onAudioManagerStateListener) {
        this.onAudioStateChangeListener = onAudioManagerStateListener;
    }
    
    @Deprecated
    private void onAudioManagerChangedState(final AudioDevice audioDevice) {
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "onAudioManagerChangedState: devices=" + this.audioDevices + ", selected=" + this.selectedAudioDevice);
        if (this.onAudioStateChangeListener != null) {
            this.onAudioStateChangeListener.onAudioChangedState(audioDevice);
        }
    }
    
    public void setManageHeadsetByDefault(final boolean manageHeadsetByDefault) {
        this.manageHeadsetByDefault = manageHeadsetByDefault;
    }
    
    public void setOnWiredHeadsetStateListener(final OnWiredHeadsetStateListener wiredHeadsetStateListener) {
        this.wiredHeadsetStateListener = wiredHeadsetStateListener;
    }
    
    private void notifyWiredHeadsetListener(final boolean plugged, final boolean hasMicrophone) {
        if (this.wiredHeadsetStateListener != null) {
            this.wiredHeadsetStateListener.onWiredHeadsetStateChanged(plugged, hasMicrophone);
        }
    }
    
    public void setManageSpeakerPhoneByProximity(final boolean manageSpeakerPhoneByProximity) {
        this.manageSpeakerPhoneByProximity = manageSpeakerPhoneByProximity;
    }
    
    public void setManageBluetoothByDefault(final boolean manageBluetoothByDefault) {
        this.manageBluetoothByDefault = manageBluetoothByDefault;
    }
    
    public void setBluetoothAudioDeviceStateListener(final BluetoothAudioDeviceStateListener bluetoothAudioDeviceStateListener) {
        this.bluetoothAudioDeviceStateListener = bluetoothAudioDeviceStateListener;
    }
    
    private void notifyBluetoothAudioDeviceStateListener(final boolean connected) {
        if (this.bluetoothAudioDeviceStateListener != null) {
            this.bluetoothAudioDeviceStateListener.onStateChanged(connected);
        }
    }
    
    @Deprecated
    public void close() {
        this.stop();
    }
    
    public void stop() {
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "stop");
        ThreadUtils.checkIsOnMainThread();
        if (this.amState != AudioManagerState.RUNNING) {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Trying to stop AudioManager in incorrect state: " + this.amState);
            return;
        }
        this.amState = AudioManagerState.UNINITIALIZED;
        this.unregisterReceiver(this.wiredHeadsetReceiver);
        this.bluetoothManager.stop();
        this.setSpeakerphoneOn(this.savedIsSpeakerPhoneOn);
        this.setMicrophoneMute(this.savedIsMicrophoneMute);
        this.audioManager.setMode(this.savedAudioMode);
        this.audioManager.abandonAudioFocus(this.audioFocusChangeListener);
        this.audioFocusChangeListener = null;
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "Abandoned audio focus for VOICE_CALL streams");
        if (this.proximitySensor != null) {
            this.proximitySensor.stop();
            this.proximitySensor = null;
        }
        this.audioManagerEvents = null;
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "AudioManager stopped");
    }
    
    @Deprecated
    public void setAudioDevice(final AudioDevice device) {
        if (!this.audioDevices.contains(device)) {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Device doesn't nave " + device);
            return;
        }
        if (device == this.selectedAudioDevice) {
            return;
        }
        this.selectAudioDevice(device);
    }
    
    public AudioDevice getDefaultAudioDevice() {
        return this.defaultAudioDevice;
    }
    
    private void setAudioDeviceInternal(final AudioDevice device) {
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "setAudioDeviceInternal(device=" + device + ")");
        if (!this.audioDevices.contains(device)) {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Invalid audio device selection");
            return;
        }
        switch (device) {
            case SPEAKER_PHONE: {
                this.setSpeakerphoneOn(true);
                break;
            }
            case EARPIECE:
            case WIRED_HEADSET:
            case BLUETOOTH: {
                this.setSpeakerphoneOn(false);
                break;
            }
            default: {
                AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Invalid audio device selection");
                break;
            }
        }
        if (AudioDevice.EARPIECE == device && this.hasWiredHeadset) {
            this.selectedAudioDevice = AudioDevice.WIRED_HEADSET;
        }
        else {
            this.selectedAudioDevice = device;
        }
    }
    
    public void setDefaultAudioDevice(final AudioDevice defaultDevice) {
        ThreadUtils.checkIsOnMainThread();
        switch (defaultDevice) {
            case SPEAKER_PHONE: {
                this.defaultAudioDevice = defaultDevice;
                break;
            }
            case EARPIECE: {
                if (this.hasEarpiece()) {
                    this.defaultAudioDevice = defaultDevice;
                    break;
                }
                this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
                break;
            }
            default: {
                AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Invalid default audio device selection");
                break;
            }
        }
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "setDefaultAudioDevice(device=" + this.defaultAudioDevice + ")");
        this.updateAudioDeviceState();
    }
    
    public void selectAudioDevice(final AudioDevice device) {
        ThreadUtils.checkIsOnMainThread();
        if (!this.audioDevices.contains(device)) {
            AppRTCAudioManager.LOGGER.e(AppRTCAudioManager.TAG, "Can not select " + device + " from available " + this.audioDevices);
            return;
        }
        this.userSelectedAudioDevice = device;
        this.updateAudioDeviceState();
    }
    
    public Set<AudioDevice> getAudioDevices() {
        ThreadUtils.checkIsOnMainThread();
        return Collections.unmodifiableSet((Set<? extends AudioDevice>)new HashSet<AudioDevice>(this.audioDevices));
    }
    
    public AudioDevice getSelectedAudioDevice() {
        ThreadUtils.checkIsOnMainThread();
        return this.selectedAudioDevice;
    }
    
    private void registerReceiver(final BroadcastReceiver receiver, final IntentFilter filter) {
        this.apprtcContext.registerReceiver(receiver, filter);
    }
    
    private void unregisterReceiver(final BroadcastReceiver receiver) {
        this.apprtcContext.unregisterReceiver(receiver);
    }
    
    private void setSpeakerphoneOn(final boolean on) {
        final boolean wasOn = this.audioManager.isSpeakerphoneOn();
        if (wasOn == on) {
            return;
        }
        this.audioManager.setSpeakerphoneOn(on);
    }
    
    private void setMicrophoneMute(final boolean on) {
        final boolean wasMuted = this.audioManager.isMicrophoneMute();
        if (wasMuted == on) {
            return;
        }
        this.audioManager.setMicrophoneMute(on);
    }
    
    private boolean hasEarpiece() {
        return this.apprtcContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }
    
    @Deprecated
    private boolean hasWiredHeadset() {
        if (Build.VERSION.SDK_INT < 23) {
            return this.audioManager.isWiredHeadsetOn();
        }
        final AudioDeviceInfo[] devices2;
        @SuppressLint("WrongConstant")
        final AudioDeviceInfo[] devices = devices2 = this.audioManager.getDevices(3);
        for (final AudioDeviceInfo device : devices2) {
            final int type = device.getType();
            if (type == 3) {
                AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "hasWiredHeadset: found wired headset");
                return true;
            }
            if (type == 11) {
                AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "hasWiredHeadset: found USB audio device");
                return true;
            }
        }
        return false;
    }
    
    public void updateAudioDeviceState() {
        ThreadUtils.checkIsOnMainThread();
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "--- updateAudioDeviceState: wired headset=" + this.hasWiredHeadset + ", BT state=" + this.bluetoothManager.getState());
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "Device status: available=" + this.audioDevices + ", selected=" + this.selectedAudioDevice + ", user selected=" + this.userSelectedAudioDevice);
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_DISCONNECTING) {
            this.bluetoothManager.updateDevice();
        }
        final Set<AudioDevice> newAudioDevices = new HashSet<AudioDevice>();
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE) {
            newAudioDevices.add(AudioDevice.BLUETOOTH);
            if (!this.audioDevices.isEmpty() && !this.audioDevices.contains(AudioDevice.BLUETOOTH)) {
                if (this.manageBluetoothByDefault) {
                    this.userSelectedAudioDevice = AudioDevice.BLUETOOTH;
                }
                this.notifyBluetoothAudioDeviceStateListener(true);
            }
        }
        if (this.hasWiredHeadset) {
            newAudioDevices.add(AudioDevice.WIRED_HEADSET);
        }
        newAudioDevices.add(AudioDevice.SPEAKER_PHONE);
        if (this.hasEarpiece()) {
            newAudioDevices.add(AudioDevice.EARPIECE);
        }
        boolean audioDeviceSetUpdated = !this.audioDevices.equals(newAudioDevices);
        this.audioDevices = newAudioDevices;
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE && this.userSelectedAudioDevice == AudioDevice.BLUETOOTH) {
            this.userSelectedAudioDevice = AudioDevice.NONE;
        }
        if (!this.hasWiredHeadset && this.userSelectedAudioDevice == AudioDevice.WIRED_HEADSET) {
            this.userSelectedAudioDevice = AudioDevice.NONE;
        }
        final boolean needBluetoothAudioStart = this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE && (this.userSelectedAudioDevice == AudioDevice.NONE || this.userSelectedAudioDevice == AudioDevice.BLUETOOTH);
        final boolean needBluetoothAudioStop = (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING) && this.userSelectedAudioDevice != AudioDevice.NONE && this.userSelectedAudioDevice != AudioDevice.BLUETOOTH;
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED) {
            AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "Need BT audio: start=" + needBluetoothAudioStart + ", stop=" + needBluetoothAudioStop + ", BT state=" + this.bluetoothManager.getState());
        }
        if (needBluetoothAudioStop) {
            this.bluetoothManager.stopScoAudio();
            this.bluetoothManager.updateDevice();
        }
        if (needBluetoothAudioStart && !needBluetoothAudioStop && !this.bluetoothManager.startScoAudio()) {
            this.audioDevices.remove(AudioDevice.BLUETOOTH);
            this.notifyBluetoothAudioDeviceStateListener(false);
            audioDeviceSetUpdated = true;
        }
        AudioDevice newAudioDevice;
        if (this.userSelectedAudioDevice != AudioDevice.NONE) {
            newAudioDevice = this.userSelectedAudioDevice;
        }
        else {
            newAudioDevice = this.defaultAudioDevice;
        }
        if (newAudioDevice != this.selectedAudioDevice || audioDeviceSetUpdated) {
            this.setAudioDeviceInternal(newAudioDevice);
            AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "New device status: available=" + this.audioDevices + ", selected=" + this.selectedAudioDevice);
            if (this.audioManagerEvents != null) {
                this.audioManagerEvents.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }
        AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "--- updateAudioDeviceState done");
    }
    
    static {
        TAG = AppRTCAudioManager.class.getSimpleName();
        LOGGER = Logger.getInstance(com.hahalolo.call.webrtc.BaseClient.TAG);
    }
    
    public enum AudioDevice
    {
        SPEAKER_PHONE, 
        WIRED_HEADSET, 
        EARPIECE, 
        BLUETOOTH, 
        NONE;
    }
    
    public enum AudioManagerState
    {
        UNINITIALIZED, 
        PREINITIALIZED, 
        RUNNING;
    }
    
    private class WiredHeadsetReceiver extends BroadcastReceiver
    {
        private static final int STATE_UNPLUGGED = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int HAS_MIC = 1;
        
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra("state", 0);
            final int microphone = intent.getIntExtra("microphone", 0);
            final String name = intent.getStringExtra("name");
            AppRTCAudioManager.LOGGER.d(AppRTCAudioManager.TAG, "WiredHeadsetReceiver.onReceive" + AppRTCUtils.getThreadInfo() + ": a=" + intent.getAction() + ", s=" + ((state == 0) ? "unplugged" : "plugged") + ", m=" + ((microphone == 1) ? "mic" : "no mic") + ", n=" + name + ", sb=" + this.isInitialStickyBroadcast());
            AppRTCAudioManager.this.hasWiredHeadset = (state == 1);
            AppRTCAudioManager.this.notifyWiredHeadsetListener(state == 1, microphone == 1);
            if (!AppRTCAudioManager.this.manageHeadsetByDefault) {
                return;
            }
            if (AppRTCAudioManager.this.hasWiredHeadset) {
                AppRTCAudioManager.this.userSelectedAudioDevice = AudioDevice.WIRED_HEADSET;
            }
            AppRTCAudioManager.this.updateAudioDeviceState();
        }
    }
    
    public interface BluetoothAudioDeviceStateListener
    {
        void onStateChanged(final boolean p0);
    }
    
    public interface AudioManagerEvents
    {
        void onAudioDeviceChanged(final AudioDevice p0, final Set<AudioDevice> p1);
    }
    
    @Deprecated
    public interface OnAudioManagerStateListener
    {
        void onAudioChangedState(final AudioDevice p0);
    }
    
    public interface OnWiredHeadsetStateListener
    {
        void onWiredHeadsetStateChanged(final boolean p0, final boolean p1);
    }
}
