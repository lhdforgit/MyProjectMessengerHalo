/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.utils.glide

import android.content.Context
import android.util.Log.VERBOSE
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.editor.glide.AttachmentStreamUriLoader
import com.halo.editor.glide.AttachmentStreamUriLoader.AttachmentModel
import com.halo.editor.glide.DecryptableStreamUriLoader
import com.halo.editor.glide.DecryptableStreamUriLoader.DecryptableUri
import java.io.InputStream

/**
 * @author ndn
 * Created by ndn
 * Created on 5/25/18.
 * https://bumptech.github.io/glide/doc/configuration.html
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun applyOptions(
        context: Context,
        builder: GlideBuilder
    ) {
        super.applyOptions(context, builder)
        builder.setLogLevel(VERBOSE)
    }

    /**
     * prepend/append determine the order in which ModelLoaders are called.
     * At the beginning of a request, Glide looks at the given model and requested resource
     * and finds all possible paths using registered components to load the requested resource from the model.
     * Each of those paths is then tried in order until one succeeds or all fail.
     *
     *
     * By using append, you're adding your ModelLoader after any existing ModelLoaders for the model type (in this case String).
     * That means your ModelLoader will only be tried if the others for the model type fail.
     *
     *
     * Use prepend instead to indicate that you want your ModelLoader to be tried first.
     *
     *
     * I know that the calls the getUrl make this more confusing.
     * For simplicities sake we construct all DataFetchers in the order we will
     * try to call them when a request is made rather than trying to only call each ModelLoader
     * when we're definitely going to also call its DataFetcher.
     */
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        registry.append(
            DecryptableUri::class.java,
            InputStream::class.java,
            DecryptableStreamUriLoader.Factory(context)
        )
        registry.append(
            AttachmentModel::class.java,
            InputStream::class.java,
            AttachmentStreamUriLoader.Factory()
        )
        registry.append(
            MessageEntity::class.java,
            InputStream::class.java,
            ChatMessageEntityLoader.Factory()
        )
        registry.append(
            AttachmentTable::class.java,
            InputStream::class.java,
            ChatMessageAttachmentLoader.Factory()
        )
    }
}