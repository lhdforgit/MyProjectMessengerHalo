package com.hahalolo.messager.chatkit.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.google.common.collect.Iterators
import com.hahalolo.messager.chatkit.adapter.ChatMessagePagedAdapter.Status.Companion.FAILED
import com.hahalolo.messager.chatkit.adapter.ChatMessagePagedAdapter.Status.Companion.RUNNING
import com.hahalolo.messager.chatkit.adapter.ChatMessagePagedAdapter.Status.Companion.SUCCESS
import com.hahalolo.messager.chatkit.adapter.diff.ChatMessageDiffCallback
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatHolderItem
import com.hahalolo.messager.chatkit.holder.ChatMessageHolders
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.holder.create_group.ChatCreateGroupHolderItem
import com.hahalolo.messager.chatkit.holder.date.ChatDateHolderItem
import com.hahalolo.messager.chatkit.holder.status.ChatNetworkStatusItem
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.hahalolo.messager.chatkit.utils.DateFormatter
import com.halo.data.room.entity.MessageEntity
import com.halo.widget.sticky_header.StickyRecyclerHeadersAdapter
import java.util.*
import kotlin.collections.HashMap

class ChatMessagePagedAdapter(
    val ownerId: String,
    val chatListener: ChatListener,
    val imageLoader: ImageLoader,
    private val viewPreloadSizeProvider: ViewPreloadSizeProvider<MessageEntity>,
    private val diffCallback: DiffUtil.ItemCallback<MessageEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyRecyclerHeadersAdapter<ChatDateHolderItem>,
    ListPreloader.PreloadModelProvider<MessageEntity> {

    private var dateHeadersFormatter: DateFormatter.Formatter? = null
    private val chatMessageHolders = ChatMessageHolders(chatListener, imageLoader)
    private var messagesListStyle: MessagesListStyle? = null

    private var seenMessageListener: SeenMessageListener? = null

    private var networkStatus: Int? = null

    private var lists = mutableListOf<MessageEntity>()

    private val groupTypes = HashMap<String, MessageGroupType>()

    init {

    }

    fun submitList(list: MutableList<MessageEntity>) {
        val diffCallback = ChatMessageDiffCallback(this.lists, list, diffCallback)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.lists.clear()
        this.lists.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setSeenMessageSListener(seenMessageListener: SeenMessageListener?) {
        this.seenMessageListener = seenMessageListener
    }

    private fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ChatHolderItem<*>) {
            val listTarget = viewHolder.getTargets()
            listTarget.firstOrNull()?.run {
                viewPreloadSizeProvider.setView(this)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (hasExtraRow() && position == itemCount - 1) {
            return ChatMessageHolders.VIEW_TYPE_NETWORK_STATUS
        }
        return chatMessageHolders.getViewType(getItem(position), ownerId)
    }

    fun setStyle(style: MessagesListStyle?) {
        messagesListStyle = style
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ChatMessageHolders.VIEW_TYPE_NETWORK_STATUS) {
            return ChatNetworkStatusItem.create(parent)
        }
        val viewHolder = chatMessageHolders.getHolder(parent, viewType, messagesListStyle!!)
        checkPreloadHolder(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is ChatNetworkStatusItem -> {
                holder.bindStatus()
            }
            is ChatCreateGroupHolderItem -> {
                holder.onBind()
            }
            else -> {
//                getItem(position)?.run {
//                    if (!this.isSeenMessage(ownerId) && position==0 ) {
//                        seenMessageListener?.onSeenMessage(this.messageId(), this.messageTime())
//                    }
//                    chatMessageHolders.onBind(holder, this)
//                }
                onBindHolderGroupType(holder, position)
                checkPreloadHolder(holder)
            }
        }
    }

    private fun onBindHolderGroupType(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.run {
            val groupType = groupTypes.get(this.messageId()) ?: run {
                val befor = getItem(position - 1)
                val after = getItem(position + 1)
                val groupType = chatMessageHolders.getHolderGroupType(befor, this, after, holder)
                groupTypes[this.messageId()] = groupType
                groupType
            }
            if (holder is ChatHolderItem<*>) {
                holder.onMessageGroupType(groupType)
            }
        }
    }

    private fun getItem(position: Int): MessageEntity? {
        return lists.takeIf { position >= 0 && position < it.size }?.get(position)
    }

    override fun getItemCount(): Int {
        return lists.size + if (hasExtraRow()) 1 else 0
    }

    override fun getHeaderId(position: Int): Long {
        getItem(position)?.messageTime()?.run {
            val calender = Calendar.getInstance()
            calender.timeInMillis = this
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val month = calender.get(Calendar.MONTH)
            val year = calender.get(Calendar.YEAR)
            val id: Long = (day * 1000000 + month * 10000 + year).toLong()
            return id
        } ?: return -1
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): ChatDateHolderItem? {
        return ChatDateHolderItem.create(parent, chatListener, messagesListStyle!!)
    }

    override fun onBindHeaderViewHolder(holder: ChatDateHolderItem?, position: Int) {
        getItem(position)?.messageTime()?.run {
            holder?.onBind(Date(this))
        }
    }

    fun hasExtraRow(): Boolean {
        return networkStatus != null && networkStatus != SUCCESS && networkStatus != FAILED
    }

    fun updateStatus(newNetworkState: Int?) {
        val previousState = this.networkStatus
        val hadExtraRow = hasExtraRow()
        this.networkStatus = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(lists.size)
            } else {
                notifyItemInserted(lists.size)
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(lists.size)
        }
    }

    fun updateItem(item: MessageEntity?) {
        val index = Iterators.indexOf(lists.iterator()) {
            TextUtils.equals(item?.messageId(), it?.messageId())
        }
        if (index >= 0) {
            groupTypes.remove(item?.messageId())
            notifyItemChanged(index)
        }
    }

    fun updateItem(position: Int) {
        if (position >= 0 && position < lists.size) {
            updateItem(lists[position])
        }
    }

    fun isEmptyList(): Boolean {
        return lists.isEmpty()
    }

    override fun getPreloadItems(position: Int): List<MessageEntity> {
        return try {
            val list: List<MessageEntity> = lists
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
        return imageLoader.getRequestManager().asDrawable().load(item)
    }

    @IntDef(RUNNING, SUCCESS, FAILED)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Status {
        companion object {
            const val RUNNING = 0
            const val SUCCESS = 1
            const val FAILED = 2
        }
    }

}