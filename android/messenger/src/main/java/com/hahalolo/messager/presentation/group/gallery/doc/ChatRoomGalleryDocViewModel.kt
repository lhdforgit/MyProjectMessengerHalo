/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery.doc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.presentation.group.gallery.adapter.ManagerFileType
import com.halo.data.repository.attachment.AttachmentRepository
import com.halo.di.AbsentLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChatRoomGalleryDocViewModel @Inject constructor(
    private val attachmentRepository: AttachmentRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController){
    var channelId = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val docAttachment = flowOf(
        channelId.asFlow().flatMapLatest { channelId ->
            attachmentRepository.getAttachmentsPaging(
                token = token(),
                workspaceId = "0",
                channelId = channelId,
                type = ManagerFileType.DOCUMENT_TYPE
            )
        }
    ).flattenMerge(2)
}