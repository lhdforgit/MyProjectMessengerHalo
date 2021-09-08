/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view.media

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMutilImageViewBinding
import com.halo.data.entities.attachment.Attachment
import com.halo.widget.reactions.MessageMenuListener
import com.halo.widget.reactions.ReactionClickListener
import com.halo.widget.reactions.ReactionSelectedListener
import com.halo.widget.reactions.message.MessageReactionPopup
import com.halo.widget.reactions.message.MessageReactionUtils


class ChatMutilImageView : RelativeLayout {

    private lateinit var binding: ChatMutilImageViewBinding
    private var requestManager: RequestManager? = null

    private var listMedia = mutableListOf<AttachmentTable>()
    private var listener: MutilMediaListener? = null

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

    fun updateRequestManager(requestManager: RequestManager?) {
        this.requestManager = requestManager
        binding.image1.requestManager = requestManager
        binding.image2.requestManager = requestManager
        binding.image3.requestManager = requestManager
        binding.image4.requestManager = requestManager
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val type = MeasureSpec.getMode(widthMeasureSpec)
        val height: Int
        when (listMedia.size) {
            0 -> {
                height = width
            }
            1 -> {
                val widthSource = listMedia[0].width
                val heightSource = listMedia[0].height
                if (widthSource > 0) {
                    val maxHeight: Int
                    if (widthSource > heightSource) {
                        maxHeight = width * 9 / 16
                        height = Math.max(heightSource * width / widthSource, maxHeight)
                    } else {
                        maxHeight = width * 16 / 9
                        height = Math.min(heightSource * width / widthSource, maxHeight)
                    }
                } else {
                    height = width
                }
            }
            2 -> {
                height = width / 2
            }
            else -> {
                height = width
            }
        }
        setMeasuredDimension(width, height)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, type),
            MeasureSpec.makeMeasureSpec(height, type)
        )
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.chat_mutil_image_view, this, false
        )
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }




    fun updateListMedia(listMedia: MutableList<AttachmentTable>, listener: MutilMediaListener?) {
        this.listMedia = listMedia
        this.listener = listener
        requestUpdateLayout()
    }

    private fun requestUpdateLayout() {
        listMedia.takeIf { it.isNotEmpty() }?.run {
            val size = this.size
            binding.groupLayout1.visibility = if (size >= 1) View.VISIBLE else View.GONE
            binding.groupLayout2.visibility = if (size >= 2) View.VISIBLE else View.GONE
            binding.image1.visibility = if (size >= 1) View.VISIBLE else View.GONE
            binding.image2.visibility = if (size >= 2) View.VISIBLE else View.GONE
            binding.image3Layout.visibility = if (size >= 3) View.VISIBLE else View.GONE
            binding.image4.visibility = if (size >= 4) View.VISIBLE else View.GONE
            binding.countTv.visibility = if (size > 4) View.VISIBLE else View.GONE

            onloadImageResource(if (size >= 1) this.get(0) else null, binding.image1)
            onloadImageResource(if (size >= 2) this.get(1) else null, binding.image2)
            onloadImageResource(if (size >= 3) this.get(2) else null, binding.image3)
            onloadImageResource(if (size >= 4) this.get(3) else null, binding.image4)
            binding.countTv.text = String.format("+%s", size - 4)

            bindAction()
        }
    }

    private fun bindAction() {
        binding.image1.setOnClickListener { listener?.onClickViewMedia(0, listMedia) }
        binding.image2.setOnClickListener { listener?.onClickViewMedia(1, listMedia) }
        binding.image3.setOnClickListener { listener?.onClickViewMedia(2, listMedia) }
        binding.image4.setOnClickListener { listener?.onClickViewMedia(3, listMedia) }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun disableReaction() {
        binding.image1.setOnTouchListener(null)
        binding.image2.setOnTouchListener(null)
        binding.image3.setOnTouchListener(null)
        binding.image4.setOnTouchListener(null)

        binding.image1.setOnLongClickListener(null)
        binding.image2.setOnLongClickListener(null)
        binding.image3.setOnLongClickListener(null)
        binding.image4.setOnLongClickListener(null)
    }

    val reactionPopup1 :  MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(context)
    }
    val reactionPopup2 :  MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(context)
    }
    val reactionPopup3 :  MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(context)
    }
    val reactionPopup4 :  MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(context)
    }

    fun bindReaction(
        reactionSelectedListener: ReactionSelectedListener,
        reactionClickListener: ReactionClickListener,
        messageMenuListener: MessageMenuListener,
    ) {
        if (listMedia.size >= 0) {
            reactionPopup1.updateListener(binding.image1, reactionSelectedListener, reactionClickListener, messageMenuListener)
            if (listMedia.size >= 1) {
                reactionPopup2.updateListener(binding.image2, reactionSelectedListener, reactionClickListener, messageMenuListener)

                if (listMedia.size >= 2) {
                    reactionPopup3.updateListener(binding.image3, reactionSelectedListener, reactionClickListener, messageMenuListener)
                    if (listMedia.size >= 3) {
                        reactionPopup4.updateListener(binding.image4, reactionSelectedListener, reactionClickListener, messageMenuListener)
                    }
                }
            }
        }
    }


    private fun onloadImageResource(item: AttachmentTable?, imageView: ChatImageView) {
        imageView.updateMedia(item)
    }

    fun getTargets(): MutableList<ImageView> {
        val result = mutableListOf<ImageView>()
        result.addAll(binding.image1.getTarget())
        return result
    }

    fun invalidateLayout(requestManager: RequestManager?) {
        binding.image1.invalidateLayout(requestManager)
        binding.image2.invalidateLayout(requestManager)
        binding.image3.invalidateLayout(requestManager)
        binding.image4.invalidateLayout(requestManager)
    }

    fun updateListMedia(listMedia: MutableList<Attachment>) {
        val size = listMedia.size
        binding.groupLayout1.visibility = if (size >= 1) View.VISIBLE else View.GONE
        binding.groupLayout2.visibility = if (size >= 2) View.VISIBLE else View.GONE
        binding.image1.visibility = if (size >= 1) View.VISIBLE else View.GONE
        binding.image2.visibility = if (size >= 2) View.VISIBLE else View.GONE
        binding.image3Layout.visibility = if (size >= 3) View.VISIBLE else View.GONE
        binding.image4.visibility = if (size >= 4) View.VISIBLE else View.GONE
        binding.countTv.visibility = if (size > 4) View.VISIBLE else View.GONE

        onloadImageResource(if (size >= 1) listMedia.get(0) else null, binding.image1)
        onloadImageResource(if (size >= 2) listMedia.get(1) else null, binding.image2)
        onloadImageResource(if (size >= 3) listMedia.get(2) else null, binding.image3)
        onloadImageResource(if (size >= 4) listMedia.get(3) else null, binding.image4)
        binding.countTv.text = String.format("+%s", size - 4)

    }

    private fun onloadImageResource(item: Attachment?, imageView: ChatImageView) {
        imageView.updateMedia(item)
    }
}