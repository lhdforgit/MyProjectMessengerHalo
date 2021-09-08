/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.utils

import android.content.Context
import com.halo.data.room.entity.MessageEntity
import com.halo.content.R

object MsgSystemObject {
    private const val MSG_ADD_GROUP_BY_ROLE = "{{user}} added {{object}} as a group {{role}}"
    const val MSG_ADD_GROUP = "{{user}} added {{names}} to this conversation"
    private const val MSG_REMOVE_GROUP_BY_ROLE = "{{user}} removed {{object}} as a group {{role}}"
    private const val MSG_REMOVE_GROUP = "{{user}} removed {{object}} from the conversation"
    const val MSG_CREATE_GROUP = "{{user}} created this conversation"
    const val MSG_SET_NICKNAME = "{{user}} set the nickname for {{object}} to {{nickName}}"
    private const val MSG_LEFT_GROUP = "{{user}} has left the conversation"
    const val MSG_RENAME_GROUP = "{{user}} named the conversation {{name}}"
    const val MSG_RENAME_CONVERSATION = "{{user}} named the conversation {{nameConversation}}"
    const val MSG_REMOVE_NICK_NAME = "{{user}} cleared the nickname for {{object}}"
    const val MSG_REMOVE_GROUP_NAME = "{{user}} removed the name of the conversation"
    const val MSG_REMOVE_CHANGE_GROUP_AVATAR = "{{user}} has changed the conversation picture"
    private const val MSG_REMOVE_CHANGE_GROUP_COLOR = "{{user}} change the conversation color theme to {{color}}"
    private const val MSG_SEND_STICKER = "{{user}} send a sticker"
    private const val MSG_SEND_PHOTO = "{{user}} sent {{count}} image(s)"
    private const val MSG_SEND_DOCUMENT = "{{user}} sent {{count}} document(s)"
    private const val MSG_SEND_FILE = "{{user}} sent {{count}} file(s)"
    private const val MSG_SEND_VIDEO = "{{user}} sent {{count}} video(s)"
    private const val MSG_SEND_GIF = "{{user}} sent a gif"
    private const val MSG_SEEN_BY = "Seen by {{members}}"
    private const val MSG_DELETE_CONVERSATION = "{{user}} deleted this conversation"
     const val MSG_CHANGED_GROUP_PHOTO = "{{user}} changed the conversation photo"
    const val MSG_HAHA = "haha"
}