/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.commons

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener

/**
 * Created by ndngan on 10/23/2018.
 * Callback for implementing images loading in message list
 */
interface ImageLoader {

    fun getRequestManager(): RequestManager

    fun loadAvatar(imageView: ImageView?, userID: String?, url: String?) {
    }

    fun loadSmallAvatar(imageView: ImageView?, url: String?) {
    }

    fun loadImage(imageView: ImageView?, url: String?, payload: Any?, requestListener: RequestListener<Drawable>?)

    fun loadSticker(imageView: ImageView?, url: String?, payload: Any?, requestListener: RequestListener<Drawable>?) {
    }

    fun loadGif(imageView: ImageView?, url: String?, payload: Any?, requestListener: RequestListener<GifDrawable>?) {
    }
    fun loadHahaGif(imageView: ImageView?, url: String?, payload: Any?, requestListener: RequestListener<GifDrawable>?) {
    }
}