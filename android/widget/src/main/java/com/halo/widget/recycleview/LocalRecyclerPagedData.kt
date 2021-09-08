package com.halo.widget.recycleview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halo.common.utils.JobUtil
import kotlinx.coroutines.Dispatchers

inline fun <reified V> Any?.ofTypeAndGet(): V? = if (this is V) this else null

class LocalRecyclerLinearPagedData @JvmOverloads constructor(context: Context,attributes: AttributeSet?= null, dif : Int = 0) : RecyclerView(context,attributes,dif) {

    var state : State? = null

    private var needLoadMore = false

    interface State {
        suspend fun more()
    }

    fun enableScrolledCallback(){
        layoutManager?.ofTypeAndGet<LinearLayoutManager>()?.let { layoutManager ->
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(!recyclerView.canScrollVertically(1)){
                        JobUtil.doJob(Dispatchers.Main){
                            state?.more()
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }
}