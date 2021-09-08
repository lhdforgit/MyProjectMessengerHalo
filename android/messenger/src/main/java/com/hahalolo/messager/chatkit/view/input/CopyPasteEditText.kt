package com.hahalolo.messager.chatkit.view.input

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatEditText
import com.hahalolo.messenger.R
import com.halo.common.utils.KeyboardUtils
import com.halo.widget.renderemoji.RenderEmojiEdittext

abstract class CopyPasteEditText : RenderEmojiEdittext {

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    abstract fun isOpenPopupMenu():Boolean

    fun initView(context: Context) {
        kotlin.runCatching {
            setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    if (isOpenPopupMenu() && v != null) {
                        KeyboardUtils.showSoftInput(v)
                        showMenu(context, v)
                        return true
                    }
                    return false
                }
            })
        }
    }

    private fun showMenu(context: Context, viewAnchor: View) {
        val wrapper: Context = ContextThemeWrapper(context, R.style.PopupMenu)
        val popup = PopupMenu(wrapper, viewAnchor)
        popup.menuInflater
            .inflate(R.menu.edit_text_copy_paste_menu, popup.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.CENTER_HORIZONTAL
        }
        val menu = popup.menu
        val copyBtn = menu.findItem(android.R.id.copy)
        val cutBtn = menu.findItem(android.R.id.cut)
        val selectAllBtn = menu.findItem(android.R.id.selectAll)
        val pasteBtn = menu.findItem(android.R.id.paste)
        copyBtn.isVisible = canCopy()
        cutBtn.isVisible = canCut()
        selectAllBtn.isVisible = canSelectAll()
        popup.setOnMenuItemClickListener { item ->
            try {
                when (item.itemId) {
                    android.R.id.copy -> {
                        onTextContextMenuItem(android.R.id.copy)
                        setSelection(selectionEnd)
                    }
                    android.R.id.cut -> {
                        onTextContextMenuItem(android.R.id.cut)
                    }
                    android.R.id.paste -> {
                        onTextContextMenuItem(android.R.id.paste)
                    }
                    android.R.id.selectAll -> {
                        setSelection(0, length())
                        showMenu(this.context, this)
                    }
                    else -> {

                    }
                }
            }catch (e: Exception) {e.printStackTrace()}
            true
        }
        popup.show()
    }

    private fun currentText(): String {
        return text?.toString() ?: ""
    }

    private fun canCopy(): Boolean {
        return selectionStart < selectionEnd
    }

    private fun canCut(): Boolean {
        return selectionStart < selectionEnd
    }

    private fun canSelectAll(): Boolean {
        return currentText().isNotEmpty() && (selectionStart > 0 || selectionEnd < length())
    }
}