package com.hahalolo.messager.presentation.main.contacts.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.ViewGroup
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.holder.EmptyUndefineErrorHolder
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.hahalolo.messager.presentation.main.contacts.ChatContactsListener
import com.hahalolo.messenger.R
import com.halo.data.entities.contact.Contact

class ChatContactsAdapter(
    private val info: Boolean,
    private val requestManager: RequestManager,
    private val listener: ChatContactsListener,
    private val preloadSizeProvider: ViewPreloadSizeProvider<Contact>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ContactStateListener {

    val ITEM_COMPARATOR =
        object : DiffUtil.ItemCallback<Contact>() {

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Contact,
                newItem: Contact
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: Contact,
                newItem: Contact
            ): Boolean {
                return oldItem.contactId == newItem.contactId
            }
        }

     val differ by lazy {
        AsyncPagingDataDiffer(
            diffCallback = ITEM_COMPARATOR,
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {
                    notifyItemRangeInserted(position, count)
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyItemRangeRemoved(position, count)
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }

                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    notifyItemRangeChanged(position, count, payload)
                }
            }
        )
    }

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.removeLoadStateListener(listener)
    }

    suspend fun submitData(pagingData: PagingData<Contact>) {
        differ.submitData(pagingData)
    }

    override fun retry() {
        differ.retry()
    }

    fun refresh() {
        differ.refresh()
    }

    fun getItem(position: Int): Contact? =
        differ.takeIf { it.itemCount > position }?.getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        if (viewType == R.layout.chat_contact_item) {
            viewHolder = ChatContactsViewHolder.getHolder(info, parent, requestManager, listener)
            checkPreloadHolder(viewHolder)
        } else {
            viewHolder = EmptyUndefineErrorHolder.createHolder(parent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatContactsViewHolder) {
            getItem(position)?.run {
                holder.bind(this)
                checkPreloadHolder(holder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.takeIf {
            !TextUtils.isEmpty(it.fullName)
        }?.run {
            R.layout.chat_contact_item
        } ?: kotlin.run {
            R.layout.chat_empty_holder_item
        }
    }

    override fun getItemCount(): Int {
        return differ.itemCount
    }

    fun withLoadStateFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter{
        differ.addLoadStateListener { loadState ->
            footer.loadState = loadState.append
        }
        return ConcatAdapter(this, footer)
    }

    private fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is PreloadHolder) {
            (viewHolder as PreloadHolder).getTargets().firstOrNull()?.run {
                preloadSizeProvider.setView(this)
            }
        }
    }

}

interface ContactStateListener {
    fun retry()
}