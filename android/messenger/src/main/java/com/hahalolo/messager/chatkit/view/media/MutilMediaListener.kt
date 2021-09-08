/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view.media

import com.halo.data.room.table.AttachmentTable

interface MutilMediaListener {
    fun onClickViewMedia(position : Int, listImage :MutableList<AttachmentTable>?)
}