/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.tone;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.halo.widget.R;

/**
 * @author ngannd
 * Create by ngannd on 22/07/2019
 */
public final class HaloTone {

    private HaloTone() {
    }

    public static void playReactionTone(@NonNull Context context) {
        playTone(context, R.raw.reaction, 0.1f);
    }

    public static void playTenTone(@NonNull Context context) {
        playTone(context, R.raw.ten, 0.0f);
    }

    public static void playMessageTone(@NonNull Context context) {
        playTone(context, R.raw.message, 0.0f);
    }

    private static void playTone(
            @NonNull Context context,
            @RawRes int toneRes,
            float volume) {
        try {
            if (volume > 0.0f) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(context)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        playWithRingtoneManager(context, toneRes, volume);
                    } else {
                        playMediaPool(context, toneRes, volume);
                    }
                } else {
                    playMediaPool(context, toneRes, volume);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(context)) {
                        playWithRingtoneManager(context, toneRes, 0.0f);
                    } else {
                        playMediaPool(context, toneRes, 0.0f);
                    }
                } else {
                    playWithRingtoneManager(context, toneRes, 0.0f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playWithRingtoneManager(
            @NonNull Context context,
            @RawRes int toneRes,
            float volume) {
        try {
            Uri path = Uri.parse("android.resource://" + context.getPackageName() + "/" + toneRes);
            // The line below will set it as a default ring tone replace
            // RingtoneManager.TYPE_RINGTONE with RingtoneManager.TYPE_NOTIFICATION
            // to set it as a notification tone
            if (path != null) {
                RingtoneManager.setActualDefaultRingtoneUri(
                        context.getApplicationContext(),
                        RingtoneManager.TYPE_NOTIFICATION,
                        path);
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), path);
                if (volume != 0.0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        r.setVolume(volume);
                    }
                }
                r.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playMediaPool(
            @NonNull Context context,
            @RawRes int toneRes,
            float volume) {
        MediaPlayerPool mediaPlayerPool = new MediaPlayerPool(context, 1);
        mediaPlayerPool.playSound(toneRes, volume);
    }
}