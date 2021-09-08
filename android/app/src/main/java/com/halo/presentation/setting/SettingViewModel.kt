package com.halo.presentation.setting


import com.hahalolo.cache.setting.SettingPref
import com.halo.presentation.base.AbsTokenViewModel
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 12/17/20
 * com.halo.presentation.setting
 */
class SettingViewModel
@Inject constructor(
    private val settingPref: SettingPref
) : AbsTokenViewModel() {

    val isVideoSound: Boolean
        get() = settingPref.isVideoSound()

    fun setVideoSound(sound: Boolean) {
        settingPref.setVideoSound(sound)
    }

    var permissionBubble = false

    val isOpenBubbleChat: Boolean
        get() = settingPref.isBubbleOpen()

    fun setBubbleChat(status: Boolean) {
        settingPref.setBubbleChat(status)
    }

}