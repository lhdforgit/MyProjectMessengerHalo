package com.hahalolo.incognito.presentation.conversation.view.sticker.gif

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.giphy.sdk.core.models.Media
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgGifItemBinding

class IncognitoMsgGifViewHolder(
    private val binding: IncognitoMsgGifItemBinding,
    private val onClick: (media: Media) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("CheckResult")
    fun onBind(media: Media) {

        val link = media.images.fixedHeightSmall.gifUrl?:""
        val url = if (TextUtils.isEmpty(link)){
            link
        }else{
            String.format(itemView.context.getString(com.halo.widget.sticker.R.string.media_giphy_url), media.getId())
        }

        Glide.with(itemView.context)
            .asGif()
            .load(url)
            .apply(RequestOptions.placeholderOf(ContextCompat.getDrawable(itemView.context, com.halo.widget.R.drawable.holder_rect)))
            .apply(RequestOptions.errorOf(ContextCompat.getDrawable(itemView.context, R.drawable.holder_rect)))
            .listener( object: RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(binding.icon)

        itemView.setOnClickListener {
            onClick.invoke(media)
        }
    }

    companion object {
        fun create(parent: ViewGroup, onClick: (media: Media) -> Unit): IncognitoMsgGifViewHolder {
            val binding = DataBindingUtil.inflate<IncognitoMsgGifItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.incognito_msg_gif_item, parent, false
            )
            return IncognitoMsgGifViewHolder(binding, onClick)
        }
    }
}