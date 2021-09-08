package com.hahalolo.messager.bubble.conversation.detail.gallery

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatAttachmentAdapter
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryDiffCallback
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.PreloadHolder
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BubbleGalleryLayoutBinding
import com.hahalolo.swipe.SwipeLayout
import com.halo.common.utils.ClipbroadUtils
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.list.ListUtils
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class BubbleGalleryLayout : AbsLifecycleView {

    private var binding: BubbleGalleryLayoutBinding? = null
    private var viewModel: BubbleGalleryViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null

    private var requestManager: RequestManager? = null

    private fun getRequestManager(): RequestManager {
        if (requestManager == null) return Glide.with(this)
        return requestManager!!
    }

    private val preloadSizeProvider = ViewPreloadSizeProvider<AttachmentTable>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    override fun initializeBinding() {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.bubble_gallery_layout, this, true)
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(BubbleGalleryViewModel::class) as? BubbleGalleryViewModel
    }

    override fun initializeLayout() {

    }

    fun startMediaShared(roomId: String) {

    }

    fun startLinkShared(roomId: String) {

    }

    fun startDocShared(roomId: String) {

    }
}