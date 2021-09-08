package com.halo.widget.renderemoji

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.halo.data.common.utils.Strings

open class RenderEmojiEdittext : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        text?.takeIf {
            it.isNotEmpty()
                    && it.length > start
                    && it.get(start) == ' '
                    && selectionEnd == selectionStart
                    && selectionEnd > start
        }?.toString()?.run {
            var textStart = this.substring(0, selectionEnd)
            val textEnd = this.substring(selectionEnd)
            val emoji = findEmojiRender(textStart)
            val newText: String
            if (emoji != null) {
                val emojiRenderSize = "${emoji.textRender} ".length
                textStart = textStart.substring(0, textStart.length - emojiRenderSize) + "${emoji.emoji} "
                newText = textStart + textEnd
                setText(newText)
                setSelection(textStart.length)
                return
            }
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    private fun findEmojiRender(text: String): EmojiRender? {
        return emojis.find {
            text.endsWith(" ${it.textRender} ") || TextUtils.equals(text, "${it.textRender} ")
        }
    }


    val emojis = mutableListOf(
        EmojiRender("\uD83D\uDE00", ":D", "U+1F600"),
        EmojiRender("\uD83D\uDE01", ":#)", "U+1F601"),
        EmojiRender("\uD83D\uDE02", "cwl", "U+1F602"),
        EmojiRender("\uD83E\uDD23", "=)", "U+1F603"),
        EmojiRender("\uD83D\uDE23", ">_<", "U+1F623"),
        EmojiRender("\uD83D\uDC94", "(u) or /<3", "U+1F494"),
    )

    class EmojiRender(
        val emoji: String,
        val textRender: String,
        val unicode: String,
    )
}