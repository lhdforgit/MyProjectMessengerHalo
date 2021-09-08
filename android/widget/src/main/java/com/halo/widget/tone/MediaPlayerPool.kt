/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.tone

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RawRes


/**
 * @author ngannd
 * Create by ngannd on 08/06/2019
 */

class MediaPlayerPool(context: Context, maxStreams: Int) {
    private val context: Context = context.applicationContext

    private val mediaPlayerPool = mutableListOf<MediaPlayer>().also {
        for (i in 0..maxStreams) it += buildPlayer()
    }
    private val playersInUse = mutableListOf<MediaPlayer>()

    private fun buildPlayer() = MediaPlayer().apply {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .build()
            )
        }
        setOnPreparedListener { start() }
        setOnCompletionListener { recyclePlayer(it) }
    }

    /**
     * Returns a [MediaPlayer] if one is available,
     * otherwise null.
     */
    private fun requestPlayer(): MediaPlayer? {
        return if (mediaPlayerPool.isNotEmpty()) {
            mediaPlayerPool.removeAt(0).also {
                playersInUse += it
            }
        } else null
    }

    fun clearPlayer() {
        mediaPlayerPool.clear()
    }

    private fun recyclePlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.reset()
        playersInUse -= mediaPlayer
        mediaPlayerPool += mediaPlayer
    }

    fun playSound(
        @RawRes rawResId: Int,
        volume: Float = 0.0f
    ) {
        try {
            val assetFileDescriptor = context.resources.openRawResourceFd(rawResId) ?: return
            val mediaPlayer = requestPlayer() ?: return
            if (volume > 0.0f) {
                mediaPlayer.setVolume(volume, volume)
            }
            mediaPlayer.run {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.declaredLength
                )
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}