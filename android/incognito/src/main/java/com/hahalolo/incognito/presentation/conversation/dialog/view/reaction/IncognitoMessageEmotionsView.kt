package com.hahalolo.incognito.presentation.conversation.dialog.view.reaction

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMessageEmotionsViewBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ThumbImageUtils

class IncognitoMessageEmotionsView : FrameLayout {
    lateinit var binding: IncognitoMessageEmotionsViewBinding
    lateinit var requestManager: RequestManager
    private var listener: IncognitoMessageEmotionsListener? = null

    private var listIcon = listOf(
        "https://media.hahalolo.com/2021/06/01/08/38/ff0f16dc2a2d3845aee201738c2fd854-1622536735_640xauto_high.jpg",
        "https://media.hahalolo.com/2021/06/01/08/38/426eb0ca1d0954000c1de3e9b1eb1879-1622536735_640xauto_high.jpg",
        "https://media.hahalolo.com/2021/06/01/08/38/bb569b987371f91ba2f5e7c9855ad03a-1622536735_640xauto_high.jpg",
        "https://media.hahalolo.com/2021/06/01/08/38/27835b33731955faf1209fb55cf11c2a-1622536735_1080xauto_high.jpg",
        "https://media.hahalolo.com/2021/06/01/08/38/08c37e0a9723d7fefbc7b73633e61147-1622536735_1080xauto_high.jpg",
        "https://media.hahalolo.com/2021/06/01/08/38/88eb258ecec46c875f9f60a5d9e34427-1622536735_640xauto_high.jpg"
    )

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.incognito_message_emotions_view,
            this,
            false
        )
        requestManager = Glide.with(this)
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAttachedToWindow) {
            updateLayout()
        }
    }

    private fun updateLayout() {
        listIcon.forEachIndexed { index, url ->
            when {
                index < 6 -> {
                    val icon = createIconView(url)
                    icon.setOnClickListener {
                        listener?.openMedia()
                    }
                    binding.emotionGroup.addView(icon)
                }
                index == 6 -> {
                    val icon = createLastImageView()
                    icon.setOnClickListener {
                        listener?.navigateGallery()
                        binding.emotionGroup.addView(icon)
                    }
                }
            }
        }
    }

    private fun createIconView(url: String): ImageView {
        val imageView = ShapeableImageView(context)
        GlideRequestBuilder.getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(url))
            .into(imageView)
        return imageView
    }

    private fun createLastImageView(): ImageView {
        val imageView = ShapeableImageView(context)
        GlideRequestBuilder.getCenterCropRequest(requestManager)
            .load(R.drawable.ic_incognito_add_emotion)
            .into(imageView)
        return imageView
    }

    private fun ShapeableImageView.initCreateImageView() {
        val widthScreen = resources.displayMetrics.widthPixels
        val widthImgGr = widthScreen - SizeUtils.dp2px(100f)
        val widthImage = widthImgGr / 7
        val params = LayoutParams(widthImage, widthImage)
        val margin = SizeUtils.dp2px(2f)
        params.setMargins(0, 0, margin, 0)
        this.layoutParams = params
        this.shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCornerSizes(14f)
            .build()
    }
}