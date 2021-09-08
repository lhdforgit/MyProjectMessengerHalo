package com.halo.presentation.utils.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.halo.data.room.entity.MessageEntity
import com.halo.common.utils.ThumbImageUtils
import java.io.InputStream

class ChatMessageEntityLoader private constructor(
    urlLoader: ModelLoader<GlideUrl, InputStream>,
    modelCache: ModelCache<MessageEntity, GlideUrl>
) : BaseGlideUrlLoader<MessageEntity>(urlLoader, modelCache) {
    class Factory : ModelLoaderFactory<MessageEntity, InputStream> {
        private val modelCache = ModelCache<MessageEntity, GlideUrl>(500)

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MessageEntity, InputStream> {
            return ChatMessageEntityLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java), modelCache)
        }

        override fun teardown() {}
    }

    override fun handles(model: MessageEntity): Boolean {
        return true
    }

    override fun getUrl(model: MessageEntity?, width: Int, height: Int, options: Options): String {
//        val path = model?.attachmentTables?.firstOrNull()?.getAttachmentUrl()
        val uri = ThumbImageUtils.thumb(ThumbImageUtils.Size.MEDIA_WIDTH_360, "path", ThumbImageUtils.TypeSize._AUTO)
        return uri?:""
    }
}