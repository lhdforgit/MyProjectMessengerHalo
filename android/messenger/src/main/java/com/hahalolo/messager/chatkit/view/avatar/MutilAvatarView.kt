package com.hahalolo.messager.chatkit.view.avatar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MutilAvatar2ViewBinding
import com.hahalolo.messenger.databinding.MutilAvatar3ViewBinding
import com.hahalolo.messenger.databinding.MutilAvatar4ViewBinding
import com.hahalolo.messenger.databinding.MutilAvatarViewBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils

class MutilAvatarView : FrameLayout {
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

    private var binding: ViewDataBinding? = null

    private var requestManager: RequestManager? = null

    private var size: Int = 0

    private var imageList = mutableListOf<String>()

    fun setRequestManager(requestManager: RequestManager?) {
        this.requestManager = requestManager
    }

    private fun initView() {
        binding = MutilAvatarViewBinding.inflate(LayoutInflater.from(context))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
        val type = MeasureSpec.getMode(widthMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size, type),
            MeasureSpec.makeMeasureSpec(size, type)
        )
        if (size > 0 && this.size != size) {
            this.size = size
            updateSize()
        }
    }

    fun setImage(avatar: String) {
        this.imageList.clear()
        this.imageList.add(avatar)
        initLayout()
    }

    fun setImageList(imageList: MutableList<String>) {
        this.imageList = imageList
        initLayout()
    }

    private fun initLayout() {
        removeAllViews()
        binding = null
        if (imageList.isNotEmpty()) {
            when (val listSize = imageList.size) {
                1 -> {
                    binding = MutilAvatarViewBinding.inflate(LayoutInflater.from(context))
                }
                2 -> {
                    binding = MutilAvatar2ViewBinding.inflate(LayoutInflater.from(context))
                }
                3 -> {
                    binding = MutilAvatar3ViewBinding.inflate(LayoutInflater.from(context))
                }
                else -> {
                    binding = MutilAvatar4ViewBinding.inflate(LayoutInflater.from(context))
                    (binding as MutilAvatar4ViewBinding).countTv.visibility =
                        if (listSize > 4) View.VISIBLE else View.GONE
                    (binding as MutilAvatar4ViewBinding).countTv.text =
                        String.format("%s+", (listSize - 4))
                }
            }
        }
        updateSize()
        requestLoadImage()
        try {
            addView(binding?.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }catch (e : Exception){

        }
    }

    private fun updateSize() {
        when (binding) {
            is MutilAvatar2ViewBinding -> {
                updateSize((binding as MutilAvatar2ViewBinding).image1, size * 3 / 5)
                updateSize((binding as MutilAvatar2ViewBinding).image2, size * 4 / 5)
            }
            is MutilAvatar3ViewBinding -> {
                updateSize((binding as MutilAvatar3ViewBinding).image1, size * 3 / 5)
                updateSize((binding as MutilAvatar3ViewBinding).image2, size * 3 / 5)
                updateSize((binding as MutilAvatar3ViewBinding).image3, size * 3 / 5)
            }
            is MutilAvatar4ViewBinding -> {
                updateSize((binding as MutilAvatar4ViewBinding).image1, size * 3 / 5)
                updateSize((binding as MutilAvatar4ViewBinding).image2, size * 3 / 5)
                updateSize((binding as MutilAvatar4ViewBinding).image3, size * 3 / 5)
                updateSize((binding as MutilAvatar4ViewBinding).image4, size * 3 / 5)
                (binding as MutilAvatar4ViewBinding).countTv.takeIf { it.visibility == View.VISIBLE }
                    ?.textSize = size / 15f
            }
        }
    }

    private fun updateSize(view: View?, size: Int) {
        view?.layoutParams?.width = size
        view?.layoutParams?.height = size
    }

    private fun getPathAvatar(position: Int): String {
        return imageList.takeIf { it.isNotEmpty() && position >= 0 && position < it.size }
            ?.get(position) ?: ""
    }

    private fun requestLoadImage() {
        binding?.run {
            when (this) {
                is MutilAvatarViewBinding -> {
                    loadImage(this.image1, getPathAvatar(0))
                }
                is MutilAvatar2ViewBinding -> {
                    loadImage(this.image1, getPathAvatar(0))
                    loadImage(this.image2, getPathAvatar(1))
                }
                is MutilAvatar3ViewBinding -> {
                    loadImage(this.image1, getPathAvatar(0))
                    loadImage(this.image2, getPathAvatar(1))
                    loadImage(this.image3, getPathAvatar(2))
                }
                is MutilAvatar4ViewBinding -> {
                    loadImage(this.image1, getPathAvatar(0))
                    loadImage(this.image2, getPathAvatar(1))
                    loadImage(this.image3, getPathAvatar(2))
                    loadImage(this.image4, getPathAvatar(3))
                }
            }
        }
    }

    private fun loadImage(image: ImageView?, path: String?) {
        image?.let {
            requestManager?.run {
                GlideRequestBuilder.getCircleCropRequest(this)
                    .load(getThumb(path ?: ""))
                    .placeholder(R.drawable.community_avatar_holder)
                    .error(R.drawable.community_avatar_holder)
                    .into(image)
            }
        }
    }

    private fun getThumb(content: String): String? {
        return ThumbImageUtils.thumb(
            ThumbImageUtils.Size.AVATAR_NORMAL,
            content,
            ThumbImageUtils.TypeSize._1_1
        )
    }

    private fun invalidateImageView(requestManager: RequestManager?, imageView: ImageView?) {
        imageView?.run {
            requestManager?.clear(this)
            this.setImageDrawable(null)
        }
    }

    fun invalidateLayout(requestManager: RequestManager?) {
        when (binding) {
            is MutilAvatarViewBinding -> {
                invalidateImageView(requestManager, (binding as MutilAvatarViewBinding).image1)
            }
            is MutilAvatar2ViewBinding -> {
                invalidateImageView(requestManager, (binding as MutilAvatar2ViewBinding).image1)
                invalidateImageView(requestManager, (binding as MutilAvatar2ViewBinding).image2)
            }
            is MutilAvatar3ViewBinding -> {
                invalidateImageView(requestManager, (binding as MutilAvatar3ViewBinding).image1)
                invalidateImageView(requestManager, (binding as MutilAvatar3ViewBinding).image2)
                invalidateImageView(requestManager, (binding as MutilAvatar3ViewBinding).image3)
            }
            is MutilAvatar4ViewBinding -> {
                (binding as MutilAvatar4ViewBinding).countTv.text=""
                (binding as MutilAvatar4ViewBinding).countTv.visibility = View.GONE
                invalidateImageView(requestManager, (binding as MutilAvatar4ViewBinding).image1)
                invalidateImageView(requestManager, (binding as MutilAvatar4ViewBinding).image2)
                invalidateImageView(requestManager, (binding as MutilAvatar4ViewBinding).image3)
                invalidateImageView(requestManager, (binding as MutilAvatar4ViewBinding).image4)
            }
        }
    }

    fun targetView(): ImageView? {
        return (binding as? MutilAvatarViewBinding)?.image1
    }
}