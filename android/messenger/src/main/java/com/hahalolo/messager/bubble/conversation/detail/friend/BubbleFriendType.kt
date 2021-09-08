package com.hahalolo.messager.bubble.conversation.detail.friend

import androidx.annotation.IntDef

@IntDef(
    BubbleFriendType.CREATE_GROUP,
    BubbleFriendType.ADD_MEMBER,
    BubbleFriendType.CREATE_CONVERSATION
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BubbleFriendType {
    companion object {
        const val CREATE_GROUP = 1
        const val ADD_MEMBER = 2
        const val CREATE_CONVERSATION = 3
    }
}
