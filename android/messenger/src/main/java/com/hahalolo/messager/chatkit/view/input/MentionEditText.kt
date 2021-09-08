package com.hahalolo.messager.chatkit.view.input

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.os.BuildCompat
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import com.google.common.collect.Iterators
import com.hahalolo.messager.chatkit.view.input.MentionSpannableString
import com.hahalolo.messager.chatkit.view.input.MentionUtils.REGEX_TAG
import com.hahalolo.messager.chatkit.view.input.MentionUtils.getRegex
import com.hahalolo.messager.transform.ViewTransform.buildTextMentionInput
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MessageEntity
import java.util.*
import java.util.regex.Pattern

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class MentionEditText : CopyPasteEditText {

    private var indexEnd = -1
    private var indexStart = -1
    private val listTag: MutableList<MentionSpannableEntity> = mutableListOf()

    private fun removeTagEntity(key: String?) {
        Iterators.removeIf(listTag.iterator()) { input: MentionSpannableEntity? ->
            input != null && TextUtils.equals(
                input.key,
                key
            )
        }
    }

    fun addMentions(member: MemberEntity) {
        try {
            if (text != null && indexEnd <= text!!.length && indexEnd > indexStart && indexStart >= 0) {
                val query = getBaseText(indexStart + 1, indexEnd)
                val name = member.getMentionName(query)
                val spanEntity = addNewTag(name, member)
                val resultStart = getBaseText(0, indexStart)
                val resultEnd = getBaseText(indexEnd, text!!.length)
                indextSelect = resultStart.length + spanEntity.content.length + 1
                formatText(resultStart + spanEntity.text + " " + resultEnd)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addNewTag(name: String?, member: MemberEntity?): MentionSpannableEntity {
        val spanEntity: MentionSpannableEntity
        spanEntity = MentionSpannableEntity(name!!, member)
        listTag.add(spanEntity)
        return spanEntity
    }

    fun getTagEntity(key: String?): MentionSpannableEntity? {
        val index = Iterators.indexOf(listTag.iterator()) { input: MentionSpannableEntity? ->
            input != null && TextUtils.equals(
                input.key,
                key
            )
        }
        return if (index >= 0) {
            MentionSpannableEntity(listTag[index])
        } else null
    }

    private var indextSelect = -1
    private var lastString: String? = null

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        initView()
    }

    private fun initView() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val thisString = s?.toString() ?: ""
                if (mentionListener != null) {
                    mentionListener!!.onTextChange(thisString)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val thisString = s?.toString() ?: ""
                if (!TextUtils.equals(thisString, lastString)) {
                    lastString = thisString
                    if (checkRemoveTag()) return
                }
                checkQueryTagFriend(thisString)
            }
        }
        addTextChangedListener(textWatcher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            customInsertionActionModeCallback = callback
        }
        customSelectionActionModeCallback = callback
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    var contentCopy: String? = null
    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.copy && mentionListener != null && mentionListener!!.enablePopupMenu()) {
            contentCopy = textCopy()
            if (!TextUtils.isEmpty(contentCopy)) {
                copyText(contentCopy!!)
            }
            return true
        }
        return super.onTextContextMenuItem(id)
    }

    var callback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId == android.R.id.copy) {
                contentCopy = textCopy()
                return false
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            if (!TextUtils.isEmpty(contentCopy)) {
                copyText(contentCopy!!)
            }
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val enable = mentionListener != null && mentionListener!!.enablePopupMenu()
            if (enable) menu.clear()
            return true
        }
    }
    private fun textCopy(): String{
        var result = ""

        val start = selectionStart
        val end = selectionEnd
        val textBase = if (text != null) text.toString() else ""
        result =  if (start in 0..end && end <= textBase.length) {
            textBase.substring(start, end)
        } else ""
        Strings.log("textCopy textCopy "+ result)
        return result
    }

    private fun copyText(s: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", s)
            clipboard.setPrimaryClip(clip)
            contentCopy = ""
            Toast.makeText(context, R.string.taging_coppy_complete, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatText(content: String) {
        val stringList = sublistTag(content)
        val sb = SpannableStringBuilder()
        for (i in stringList.indices) {
            val spanEntity = stringList[i]
            if (spanEntity.isMention() //                    && spanEntity.getEnd() <= sb.length()
            ) {
                val tagingSpaned = MentionSpannableString(spanEntity)
                sb.append(tagingSpaned)
            } else {
                sb.append(spanEntity.content)
            }
        }
        lastString = sb.toString()
        super.setText(sb)
        if (indextSelect >= 0 && indextSelect < sb.length) {
            setSelection(indextSelect)
            indextSelect = -1
        } else {
            setSelection(sb.length)
        }
    }

    override fun setSelection(index: Int) {
        try {
            // Nội dung tin nhắn input có giới hạn số lượng kí tự
            super.setSelection(index)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setMessageEdit(message: MessageEntity) {
        indextSelect = -1
        listTag.clear()

        message.memberMentions().forEach {
            val mentionEntity =
                MentionSpannableEntity(
                    it.userId(),
                    it.memberName(),
                    it
                )
            listTag.add(mentionEntity)
        }
        formatText(MentionUtils.getContentMention(message.messageText(),
            message.mentionIds()))
    }

    fun setInitText(inputText: String?) {
        listTag.clear()
        formatText(inputText ?: "")
    }

    fun clearText() {
        listTag.clear()
        formatText("")
    }

    val textBase: String
        get() = if (text == null) "" else getBaseText(0, text!!.length)
    val textContent: String
        get() = if (text == null) "" else text.toString()

    private fun checkRemoveTagEntity(spanEntity: MentionSpannableEntity) {
        //remove if text base not have key taging
        if (!textBase.contains(spanEntity.key!!)) {
            removeTagEntity(spanEntity.key)
        }
    }

    private fun checkRemoveTag(): Boolean {
        val editable = text
        if (editable != null) {
            val content = editable.toString()
            val spans = editable.getSpans(
                0,
                editable.length,
                MentionSpannableString.MentionColorSpan::class.java
            )
            if (spans != null && spans.size > 0) {
                val hashSet = HashSet(Arrays.asList(*spans))
                val list = ArrayList(hashSet)
                for (colorSpan in list) {
                    val start = editable.getSpanStart(colorSpan)
                    val end = editable.getSpanEnd(colorSpan)
                    if (start < end) {
                        val text = content.substring(start, end)
                        if (colorSpan.spanEntity != null
                            && !TextUtils.equals(text, colorSpan.spanEntity.content)
                        ) {
                            checkRemoveTagEntity(colorSpan.spanEntity)
                            indextSelect = selectionEnd
                            formatText(
                                getBaseText(0, start) + text + getBaseText(
                                    end,
                                    editable.length
                                )
                            )
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun sublistTag(content: String?): List<MentionSpannableEntity> {
        val stringList: MutableList<MentionSpannableEntity> = ArrayList()
        val regex = getRegex(listTag)
        var s = 0
        if (regex != null && !TextUtils.isEmpty(regex)) {
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(content)
            while (matcher.find()) {
                val group = matcher.group()
                if (s != matcher.start()) {
                    stringList.add(
                        MentionSpannableEntity(
                            content!!.substring(s, matcher.start()),
                            s,
                            matcher.start()
                        )
                    )
                }
                val spanEntity = getTagEntity(group.replace(REGEX_TAG, ""))
                if (spanEntity != null) {
                    spanEntity.setLocation(matcher.start(), matcher.end())
                    stringList.add(spanEntity)
                } else {
                    stringList.add(MentionSpannableEntity(group, matcher.start(), matcher.end()))
                }
                s = matcher.end()
            }
        }
        if (content != null && s != content.length) {
            stringList.add(MentionSpannableEntity(content.substring(s), s, content.length))
        }
        return stringList
    }

    private fun checkQueryTagFriend(text: String) {
        if (mentionListener != null) {
            val selectCurrent = selectionEnd
            if (selectCurrent >= 0 && selectCurrent <= text.length && !TextUtils.isEmpty(
                    text.substring(
                        0,
                        selectCurrent
                    )
                )
                && text.substring(0, selectCurrent).contains(SPERATOR_TAG)
            ) {
                indexStart = text.substring(0, selectCurrent).lastIndexOf(SPERATOR_TAG)
                indexEnd = selectCurrent
                if (indexEnd > indexStart && indexStart >= 0) {
                    val textQuery = text.substring(indexStart, indexEnd)
                    val pattern = Pattern.compile(SPERATOR_NOT_TAG)
                    val matcher = pattern.matcher(textQuery)
                    mentionListener!!.onQueryTagFriend(
                        if (matcher.find()) null else textQuery.substring(
                            1
                        )
                    )
                } else {
                    mentionListener!!.onQueryTagFriend(null)
                }
            } else {
                resetIndex()
                mentionListener!!.onQueryTagFriend(null)
            }
        }
    }

    private fun getBaseText(startIn: Int, end: Int): String {
        var start = startIn
        return try {
            val editable = text
            var result = ""
            if (editable != null) {
                val content = editable.toString()
                val spans = editable.getSpans(
                    0,
                    editable.length,
                    MentionSpannableString.MentionColorSpan::class.java
                )
                if (spans != null && spans.size > 0) {
                    val hashSet = HashSet(Arrays.asList(*spans))
                    val list = ArrayList(hashSet)
                    list.sortWith { tagingColorSpan: MentionSpannableString.MentionColorSpan?,
                                    t1: MentionSpannableString.MentionColorSpan? ->
                        editable.getSpanStart(tagingColorSpan) - editable.getSpanStart(t1)
                    }
                    val resultBuilder = StringBuilder()
                    for (i in list.indices) {
                        if (start < end) {
                            val currentSpan = list[i]
                            val spanStart = editable.getSpanStart(currentSpan)
                            val spanEnd = editable.getSpanEnd(currentSpan)
                            if (start <= spanStart
                                && spanEnd <= end
                            ) {
                                // chua span trong khong start-end
                                resultBuilder.append(content.substring(start, spanStart))
                                resultBuilder.append(currentSpan.spanEntity.text)
                                start = spanEnd
                            } else if (start < Math.min(end, spanEnd)) {
                                resultBuilder.append(
                                    content.substring(
                                        start,
                                        Math.min(end, spanEnd)
                                    )
                                )
                                start = Math.min(end, spanEnd)
                            }
                        }
                    }
                    result = resultBuilder.toString()
                    result = result + content.substring(start, end)
                } else if (start < end) {
                    return content.substring(start, end)
                }
            }
            result
        } catch (e: Exception) {
            ""
        }
    }

    private fun resetIndex() {
        indexStart = -1
        indexEnd = -1
    }

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
        val ic = super.onCreateInputConnection(editorInfo)
        EditorInfoCompat.setContentMimeTypes(
            editorInfo,
            arrayOf("image/jpg", "image/png", "image/jpeg", "image/gif")
        )
        val callback = object : InputConnectionCompat.OnCommitContentListener{
            override fun onCommitContent(
                inputContentInfo: InputContentInfoCompat?,
                flags: Int,
                opts: Bundle?
            ): Boolean {
                if (BuildCompat.isAtLeastNMR1() && flags and
                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION != 0
                ) {
                    try {
                        inputContentInfo?.requestPermission()
                    } catch (e: Exception) {
                        return false // return false if failed
                    }
                }
                // read and display inputContentInfo asynchronously.
                // call inputContentInfo.releasePermission() as needed.
                val content = inputContentInfo?.contentUri
                val contentQuery = content?.getQueryParameter("fileName")
                val linkUri = inputContentInfo?.linkUri

                linkUri?.toString()?.takeIf { it.isNotEmpty() && URLUtil.isValidUrl(it) }?.run {
                    mentionListener?.onInsertStickerFromKeybroad(contentQuery!=null
                            && contentQuery.contains("sticker"),
                        linkUri
                    )
                }
                return true // return true if succeeded
            }
        }
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback)
    }

    private var mentionListener: MentionListener? = null

    fun setMentionListener(mentionListener: MentionListener?) {
        this.mentionListener = mentionListener
    }

    fun mentionModel(): MutableList<MentionSpannableEntity>{
        kotlin.runCatching {
            val result: MutableList<MentionSpannableEntity> = ArrayList()
            val editable = text
            if (editable != null) {
                val content = editable.toString()
                val spans = editable.getSpans(
                    0,
                    editable.length,
                    MentionSpannableString.MentionColorSpan::class.java
                )
                var spaceStart = 0
                if (content.startsWith(" ") || content.startsWith("\n")) {
                    val len = length()
                    while (spaceStart < len
                        && (content[spaceStart] == ' '
                                || content[spaceStart] == '\n')
                    ) {
                        spaceStart++
                    }
                }
                if (spans != null && spans.size > 0) {
                    val hashSet = HashSet(Arrays.asList(*spans))
                    val list = ArrayList(hashSet)
                    for (colorSpan in list) {
                        val start = editable.getSpanStart(colorSpan)
                        val end = editable.getSpanEnd(colorSpan)
                        if (start < end) {
                            val text = content.substring(start, end)
                            val spanEntity = colorSpan.spanEntity
                            if (TextUtils.equals(text, colorSpan.spanEntity.content)) {
                                spanEntity.setLocation(start - spaceStart, end - spaceStart)
                                result.add(spanEntity)
                            }
                        }
                    }
                }
            }
            return result
        }.getOrElse {
            return mutableListOf<MentionSpannableEntity>()
        }
    }

    override fun isOpenPopupMenu(): Boolean {
        return mentionListener?.enablePopupMenu()==true
    }

    interface MentionListener {
        fun enablePopupMenu(): Boolean {
            return false
        }

        fun onQueryTagFriend(s: String?)
        fun onTextChange(s: String?)
        fun onInsertStickerFromKeybroad(sticker: Boolean, uri: Uri?)
    }

    companion object {
        const val SPERATOR_TAG = "@"
        private const val SPERATOR_NOT_TAG = "@[^\\w]"
    }
}