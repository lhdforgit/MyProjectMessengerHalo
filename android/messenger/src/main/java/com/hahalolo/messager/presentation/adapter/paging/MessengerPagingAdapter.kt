package com.hahalolo.messager.presentation.adapter.paging

import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.utils.Strings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

abstract class MessengerPagingAdapter<T : Any>(
    private val itemCallback: DiffUtil.ItemCallback<T>,
    private var viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?,
    private var requestManager: RequestManager,
    private val headerOffset: Int = 0
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PreloadModelProvider<T> {

    val differ by lazy {
        AsyncPagingDataDiffer(
            diffCallback = itemCallback,
            updateCallback = OffsetListUpdateCallback(this, headerOffset)
        )
    }

    /***  PagingDataAdapter  ***/

    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    fun submitData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        differ.submitData(lifecycle, pagingData)
    }

    fun retry() {
        differ.retry()
    }

    fun refresh() {
        differ.refresh()
    }

    /**
     * Returns a new [ItemSnapshotList] representing the currently presented items, including any
     * placeholders if they are enabled.
     */
    fun snapshot(): ItemSnapshotList<T> = differ.snapshot()

    @OptIn(FlowPreview::class)
    val loadStateFlow: Flow<CombinedLoadStates> = differ.loadStateFlow

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.removeLoadStateListener(listener)
    }

    fun withLoadStateHeader(
        header: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
        }
        return ConcatAdapter(header, this)
    }

    fun withLoadStateFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(this, footer)
    }

    fun withLoadStateHeaderAndFooter(
        header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(header, this, footer)
    }

    /***  End PagingDataAdapter  ***/

    protected fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is MessengerPreloadHolder) {
            (viewHolder as MessengerPreloadHolder).getTargets().firstOrNull()?.run {
                viewPreloadSizeProvider?.setView(this)
            }
        }
    }

    override fun getPreloadItems(position: Int): MutableList<T> {
        return try {
            val list = differ.snapshot().items.toMutableList()
            if (list.isNotEmpty() && position >= 0 && position < list.size) {
                if (position + getCountPreload() <= list.size) {
                    list.subList(position, position + getCountPreload())
                } else {
                    list.subList(position, list.size)
                }
            } else mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    protected open fun getCountPreload(): Int {
        return -1
    }

    override fun getPreloadRequestBuilder(item: T): RequestBuilder<*>? {
        return requestManager.asDrawable().load(item)
    }

    fun getPositionItemWithOffset(position: Int): Int {
        return position - headerOffset
    }

    private fun getItemCountWithOffset(): Int {
        return differ.itemCount + headerOffset
    }

    override fun getItemCount() = getItemCountWithOffset()

    fun getItem(position: Int) = differ.getItem(getPositionItemWithOffset(position))

    private class OffsetListUpdateCallback(
        private val adapter: RecyclerView.Adapter<*>,
        private val offset: Int
    ) : ListUpdateCallback {

        fun offsetPosition(originalPosition: Int): Int {
            return originalPosition + offset
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeInserted(offsetPosition(position), count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyItemRangeRemoved(offsetPosition(position), count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyItemMoved(offsetPosition(fromPosition), offsetPosition(toPosition))
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(offsetPosition(position), count, payload)
        }
    }
}