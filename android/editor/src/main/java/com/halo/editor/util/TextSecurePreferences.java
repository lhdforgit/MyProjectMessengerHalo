/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.content.Context;
import android.hardware.Camera;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextSecurePreferences {

    private static final String ATTACHMENT_ENCRYPTED_SECRET = "pref_attachment_encrypted_secret";
    private static final String ATTACHMENT_UNENCRYPTED_SECRET = "pref_attachment_unencrypted_secret";
    public static final String DIRECT_CAPTURE_CAMERA_ID = "pref_direct_capture_camera_id";
    private static final String HAS_SEEN_VIDEO_RECORDING_TOOLTIP = "camerax.fragment.has.dismissed.video.recording.tooltip";

    public static void setAttachmentEncryptedSecret(@NonNull Context context, @NonNull String secret) {
        setStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, secret);
    }

    public static void setAttachmentUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
        setStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, secret);
    }

    public static @Nullable
    String getAttachmentEncryptedSecret(@NonNull Context context) {
        return getStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, null);
    }

    public static @Nullable
    String getAttachmentUnencryptedSecret(@NonNull Context context) {
        return getStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, null);
    }

    public static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setDirectCaptureCameraId(Context context, int value) {
        setIntegerPrefrence(context, DIRECT_CAPTURE_CAMERA_ID, value);
    }

    @SuppressWarnings("deprecation")
    public static int getDirectCaptureCameraId(Context context) {
        return getIntegerPreference(context, DIRECT_CAPTURE_CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    private static int getIntegerPreference(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    private static void setIntegerPrefrence(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static boolean hasSeenVideoRecordingTooltip(Context context) {
        return getBooleanPreference(context, HAS_SEEN_VIDEO_RECORDING_TOOLTIP, false);
    }

    public static void setHasSeenVideoRecordingTooltip(Context context, boolean value) {
        setBooleanPreference(context, HAS_SEEN_VIDEO_RECORDING_TOOLTIP, value);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }
}
