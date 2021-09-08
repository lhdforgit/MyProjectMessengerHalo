package com.hahalolo.messager.presentation.message.adapter.holder

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel

abstract class ChatMessageViewHolder(
    view: View,
    protected val chatListener: ChatListener
) : RecyclerView.ViewHolder(view) {

    abstract fun onBind(message: ChatMessageModel)
    open fun viewTargets(): MutableList<ImageView>?=null


    protected fun widthScreen():Int{
        return Resources.getSystem().getDisplayMetrics().widthPixels
    }

    protected fun heightScreen():Int{
        return Resources.getSystem().getDisplayMetrics().heightPixels
    }

    companion object {
        const val MARGIN_GROUP = 1f
        const val MARGIN_NON_GROUP = 8f
    }
}