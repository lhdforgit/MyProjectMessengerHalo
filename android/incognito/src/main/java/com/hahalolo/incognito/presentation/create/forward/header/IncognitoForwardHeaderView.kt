package com.hahalolo.incognito.presentation.create.forward.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoForwardHeaderViewBinding
import com.hahalolo.incognito.presentation.create.forward.header.ForwardType.Companion.FILE_MULTI
import com.hahalolo.incognito.presentation.create.forward.header.ForwardType.Companion.FILE_UNIT
import com.hahalolo.incognito.presentation.create.forward.header.ForwardType.Companion.MEDIA
import com.hahalolo.incognito.presentation.create.forward.header.ForwardType.Companion.TEXT_IN
import com.hahalolo.incognito.presentation.create.forward.header.ForwardType.Companion.TEXT_OUT
import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoFileType
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ThumbImageUtils

class IncognitoForwardHeaderView : FrameLayout {

    private var binding: IncognitoForwardHeaderViewBinding? = null
    private var itemView: View? = null
    private val requestManager by lazy { Glide.with(this) }

    private var avatarView: ImageView? = null
    private var nameView: TextView? = null
    private var timeView: TextView? = null

    //TEXT_OUT, TEXT_IN
    private var contentMessage: TextView? = null

    //FILE_MULTI, MEDIA
    private var groupMediaMessage: ViewGroup? = null

    //FILE_UNIT
    private var fileGroupView: ViewGroup? = null
    private var fileIconView: ImageView? = null
    private var fileSizeView: TextView? = null
    private var fileTitleView: TextView? = null

    private var listImage = listOf(
        "https://media.hahalolo.com/2020/12/09/09/5c205932d577a7125c98425e201209095558G5_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ByPMDdluA8HZbkSt_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ZK75NiXC95a4R0DE_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/ELgJYpEWOaEk0q5c_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/Hw96vG2T0GsXHa4y_1080xauto_high.jpg",
        "https://media.hahalolo.com/5c205932d577a7125c98425e/t7HBgPJaiUvY5xKx_1080xauto_high.jpg"
    )

