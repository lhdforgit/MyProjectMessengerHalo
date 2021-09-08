/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.net.Uri

/**
 * Represents a folder that's shown in [MediaPickerFolderFragment].
 */
class MediaFolder(
    val thumbnailUri: Uri? = null,
    val title: String? = null,
    val itemCount: Int = 0,
    val bucketId: String? = null,
    val folderType: FolderType? = null
) {
    enum class FolderType {
        NORMAL, CAMERA
    }
}