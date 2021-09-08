/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view.input

import android.text.TextUtils
import com.halo.data.room.entity.MemberEntity
import java.util.*

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class MentionSpannableEntity {
    var text: String
    var start = 0
    var end = 0
    var key: String? = null
    var content: String
    var member: MemberEntity? = null

    internal constructor(spanEntity: MentionSpannableEntity) {
        key = spanEntity.key
        text = spanEntity.text
        content = spanEntity.content
        member = spanEntity.member
    }

    constructor(content: String, memberEntity: MemberEntity?) {
        key = UUID.randomUUID().toString()
        text = String.format(MentionUtils.REGEX_FORMAT, key)
        this.content = MentionEditText.SPERATOR_TAG + content
        member = memberEntity
    }

    constructor(
        key: String,
        content: String,
        memberEntity: MemberEntity
    ) {
        this.key = key
        text = String.format(MentionUtils.REGEX_FORMAT, key)
        this.content = MentionEditText.SPERATOR_TAG + content
        member = memberEntity
    }

    internal constructor(text: String, start: Int, end: Int) {
        this.text = text
        content = text
        this.start = start
        this.end = end
    }

    fun setLocation(start: Int, end: Int) {
        this.start = start
        this.end = end
    }

    fun isMention(): Boolean{
        return!TextUtils.isEmpty(key) && !TextUtils.isEmpty(content)
    }
}