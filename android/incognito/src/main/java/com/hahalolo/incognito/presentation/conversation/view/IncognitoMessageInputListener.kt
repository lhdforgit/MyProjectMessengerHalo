package com.hahalolo.incognito.presentation.conversation.view

import androidx.lifecycle.LifecycleOwner
import com.halo.widget.repository.sticker.StickerRepository

interface IncognitoMessageInputListener {
    fun lifecycleOwner() : LifecycleOwner
    fun onSendMessageText(textSend: String): Boolean

    fun stickerRepository(): StickerRepository
}