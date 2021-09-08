package com.hahalolo.messager.bubble.conversation

import androidx.annotation.IntDef

@IntDef(
    BubbleConversationType.CONVERSATION_NONE,
    BubbleConversationType.CONVERSATION_HOME,
    BubbleConversationType.CONVERSATION_MESSAGE,
    BubbleConversationType.CONVERSATION_DETAIL,
    BubbleConversationType.CONVERSATION_CREATE,
    BubbleConversationType.CONVERSATION_WRITE,
    BubbleConversationType.CONVERSATION_MEMBERS,
    BubbleConversationType.CONVERSATION_CHANGE_NAME,
    BubbleConversationType.CONVERSATION_MEDIA,
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BubbleConversationType {
    companion object {
        const val CONVERSATION_NONE         = -1
        const val CONVERSATION_HOME         = 0
        const val CONVERSATION_MESSAGE      = 1
        const val CONVERSATION_DETAIL       = 2
        const val CONVERSATION_CREATE       = 3
        const val CONVERSATION_WRITE        = 4
        const val CONVERSATION_MEMBERS      = 6
        const val CONVERSATION_CHANGE_NAME  = 7
        const val CONVERSATION_MEDIA        = 8
    }
}