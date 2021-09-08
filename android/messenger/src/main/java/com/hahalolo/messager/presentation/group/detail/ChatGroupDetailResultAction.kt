package com.hahalolo.messager.presentation.group.detail

import androidx.annotation.StringDef
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailResultAction.Companion.BACK_HOME

@StringDef(BACK_HOME)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ChatGroupDetailResultAction {
    companion object {
        const val BACK_HOME = "BACK_HOME"
    }
}