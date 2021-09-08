package com.hahalolo.messager.chatkit.view.input

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.google.common.collect.Iterators
import com.halo.common.utils.Strings
import com.halo.data.room.entity.MemberEntity
import java.util.*
import java.util.regex.Pattern

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class MentionTextView : AppCompatTextView {

    private val MAX_LENGHT = 75
    private val MAX_LINES = 4
    private var enableLimit = false
    private val listTag: MutableList<MentionSpannableEntity> = ArrayList()

    private fun getTagEntity(key: String?): MentionSpannableEntity? {
        val index = Iterators.indexOf(listTag.iterator()) { input: MentionSpannableEntity? ->
            input != null && TextUtils.equals(
                input.key,
                key
            )
        }
        return if (index >= 0) {
            MentionSpannableEntity(
                listTag[index]
            )
        } else null
    }

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        initView()
    }

    private fun initView() {}

    fun setTextContent(
        textContent: String,
        mentions: MutableList<MemberEntity>?=null,
        enableLimit : Boolean = false
    ) {
        this.enableLimit = enableLimit
        mentions?.forEach {
            val mentionEntity =
                MentionSpannableEntity(
                    it.userId(),
                    it.memberName(),
                    it
                )
            listTag.add(mentionEntity)
        }
        formatText(MentionUtils.getContentMention(textContent, mentions?.map { it.userId() }))
    }

    private fun formatText(content: String) {
        Strings.log("onBindText addTextContent formatText "+ content)
        val stringList = sublistTag(content)
        Strings.log("onBindText addTextContent formatText stringList "+ stringList)
        val sb = SpannableStringBuilder()
        for (i in stringList.indices) {
            val spanEntity = stringList[i]
            Strings.log("onBindText addTextContent formatText stringList 1 "+ sb.toString())
            Strings.log("onBindText addTextContent formatText stringList 2 "+ spanEntity.text)
            Strings.log("onBindText addTextContent formatText stringList 3 "+ countLines(sb.toString()))
            Strings.log("onBindText addTextContent formatText stringList 4 "+ countLines(spanEntity.text))
            val maxLine: Boolean = countLines(sb.toString()) + countLines(spanEntity.text) >= MAX_LINES
            val maxLength = sb.length + spanEntity.text.length >= MAX_LENGHT
            Strings.log("onBindText addTextContent formatText maxLine "+ maxLine)
            Strings.log("onBindText addTextContent formatText maxLine "+ maxLength)
            if (enableLimit && (maxLine || maxLength)){
                var subContent: String = ""
                if (maxLine) {
                    val moreLine: Int = MAX_LINES - countLines(sb.toString())
                    subContent = subStringLines(spanEntity.text, moreLine)
                }else{
                    //max length
                    if (!spanEntity.isMention()) {
                        val lenght: Int = MAX_LENGHT - sb.length
                        if (lenght >= 0) {
                            subContent = spanEntity.text.substring(0, lenght)
                        }
                    } else {
                        subContent = ""
                    }
                }
                Strings.log("onBindText addTextContent formatText subContent "+ subContent)
                sb.append("$subContent...")
                break
            }else if (spanEntity.isMention()
//                && spanEntity.getEnd() <= sb.length()
            ) {
                val tagingSpaned =
                    MentionSpannableString(
                        spanEntity
                    )
                sb.append(tagingSpaned)
            } else {
                sb.append(spanEntity.content)
            }
        }
        super.setText(sb)
    }

    private fun countLines(str: String): Int {
        kotlin.runCatching {
            return str.lines().count()
        }
        return 0
    }

    private fun subStringLines(s: String, line: Int): String {
        kotlin.runCatching {
            if (!TextUtils.isEmpty(s)) {
                val lines = s.lines()
                if (s.length > line) {
                    val result = StringBuilder()
                    for (i in 0 until line) {
                        result.append(lines[i]).append(if (i == line - 1) "" else "\n")
                    }
                    return result.toString()
                }
            }
        }
        return ""
    }

    private fun sublistTag(content: String): List<MentionSpannableEntity> {
        val stringList: MutableList<MentionSpannableEntity> = ArrayList()
        val regex = MentionUtils.getRegex(listTag)
        var s = 0
        if (regex != null && !TextUtils.isEmpty(regex)) {
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(content)
            while (matcher.find()) {
                val group = matcher.group()
                if (s != matcher.start()) {
                    stringList.add(
                        MentionSpannableEntity(
                            content.substring(s, matcher.start()),
                            s,
                            matcher.start()
                        )
                    )
                }
                val spanEntity = getTagEntity(group.replace(MentionUtils.REGEX_TAG, ""))
                if (spanEntity != null) {
                    spanEntity.setLocation(matcher.start(), matcher.end())
                    stringList.add(spanEntity)
                } else {
                    stringList.add(
                        MentionSpannableEntity(
                            group,
                            matcher.start(),
                            matcher.end()
                        )
                    )
                }
                s = matcher.end()
            }
        }
        if (s != content.length) {
            stringList.add(
                MentionSpannableEntity(
                    content.substring(s),
                    s,
                    content.length
                )
            )
        }
        return stringList
    }
}