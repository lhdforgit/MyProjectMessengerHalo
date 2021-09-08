package com.hahalolo.messager.presentation.message

import androidx.annotation.StringDef
import com.hahalolo.messager.presentation.message.ChatMessageResultAction.Companion.FINISH_CHAT

@StringDef(FINISH_CHAT)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ChatMessageResultAction {
    companion object {
        const val FINISH_CHAT = "FINISH_CHAT"
    }
}