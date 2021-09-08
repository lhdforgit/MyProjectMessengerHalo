package com.hahalolo.messager.presentation.message.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_DATE
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_FILE_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_FILE_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_GIF_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_GIF_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_IMAGE_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_IMAGE_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_LINK_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_LINK_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_NOTIFICATION
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REMOVED_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REMOVED_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REPLAY_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REPLAY_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_STICKER_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_STICKER_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TEXT_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TEXT_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TYPING_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TYPING_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_UNKNOWN
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_VIDEO_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_VIDEO_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.holder.*
import com.hahalolo.messager.presentation.message.adapter.holder.message.*
import com.halo.data.room.entity.MessageEntity
import com.halo.widget.sticky_header.StickyRecyclerHeadersAdapter

abstract class AbsChatMessageAdapter(
    val ownerId: String,
    val requestManager: RequestManager,
    val chatListener: ChatListener
) : RecyclerView.Adapter<ChatMessageViewHolder>(),
    StickyRecyclerHeadersAdapter<ChatMessageViewHolder>,
    ListPreloader.PreloadModelProvider<MessageEntity> {

    private val listData = mutableListOf<ChatMessageModel>()
    private var viewPreloadSizeProvider: ViewPreloadSizeProvider<MessageEntity> =
        ViewPreloadSizeProvider()

    fun getPreloadProvider(): ViewPreloadSizeProvider<MessageEntity> {
        return viewPreloadSizeProvider
    }

    fun submitList(list: MutableList<MessageEntity>) {
        val newList = mutableListOf<ChatMessageModel>()
        list.forEachIndexed { index, data ->
            val before = list.takeIf { (index - 1) >= 0 && (index - 1) < it.size }?.get(index - 1)
            val after = list.takeIf { (index + 1) >= 0 && (index + 1) < it.size }?.get(index + 1)
            newList.add(ChatMessageModel(data, before, after, ownerId)).apply {

            }
        }
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return listData.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return TextUtils.equals(
                    listData[oldItemPosition].id(),
                    newList[newItemPosition].id()
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listData[oldItemPosition].groupType == newList[newItemPosition].groupType
                        && this@AbsChatMessageAdapter.areContentsTheSame(
                    listData[oldItemPosition].data,
                    newList[newItemPosition].data
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listData.clear()
        this.listData.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    abstract fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean

    protected fun <B : ViewDataBinding> binding(parent: ViewGroup, layoutId: Int): B {
        return DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
    }

    abstract fun holderLayout(viewType: Int): Int?
    abstract fun emptyLayout(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val layoutId = holderLayout(viewType) ?: emptyLayout()
        val holder = when (viewType) {
            VIEW_TYPE_TEXT_INCOMING -> {
                ChatMessageItemViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_IMAGE_INCOMING -> {
                ChatMessageImageViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_VIDEO_INCOMING -> {
                ChatMessageVideoViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_STICKER_INCOMING -> {
                ChatMessageStickerViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_GIF_INCOMING -> {
                ChatMessageGifViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_LINK_INCOMING -> {
                ChatMessageLinkViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_FILE_INCOMING -> {
                ChatMessageFileViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_REMOVED_INCOMING -> {
                ChatMessageRemovedViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_REPLAY_INCOMING -> {
                ChatMessageReplayViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_TEXT_OUTCOMING -> {
                ChatMessageItemViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_IMAGE_OUTCOMING -> {
                ChatMessageImageViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_VIDEO_OUTCOMING -> {
                ChatMessageVideoViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_STICKER_OUTCOMING -> {
                ChatMessageStickerViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_GIF_OUTCOMING -> {
                ChatMessageGifViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_LINK_OUTCOMING -> {
                ChatMessageLinkViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_FILE_OUTCOMING -> {
                ChatMessageFileViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_REMOVED_OUTCOMING -> {
                ChatMessageRemovedViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_REPLAY_OUTCOMING -> {
                ChatMessageReplayViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_TYPING_INCOMING -> {
                ChatMessageTypingViewHolder.InComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_TYPING_OUTCOMING -> {
                ChatMessageTypingViewHolder.OutComing(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_DATE -> {
                ChatMessageDateViewHolder(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_NOTIFICATION -> {
                ChatMessageNotifyViewHolder(binding(parent, layoutId), chatListener)
            }
            VIEW_TYPE_UNKNOWN -> {
                ChatMessageUnknownViewHolder(binding(parent, layoutId), chatListener)
            }
            else -> {
                ChatMessageEmptyViewHolder(binding(parent, layoutId), chatListener)
            }
        }
        checkPreloadHolder(holder.viewTargets())
        return holder
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        getItem(position)?.run {
            holder.onBind(this)
        }
        checkPreloadHolder(holder.viewTargets())
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): ChatMessageViewHolder {
        val layoutId = holderLayout(VIEW_TYPE_DATE) ?: emptyLayout()
        return ChatMessageDateViewHolder(binding(parent, layoutId), chatListener)
    }

    override fun onBindHeaderViewHolder(holder: ChatMessageViewHolder, position: Int) {
        getItem(position)?.run {
            holder.onBind(this)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    protected fun getItem(position: Int): ChatMessageModel? {
        return listData.takeIf { position >= 0 && position < it.size }?.get(position)
    }

    /*HEADER DATE*/
    override fun getHeaderId(position: Int): Long {
        return getItem(position)?.headerTimeId() ?: -1
    }

    private fun checkPreloadHolder(viewTargets: MutableList<ImageView>?) {
        viewTargets?.firstOrNull()?.run {
            viewPreloadSizeProvider.setView(this)
        }
    }

    override fun getPreloadItems(position: Int): List<MessageEntity> {
        return try {
            val list: List<MessageEntity> = listData.map { it.data }
            list.takeIf { it.isNotEmpty() && position >= 0 && position < it.size }?.run {
                if (position + getCountPreload() <= list.size) {
                    list.subList(position, position + getCountPreload())
                } else {
                    list.subList(position, list.size)
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getCountPreload(): Int {
        return 1
    }

    override fun getPreloadRequestBuilder(item: MessageEntity): RequestBuilder<*>? {
        return requestManager.asDrawable().load(item)
    }
}