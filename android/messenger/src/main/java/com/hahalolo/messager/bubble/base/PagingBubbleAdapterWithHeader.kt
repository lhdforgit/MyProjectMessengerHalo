package com.hahalolo.messager.bubble.base

import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Create by ndn
 * Create on 8/26/20
 * com.halo.presentation.feed.adapter
 */
abstract class PagingBubbleAdapterWithHeader<T : Any, VH : RecyclerView.ViewHolder>(
    private val diffCallback: DiffUtil.ItemCallback<T>,
    private val headerOffset: Int = 1
) : RecyclerView.Adapter<VH>() {

    val differ by lazy {
        AsyncPagingDataDiffer(
            diffCallback = diffCallback,
            updateCallback = OffsetListUpdateCallback(this, headerOffset)
        )
    }

    /**
     * Note: [getItemId] is final, because stable IDs are unnecessary and therefore unsupported.
     *
     * [PagingDataAdapter]'s async diffing means that efficient change animations are handled for
     * you, without the performance drawbacks of [RecyclerView.Adapter.notifyDataSetChanged].
     * Instead, the diffCallback parameter of the [PagingDataAdapter] serves the same
     * functionality - informing the adapter and [RecyclerView] how items are changed and moved.
     */
    final override fun getItemId(position: Int): Long {
        return super.getItemId(getPositionItemWithOffset(position))
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