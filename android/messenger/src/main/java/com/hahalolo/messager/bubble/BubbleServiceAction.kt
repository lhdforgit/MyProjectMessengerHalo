/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.bubble

import androidx.annotation.StringDef
import com.hahalolo.messager.bubble.BubbleServiceAction.Companion.LOG_OUT
import com.hahalolo.messager.bubble.BubbleServiceAction.Companion.OPEN_BUBBLE_CONVERSATION
import com.hahalolo.messager.bubble.BubbleServiceAction.Companion.PUSH_NOTIFY
import com.hahalolo.messager.bubble.BubbleServiceAction.Companion.TAKE_FILE_MESSAGE
import com.hahalolo.messager.bubble.BubbleServiceAction.Companion.TAKE_MEDIA_MESSAGE

@StringDef(PUSH_NOTIFY, OPEN_BUBBLE_CONVERSATION, TAKE_MEDIA_MESSAGE, TAKE_FILE_MESSAGE, LOG_OUT)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BubbleServiceAction {
    companion object {
        const val PUSH_NOTIFY           = "PUSH_NOTIFY"
        const val OPEN_BUBBLE_CONVERSATION           = "OPEN_BUBBLE_CONVERSATION"

        const val TAKE_MEDIA_MESSAGE    = "TAKE_MEDIA_MESSAGE"
        const val TAKE_FILE_MESSAGE     = "TAKE_FILE_MESSAGE"

        const val LOG_OUT               = "LOG_OUT_BUBBLE"
    }
}