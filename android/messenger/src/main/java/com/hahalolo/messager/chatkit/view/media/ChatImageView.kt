/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view.media

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMediaViewBinding
import com.halo.common.utils.SettableFuture
import com.halo.data.common.utils.Strings
import com.halo.data.entities.attachment.Attachment
import com.halo.editor.components.GlideDrawableListeningTarget
import com.halo.editor.glide.DecryptableStreamUriLoader.DecryptableUri
import kotlinx.android.synthetic.main.chat_gif_incoming_item.view.*

class ChatImageView : RelativeLayout {

    private lateinit var binding: ChatMediaViewBinding
    var requestManager: RequestManager? = null

    private var media: AttachmentTable? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.chat_media_view, this, false
        )
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun updateMedia(media: AttachmentTable?) {
        this.media = media
        requestUpdateLayout()
    }

    private fun requestUpdateLayout() {
        media?.run {
            onloadImageResource(this, binding.mediaIv)
            binding.videoMediaIv.visibility = if (this.isVideo()) View.VISIBLE else View.GONE
        } ?: run {
            binding.mediaIv.setImageDrawable(null)
            binding.videoMediaIv.visibility = View.GONE
        }
    }

    private fun onloadImageResource(item: AttachmentTable?, imageView: ImageView) {
        item?.run {

            item.getAttachmentUrl().run {
                takeIf { URLUtil.isNetworkUrl(this) }?.run {
                    requestManager?.load(item.getAttachmentUrl())
                        ?.into(imageView)
                } ?: run {
                    val result = SettableFuture<Boolean>()
                    requestManager?.load(DecryptableUri(Uri.parse(this)))
                        ?.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        ?.transition(DrawableTransitionOptions.withCrossFade())
                        ?.into(GlideDrawableListeningTarget(imageView, result))
                }
            }
        } ?: run {
            imageView.setImageDrawable(null)
        }
    }

    fun getTarget(): MutableList<ImageView> {
        val result = mutableListOf<ImageView>()
        result.add(binding.mediaIv)
        return result
    }

    fun invalidateLayout(requestManager: RequestManager?) {
        try {
            requestManager?.clear(binding.mediaIv)
            binding.mediaIv.setImageDrawable(null)
        }catch (e: Exception){e.printStackTrace()}
    }

    fun updateMedia(media: Attachment?) {
        media?.run {
            onloadImageResource(this, binding.mediaIv)
            binding.videoMediaIv.visibility = if (this.isVideo()) View.VISIBLE else View.GONE
        } ?: run {
            binding.mediaIv.setImageDrawable(null)
            binding.videoMediaIv.visibility = View.GONE
        }
    }
    private fun onloadImageResource(item: Attachment?, imageView: ImageView) {
        item?.run {
            item.url.run {
                takeIf { URLUtil.isNetworkUrl(this) }?.run {
                    requestManager?.load(item.url)
                        ?.into(imageView)
                } ?: run {
//                    val result = SettableFuture<Boolean>()
//                    requestManager?.load(DecryptableUri(Uri.parse(this)))
//                        ?.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                        ?.transition(DrawableTransitionOptions.withCrossFade())
//                        ?.into(GlideDrawableListeningTarget(imageView, result))

                    requestManager?.load(this)
                        ?.into(imageView)
                }
            }
        } ?: run {
            imageView.setImageDrawable(null)
        }
    }
}