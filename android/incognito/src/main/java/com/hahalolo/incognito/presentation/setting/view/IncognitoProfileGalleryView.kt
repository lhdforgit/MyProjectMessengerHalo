package com.hahalolo.incognito.presentation.setting.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoProfileGalleryViewBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ThumbImageUtils


class IncognitoProfileGalleryView : FrameLayout {

    lateinit var binding: IncognitoProfileGalleryViewBinding
    lateinit var requestManager: RequestManager
    private var listener: IncognitoGalleryListener? = null


    private var listImage = listOf(
        "https://media.hahalolo.com/2020/12/09/09/5c205932d577a7125c98425e201209095558G5_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ByPMDdluA8HZbkSt_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ZK75NiXC95a4R0DE_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ELgJYpEWOaEk0q5c_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/Hw96vG2T0GsXHa4y_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/t7HBgPJaiUvY5xKx_1080xauto_high.jpg"
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
            layoutInflater,
            R.layout.incognito_profile_gallery_view,
            this,
            false
        )
        requestManager = Glide.with(this)
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun setListener(listener: IncognitoGalleryListener) {
        this.listener = listener
        updateLayout()
    }

    private fun updateLayout() {
        binding.navigateBt.setOnClickListener { listener?.navigateGallery() }
        listImage.forEachIndexed { index, url ->
            when {
                index < 4 -> {
                    val imageView = createImageView(url)
                    imageView.setOnClickListener { listener?.openMedia(url) }
                    binding.imageGroup.addView(imageView)
                }
                index == 4 -> {
                    val imageView = createLastImageView()
                    imageView.setOnClickListener { listener?.navigateGallery() }
                    binding.imageGroup.addView(imageView)
                }
                else -> {
                    return
                }
            }
        }
    }


    private fun createImageView(url: String): ImageView {
        val imageView = ShapeableImageView(context)
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(url))
            .into(imageView)
        imageView.initCreateImageView()
        return imageView
    }

    private fun createLastImageView(): ImageView {
        val imageView = ShapeableImageView(context)
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(R.drawable.ic_incognito_gallery_last_item)
            .into(imageView)
        imageView.initCreateImageView()
        return imageView
    }

    private fun ShapeableImageView.initCreateImageView() {
        val widthScreen = resources.displayMetrics.widthPixels
        val widthImgGr = widthScreen - SizeUtils.dp2px(100f)
        val widthImage = widthImgGr / 5
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

interface IncognitoGalleryListener {
    fun navigateGallery()
    fun openMedia(url: String)
}