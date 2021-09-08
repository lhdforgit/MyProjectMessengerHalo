/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view.input

import com.halo.data.room.entity.MessageEntity

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
object MentionUtils {
    const val REGEX_TAG = "::"
    const val REGEX_FORMAT = "::%s::"
    private const val REGEX_FORMAT_VIEW = "<@:%s>"
    @JvmStatic
    fun getRegex(listTag: List<MentionSpannableEntity>?): String? {
        val result = StringBuilder()
        if (listTag != null && !listTag.isEmpty()) {
            for (i in listTag.indices) {
                result.append(if (i == 0) "" else "|")
                    .append(String.format(REGEX_FORMAT, listTag[i].key))
            }
            return result.toString()
        }
        return null
    }


    fun getContentMention(content: String, keys: List<String?>?): String {
        var newContent = content
        if (keys != null && !keys.isEmpty()) {
            for (key in keys) {
                newContent = newContent.replace(
                    String.format(REGEX_FORMAT_VIEW, key), String.format(
                        REGEX_FORMAT, key
                    )
                )
            }
        }
        return newContent
    }

    fun getContentSubmit(content: String, list: MutableList<MentionSpannableEntity>?):String{
        var mentionText: String = content
        list?.forEach {
            it.member?.run {
                mentionText = mentionText.replace(it.text, "<@:" + this.userId() + ">")
            }
        }
        return mentionText
    }

    fun getContentCopy(entity: MessageEntity):String{
        var contentMention = entity.messageText()
        entity.memberMentions().forEach {
            contentMention = contentMention.replace("<@:${it.userId()}>" , "@${it.memberName()}" )
        }
        return contentMention
    }
}