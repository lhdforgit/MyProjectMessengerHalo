package com.hahalolo.messager.presentation.group.gallery.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.PreloadHolder
import com.hahalolo.messager.presentation.adapter.paging.MessengerPagingAdapter
import com.hahalolo.messager.presentation.adapter.paging.MessengerPreloadHolder
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageGalleryDocItemBinding
import com.hahalolo.messenger.databinding.ChatMessageGalleryLinkItemBinding
import com.hahalolo.messenger.databinding.ChatMessageGalleryMediaItemBinding
import com.halo.common.utils.DateUtils
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ThumbImageUtils
import com.halo.data.room.table.AttachmentTable
import com.halo.widget.ErrorHideEmptyHolder
import java.util.*

class MessengerManagerFileAdapter(
    private val diffCallback: ChatRoomGalleryDiffCallback,
    private val preloadSizeProvider: ViewPreloadSizeProvider<MessengerManagerFileModel>,
    private val requestManager: RequestManager,
    private val listener: AttachmentListener
) : MessengerPagingAdapter<MessengerManagerFileModel>(
    diffCallback,
    preloadSizeProvider,
    requestManager
) {
    companion object {
        const val IMAGE_ITEM_VIEW = 111
        const val DOCUMENT_ITEM_VIEW = 112
        const val LINK_ITEM_VIEW = 113
        const val ERROR_ITEM_VIEW = 114
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        when (viewType) {
            IMAGE_ITEM_VIEW -> holder = MessengerMediaViewHolder.createHolder(
                parent,
                requestManager,
                listener
            )
            DOCUMENT_ITEM_VIEW -> {
                holder = MessengerDocumentViewHolder.createHolder(parent, requestManager, listener)
            }
            LINK_ITEM_VIEW ->{
                holder = MessengerLinkViewHolder.createHolder(parent, requestManager, listener)
            }
            else -> holder = ErrorHideEmptyHolder.build(parent)
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessengerMediaViewHolder -> {
                holder.bind(getItem(position))
            }
            is MessengerDocumentViewHolder -> {
                holder.bind(getItem(position))
            }
            is MessengerLinkViewHolder ->{
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
        checkPreloadHolder(holder)
    }

    override fun getCountPreload(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let { item ->
            if (TextUtils.isEmpty(item.url)) {
                return ERROR_ITEM_VIEW
            } else {
                when (item.typeFile) {
                    ManagerFileType.IMAGE_TYPE -> {
                        return IMAGE_ITEM_VIEW
                    }
                    ManagerFileType.DOCUMENT_TYPE -> {
                        return DOCUMENT_ITEM_VIEW
                    }
                    ManagerFileType.LINK_TYPE -> {
                        return LINK_ITEM_VIEW
                    }
                    ManagerFileType.VIDEO_TYPE -> {
                        //Todo
                        return ERROR_ITEM_VIEW
                    }
                    ManagerFileType.OTHER_TYPE -> {
                        //Todo
                        return ERROR_ITEM_VIEW
                    }
                    else -> {
                        return ERROR_ITEM_VIEW
                    }
                }
            }
        } ?: kotlin.run {
            return ERROR_ITEM_VIEW
        }
    }
}

class MessengerMediaViewHolder(
    val binding: ChatMessageGalleryMediaItemBinding,
    val requestManager: RequestManager,
    private val listener: AttachmentListener,
    private val withParent: Int
) : RecyclerView.ViewHolder(binding.root), MessengerPreloadHolder {

    fun bind(mediaModel: MessengerManagerFileModel?) {
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(mediaModel?.url))
            .into(binding.photoIv)
        updateMediaSize()
        binding.root.setOnClickListener { listener.onClickAttachment(0, mediaModel) }
    }

    private fun updateMediaSize() {
        val with = withParent - SizeUtils.dp2px(0f)
        itemView.layoutParams.width = with / 3
        itemView.layoutParams.height = with / 3
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: AttachmentListener
        ): MessengerMediaViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ChatMessageGalleryMediaItemBinding.inflate(layoutInflater, parent, false)
            return MessengerMediaViewHolder(
                binding,
                requestManager,
                listener,
                parent.measuredWidth
            )
        }
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.photoIv)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.photoIv)
        binding.photoIv.setImageDrawable(null)
    }
}


