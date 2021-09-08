package com.hahalolo.cache.setting

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by ndn
 * Create on 12/17/20
 * com.hahalolo.cache.setting
 */
@Singleton
class SettingPrefImpl
@Inject constructor(
    val context: Context
) : SettingPref {

    companion object {
        const val VIDEO_SOUND_PREF = "SettingPref-Video-Sound"
        const val VIDEO_SOUND_PREF_KEY = "SettingPref-Video-Sound-Key"
        const val STATUS_BUBBLE_PREF_KEY = "SettingPref-STATUS_BUBBLE_PREF_KEY"
        const val STATUS_BUBBLE_PERMISSION_PREF_KEY =
            "SettingPref-STATUS_BUBBLE_PERMISSION_PREF_KEY"
    }

    var videoSoundSharedPref: SharedPreferences

    init {
        videoSoundSharedPref = context.getSharedPreferences(VIDEO_SOUND_PREF, Context.MODE_PRIVATE)
    }

    override fun setVideoSound(sound: Boolean) {
        val editor = videoSoundSharedPref.edit()
        editor.putBoolean(VIDEO_SOUND_PREF_KEY, sound)
        editor.apply()
    }

    override fun isVideoSound(): Boolean {
        return videoSoundSharedPref.getBoolean(VIDEO_SOUND_PREF_KEY, false)
    }

    override fun setBubbleChat(status: Boolean) {
        val editor = videoSoundSharedPref.edit()
        editor.putBoolean(STATUS_BUBBLE_PREF_KEY, status)
        editor.apply()
    }

    override fun isBubbleOpen(): Boolean {
        return videoSoundSharedPref.getBoolean(STATUS_BUBBLE_PREF_KEY, false)
    }

    override fun setBubblePermissionRemind(status: Boolean) {
        val editor = videoSoundSharedPref.edit()
        editor.putBoolean(STATUS_BUBBLE_PERMISSION_PREF_KEY, status)
        editor.apply()
    }

    override fun isShowRemindBubble(): Boolean {
        return videoSoundSharedPref.getBoolean(STATUS_BUBBLE_PERMISSION_PREF_KEY, true)
    }
}