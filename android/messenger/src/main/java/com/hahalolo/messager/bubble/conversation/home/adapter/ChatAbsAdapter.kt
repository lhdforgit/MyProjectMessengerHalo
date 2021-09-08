/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.recyclerview.widget.*
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.home.adapter.holder.MessageEmptyUndefineErrorHolder
import com.hahalolo.messager.bubble.conversation.home.adapter.holder.MessageNetworkStateRetryHolder
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateListener
import com.hahalolo.messenger.R
import com.hahalolo.player.exoplayer.HaloPlayer
import com.halo.data.common.paging.NetworkState

/**
 * @author ndn
 * Created by ndn
 * Created on 9/24/18
 *
 *
 *
 *
 * Add items above the PagedListAdapter, switch to using AsyncPagedListDiffer,
 * and pass a custom ItemCallback that offsets the positions by one:
 *
 *
 * private final AsyncPagedListDiffer<T> mDiffer;
 * private final AdapterListUpdateCallback adapterCallback = new AdapterListUpdateCallback(this);
 *
 *
 * AsyncDifferConfig<T> config = new AsyncDifferConfig.Builder<>(diffCallback).build();
 * ListUpdateCallback callback = new AsyncPagedListDiffer<>(new ListUpdateCallback() {
 * //    @Override public void onInserted(int position, int count) {
 * //        adapterCallback.onInserted(position + 1, count);
 * //    }
 *
 *
 * //    @Override public void onRemoved(int position, int count) {
 * //        adapterCallback.onRemoved(position + 1, count);
 * //    }
 *
 *
 * //    @Override public void onMoved(int fromPosition, int toPosition) {
 * //       adapterCallback.onMoved(fromPosition + 1, toPosition + 1);
 * //    }
 *
 *
 * //    @Override public void onChanged(int position, int count, Object payload) {
 * //    adapterCallback.onChanged(position + 1, count, payload);
 * //    }
 * //  }, config);
 *
 *
 * Link Github: {@see https://github.com/googlesamples/android-architecture-components/issues/375}
 */
abstract class ChatAbsAdapter<T : Any>
constructor(
    val diffCallback: DiffUtil.ItemCallback<T>,
    val requestManager: RequestManager,
    open val listener: ChatAdapterListener<T>,
    val preloadSizeProvider: ViewPreloadSizeProvider<T>?,
    val networkStateListener: NetworkStateListener,
    protected var haveHeader: Boolean = true,
    protected val headerCount: Int = ITEMS_ABOVE_PAGED_LIST_ADAPTER_COUNT,
    val player: HaloPlayer? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ChatBaseAdapter<T>, ListPreloader.PreloadModelProvider<T> {

    // Override Paged List for fix bug add header with {@link android.arch.paging.PagedListAdapter}
    var differ: AsyncPagedListDiffer<T>

    private var networkState: NetworkState? = null

    val adapterCallback by lazy { AdapterListUpdateCallback(this) }

    init {
        val config = AsyncDifferConfig.Builder(diffCallback).build()
        val callback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                adapterCallback.onInserted(getAdapterCallbackPosition(position), count)
            }

            override fun onRemoved(position: Int, count: Int) {
                adapterCallback.onRemoved(getAdapterCallbackPosition(position), count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                adapterCallback.onMoved(
                    getAdapterCallbackPosition(fromPosition),
                    getAdapterCallbackPosition(toPosition)
                )
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                adapterCallback.onChanged(getAdapterCallbackPosition(position), count, payload)
            }
        }
        differ = AsyncPagedListDiffer(callback, config)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.message_network_state_retry_item -> MessageNetworkStateRetryHolder.createHolder(
                parent,
                networkStateListener
            )
            R.layout.message_empty_undefine_error_item -> MessageEmptyUndefineErrorHolder.createHolder(
                parent
            )
            getHeaderViewType() -> if (haveHeader) onCreateHeaderHolder(parent)
            else MessageEmptyUndefineErrorHolder.createHolder(
                parent
            )
            else -> onCreateChatHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageNetworkStateRetryHolder -> holder.bind(networkState)
            is MessageEmptyUndefineErrorHolder -> holder.bind()
            else -> {
                if (haveHeader && position == HEADER_POSITION) {
                    onBindHeaderHolder(holder)
                } else {
                    differ.takeIf { getPositionItemWithHeader(position) in 0 until it.itemCount }
                        ?.run {
                            getItem(getPositionItemWithHeader(position))?.run {
                                onBindChatHolder(holder, this)
                            }
                        }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return getItemCountWithHeader() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.message_network_state_retry_item
        } else if (haveHeader && position == HEADER_POSITION) {
            getHeaderViewType()
        } else getChatViewType(getPositionItemWithHeader(position))
    }

    fun getAdapterCallbackPosition(position: Int): Int {
        return position + if (haveHeader) headerCount else 0
    }

    fun getPositionItemWithHeader(position: Int): Int {
        return position - if (haveHeader) headerCount else 0
    }

    private fun getItemCountWithHeader(): Int {
        return differ.itemCount + if (haveHeader) headerCount else 0
    }

    override fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    fun getNetworkState(): NetworkState? {
        return networkState
    }

    override fun getPreloadItems(position: Int): List<T> {
        return try {
            differ.takeIf { getPositionItemWithHeader(position) + 1 in 0 until it.itemCount }
                ?.run {
                    getItem(getPositionItemWithHeader(position) + 1)?.run {
                        return listOf(this)
                    }
                }
                ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getPreloadRequestBuilder(item: T): RequestBuilder<*>? {
        return requestManager.asDrawable().load(item)
    }

    companion object {
        const val HEADER_POSITION = 0
        const val ITEMS_ABOVE_PAGED_LIST_ADAPTER_COUNT = 1
    }
}