class MessengerDocumentViewHolder(
    val binding: ChatMessageGalleryDocItemBinding,
    val requestManager: RequestManager,
    val listener: AttachmentListener
) : RecyclerView.ViewHolder(binding.root), MessengerPreloadHolder {

    fun bind(att: MessengerManagerFileModel?) {
        att?.run {
            binding.fileNameTv.text = this.name
            //binding.dateTv.text = DateUtils.formatTime(sentAt ?: 0)
            //binding.fileSizeTv.text = this.getFileSizeText()
            var typeFileRes = R.drawable.ic_chat_file_doc
//            getDocumentType().let { type ->
//                when (type) {
//                    DocumentType.PDF -> typeFileRes = R.drawable.ic_chat_file_pdf
//                    DocumentType.EXCEl -> typeFileRes = R.drawable.ic_chat_file_excel
//                }
//            }
            binding.fileImg.setImageResource(typeFileRes)
        }
        bindAction(att)
    }

    private fun bindAction(att: MessengerManagerFileModel?) {
        binding.menuBt.setOnClickListener {listener.onClickMenu(binding.menuBt, att)}
        binding.root.setOnLongClickListener {
            listener.onClickMenu(binding.fileImg, att)
            return@setOnLongClickListener true
        }
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.fileImg)
        binding.fileImg.setImageDrawable(null)
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.fileImg)
        return views
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: AttachmentListener
        ): MessengerDocumentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ChatMessageGalleryDocItemBinding.inflate(layoutInflater, parent, false)
            return MessengerDocumentViewHolder(
                binding,
                requestManager,
                listener
            )
        }
    }
}

class MessengerLinkViewHolder constructor(
    val binding: ChatMessageGalleryLinkItemBinding,
    val requestManager: RequestManager,
    val listener: AttachmentListener
) : RecyclerView.ViewHolder(binding.root), PreloadHolder {

    fun bind(att: MessengerManagerFileModel?) {
        att?.run {
            binding.urlTv.text = url
            this.timeSend?.let {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                binding.timeTv.text = DateUtils.formatTime(it)
            } ?: run {
                binding.timeTv.visibility = View.GONE
            }
        }
        bindAction(att)
    }

    private fun bindAction(att: MessengerManagerFileModel?) {
        binding.menuBt.setOnClickListener { listener.onClickMenu(binding.urlTv, att) }
        binding.root.setOnLongClickListener {
            listener.onClickAttachment(0, att)
            return@setOnLongClickListener true
        }
        binding.urlTv.setOnClickListener { listener.onClickAttachment(0, att)}
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.thumbnailImg)
        binding.thumbnailImg.setImageDrawable(null)
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.thumbnailImg)
        return views
    }

    companion object {
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: AttachmentListener
        ): MessengerLinkViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageGalleryLinkItemBinding>(
                inflater,
                R.layout.chat_message_gallery_link_item,
                parent, false
            )
            return MessengerLinkViewHolder(
                binding,
                requestManager,
                listener
            )
        }
    }
}


class ChatRoomGalleryDiffCallback : DiffUtil.ItemCallback<MessengerManagerFileModel>() {
    override fun areItemsTheSame(
        oldItem: MessengerManagerFileModel,
        newItem: MessengerManagerFileModel
    ): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
    }

    override fun areContentsTheSame(
        oldItem: MessengerManagerFileModel,
        newItem: MessengerManagerFileModel
    ): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id) && TextUtils.equals(
            oldItem.url,
            newItem.url
        )
    }
}