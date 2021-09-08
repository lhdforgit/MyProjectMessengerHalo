package com.halo.widget.sticker.sticker_group.sticker.tab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.URLUtil
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.halo.widget.sticker.R
import com.halo.widget.sticker.databinding.StickerTabViewBinding

class StickerTabView : FrameLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    lateinit var binding: StickerTabViewBinding

    private fun initView() {
        binding = StickerTabViewBinding.inflate(LayoutInflater.from(context))
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private var requestManager: RequestManager? = null
    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    fun updateTabIcon(link: String?) {
        binding.avLoading.visibility = View.VISIBLE
        val linkTab =
            if (!URLUtil.isValidUrl(link)) context.getString(R.string.halo_url) + link else link
        requestManager
            ?.load(linkTab)
            ?.listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.avLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.avLoading.visibility = View.GONE
                    return false
                }
            })
            ?.into(binding.iconTab)
    }

    fun onTabSelected() {
        binding.layoutTab.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.bg_sticker_tab_selected_color
            )
        )
    }

    fun onTabUnselected() {
        binding.layoutTab.setBackgroundColor(Color.TRANSPARENT)
    }
}