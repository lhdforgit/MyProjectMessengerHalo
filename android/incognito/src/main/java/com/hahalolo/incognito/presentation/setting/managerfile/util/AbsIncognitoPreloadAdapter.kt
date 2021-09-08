package com.hahalolo.incognito.presentation.setting.managerfile.util

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider

abstract class AbsIncognitoPreloadAdapter<T : Any>(
    private val itemCallback: DiffUtil.ItemCallback<T>,
    protected var viewPreloadSizeProvider: ViewPreloadSizeProvider<T>?,
    protected var requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PreloadModelProvider<T> {

    val differ by lazy {
        AsyncPagingDataDiffer(
            diffCallback = itemCallback,
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

    fun getItem(position: Int): T? =
        differ.takeIf { it.itemCount > position }?.getItem(position)

    fun currentList(): List<T> {
        return differ.snapshot().items
    }

    suspend fun submitData(pagingData: PagingData<T>) {
        differ.submitData(pagingData)
    }

    fun refresh() {
        differ.refresh()
    }

    override fun getItemCount(): Int {
        return differ.itemCount
    }

    protected fun checkPreloadHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is IncognitoPreloadHolder) {
            (viewHolder as IncognitoPreloadHolder).getTargets().firstOrNull()?.run {
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
}