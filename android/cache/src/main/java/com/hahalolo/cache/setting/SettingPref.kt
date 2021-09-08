package com.hahalolo.cache.setting

/**
 * Create by ndn
 * Create on 12/17/20
 * com.hahalolo.cache.setting
 */
interface SettingPref {

    fun setVideoSound(sound: Boolean)
    fun isVideoSound(): Boolean

    fun setBubbleChat(status: Boolean)
    fun isBubbleOpen(): Boolean

    fun setBubblePermissionRemind(status: Boolean)
    fun isShowRemindBubble() : Boolean
}