    private var listFile = listOf(1, 2, 3, 1, 2, 1)


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
            R.layout.incognito_forward_header_view,
            this,
            false
        )
        addView(binding?.root, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun setTypeForward(type: Int) {
        val layoutInflater = LayoutInflater.from(context)
        when (type) {
            TEXT_OUT -> {
                itemView =
                    layoutInflater.inflate(R.layout.incognito_forward_header_text_out_view, null)
            }
            TEXT_IN -> {
                itemView =
                    layoutInflater.inflate(R.layout.incognito_forward_header_text_in_view, null)
            }
            FILE_UNIT -> {
                itemView =
                    layoutInflater.inflate(R.layout.incognito_forward_header_file_view, null)
            }
            FILE_MULTI, MEDIA -> {
                itemView =
                    layoutInflater.inflate(R.layout.incognito_forward_header_media_view, null)
            }
            else -> {
                itemView = null
            }
        }
        itemView?.apply {
            nameView = findViewById(R.id.forward_user_name)
            avatarView = findViewById(R.id.forward_user_avatar)
            timeView = findViewById(R.id.forward_message_time)
            contentMessage = findViewById(R.id.forward_message_content)
            groupMediaMessage = findViewById(R.id.forward_message_media_group)
            fileIconView = findViewById(R.id.forward_message_file_icon)
            fileTitleView = findViewById(R.id.forward_message_file_title)
            fileSizeView = findViewById(R.id.forward_message_file_size)
            fileGroupView = findViewById(R.id.forward_message_file_container)
            updateLayout(type)
            binding?.containerLayout?.addView(this)
        }
    }

    private fun updateLayout(type: Int) {
        initInfoHeader()
        when (type) {
            TEXT_OUT, TEXT_IN -> {
                initTextMessage()
            }
            FILE_UNIT -> {
                initFileUnitMessage()
            }
            FILE_MULTI -> {
                initFileMultiMessage()
            }
            MEDIA -> {
                initMediaMessage()
            }
            else -> {

            }
        }
    }

    private fun initInfoHeader() {
        nameView?.text = "Hahalolo User"
        timeView?.text = "12:39"
        avatarView?.let { img ->
            GlideRequestBuilder
                .getCenterCropRequest(requestManager)
                .load(ThumbImageUtils.thumb("https://media.hahalolo.com/2021/01/07/08/53/770bdd5edbab7f042394ebb428deb9ca-1610009633_1080xauto_high.jpg"))
                .placeholder(R.drawable.ic_dummy_personal_circle)
                .into(img)
        }
    }

    private fun initMediaMessage() {
        listImage.forEachIndexed { index, url ->
            when {
                index < 3 -> {
                    val imageView = createImageView(url)
                    groupMediaMessage?.addView(imageView)
                }
                index == 3 -> {
                    val imageView = createLastImageView(url)
                    groupMediaMessage?.addView(imageView)
                }
                else -> {
                    return
                }
            }
        }
    }

    private fun initFileMultiMessage() {
        listFile.forEachIndexed { index, type ->
            when {
                index < 3 -> {
                    val imageView = createImageView(getDrawableFile(type).first)
                    groupMediaMessage?.addView(imageView)
                }
                index == 3 -> {
                    val imageView = createLastImageView(getDrawableFile(type).third)
                    groupMediaMessage?.addView(imageView)
                }
                else -> {
                    return
                }
            }
        }
    }

    private fun initFileUnitMessage() {
        fileTitleView?.text = "Báo cáo tiểu luận cuối kỳ.pdf"
        fileSizeView?.text = "169 KB"
        getDrawableFile(IncognitoFileType.XLS).let {
            fileIconView?.setImageResource(it.first)
            fileGroupView?.setBackgroundResource(it.second)
        }
    }

    private fun initTextMessage() {
        contentMessage?.text =
            "Hôm nay trời nhẹ lên cao tui buồn không biết vì sao tôi buồn ahihi ^^"
    }

    private fun getDrawableFile(type: Int): Triple<Int, Int, Int> {
        return when (type) {
            IncognitoFileType.PDF -> Triple(
                R.drawable.ic_incognito_manager_file_pdf,
                R.drawable.bg_incognito_file_pdf,
                R.drawable.ic_incognito_file_pdf_shadow
            )
            IncognitoFileType.XLS -> Triple(
                R.drawable.ic_incognito_manager_file_xls,
                R.drawable.bg_incognito_file_xls,
                R.drawable.ic_incognito_file_xls_shadow
            )
            IncognitoFileType.DOC -> Triple(
                R.drawable.ic_incognito_manager_file_doc,
                R.drawable.bg_incognito_file_doc,
                R.drawable.ic_incognito_file_doc_shadow
            )
            else -> Triple(
                R.drawable.ic_incognito_manager_file_doc,
                R.drawable.bg_incognito_file_doc,
                R.drawable.ic_incognito_file_doc_shadow
            )
        }
    }

    private fun createLastImageView(resource: Int): View {
        val layoutInflater = LayoutInflater.from(context)
        val lastItem =
            layoutInflater.inflate(R.layout.incognito_forward_header_media_last_view, null)
        val imageView = lastItem.findViewById<ShapeableImageView>(R.id.forward_last_media_img)
        val shadowView = lastItem.findViewById<CardView>(R.id.forward_last_shadow_view)
        val countTv = lastItem.findViewById<TextView>(R.id.forward_last_count_tv)
        shadowView.visibility = View.GONE
        imageView.setImageResource(resource)
        imageView.initMargin()
        return lastItem
    }

    private fun createLastImageView(url: String): View {
        val layoutInflater = LayoutInflater.from(context)
        val lastItem =
            layoutInflater.inflate(R.layout.incognito_forward_header_media_last_view, null)
        val imageView = lastItem.findViewById<ShapeableImageView>(R.id.forward_last_media_img)
        val shadowView = lastItem.findViewById<CardView>(R.id.forward_last_shadow_view)
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(url))
            .into(imageView)
        imageView.initShapeableImageView()
        imageView.initParams()
        shadowView.initParams()
        return lastItem
    }

    private fun createImageView(resource: Int): ImageView {
        val imageView = ImageView(context)
        imageView.initMargin()
        imageView.setImageResource(resource)
        return imageView
    }

    private fun createImageView(url: String): ImageView {
        val imageView = ShapeableImageView(context)
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(url))
            .into(imageView)
        imageView.initParams()
        imageView.initShapeableImageView()
        return imageView
    }

    private fun View.initParams() {
        val widthScreen = resources.displayMetrics.widthPixels
        val widthImgGr = widthScreen - SizeUtils.dp2px(150f)
        val widthImage = widthImgGr / 4
        val params = LayoutParams(widthImage, widthImage)
        val margin = SizeUtils.dp2px(4f)
        params.setMargins(0, 0, margin, 0)
        this.layoutParams = params
    }

    private fun View.initMargin() {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val margin = SizeUtils.dp2px(16f)
        params.setMargins(0, 0, margin, 0)
        this.layoutParams = params
    }

    private fun ShapeableImageView.initShapeableImageView() {
        this.shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCornerSizes(10f)
            .build()
    }
}

@IntDef(TEXT_OUT, TEXT_IN, FILE_UNIT, FILE_MULTI, MEDIA)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ForwardType {
    companion object {
        const val TEXT_OUT = 1
        const val TEXT_IN = 2
        const val FILE_UNIT = 3
        const val FILE_MULTI = 4
        const val MEDIA = 5
    }
}