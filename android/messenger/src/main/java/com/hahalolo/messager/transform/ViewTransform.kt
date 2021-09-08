/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.transform

import com.hahalolo.messager.chatkit.view.input.MentionSpannableEntity
import com.halo.data.room.entity.MessageEntity

// Tranform object use for View
object ViewTransform {
    /*Edit Message*/
    fun buildTextMentionInput(message: MessageEntity, listTag: MutableList<MentionSpannableEntity>): String {
        try {
            var result = "message"
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return message.messageText()
    }
    /*Edit Message END*/

}