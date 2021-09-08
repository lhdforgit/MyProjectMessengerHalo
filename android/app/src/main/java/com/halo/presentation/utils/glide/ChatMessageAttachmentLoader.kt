package com.halo.presentation.utils.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.halo.data.room.table.AttachmentTable
import com.halo.common.utils.ThumbImageUtils
import java.io.InputStream

class ChatMessageAttachmentLoader private constructor(
    urlLoader: ModelLoader<GlideUrl, InputStream>,
    modelCache: ModelCache<AttachmentTable, GlideUrl>
) : BaseGlideUrlLoader<AttachmentTable>(urlLoader, modelCache) {

    class Factory : ModelLoaderFactory<AttachmentTable, InputStream> {
        private val modelCache = ModelCache<AttachmentTable, GlideUrl>(500)

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AttachmentTable, InputStream> {
            return ChatMessageAttachmentLoader(
                multiFactory.build(
                    GlideUrl::class.java,
                    InputStream::class.java
                ), modelCache
            )
        }

        override fun teardown() {}
    }

    override fun getUrl(
        model: AttachmentTable?,
        width: Int,
        height: Int,
        options: Options?
    ): String {
        val path = model?.getAttachmentUrl()
        val uri = ThumbImageUtils.thumb(
            ThumbImageUtils.Size.MEDIA_WIDTH_360,
            path,
            ThumbImageUtils.TypeSize._AUTO
        )
        return uri ?: ""
    }

    override fun handles(model: AttachmentTable): Boolean {
        return true
    }
}