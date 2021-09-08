package com.hahalolo.messager.presentation.group.gallery

import android.view.View
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel

interface AttachmentListener {
    fun onClickAttachment(position : Int, mediaModel: MessengerManagerFileModel?){}
    fun onClickLastMedia(){}
    fun onClickMenu(view: View, mediaModel: MessengerManagerFileModel?){}
}