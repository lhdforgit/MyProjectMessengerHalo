/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.utils

import androidx.annotation.StringDef
import com.hahalolo.messager.chatkit.utils.ChatConstant.Companion.HAHA_GIF

@StringDef(HAHA_GIF)
@Retention(AnnotationRetention.SOURCE)
internal annotation class ChatConstant {
    companion object {
        const val HAHA_GIF: String = "/haha.gif"
    }
}