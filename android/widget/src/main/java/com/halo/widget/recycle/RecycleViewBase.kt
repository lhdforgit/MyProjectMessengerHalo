package com.halo.widget.recycle


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.halo.common.utils.SizeUtils
import com.halo.widget.HaloLinearLayoutManager


class ItemInfo<T : Any>(
    var position: Int? = null,
    var data: T? = null,
    var isLastItem: Boolean = false
)

enum class RecycleViewAnimation() {
    NONE, DOWN_TO_UP, UP_TO_DOWN, RIGHT_TO_LEFT, LEFT_TO_RIGHT
}


fun <T : Any, VDB : ViewDataBinding, I : Any?> RecyclerView.setContentView(
    listData: List<T>,
    holder: HolderBase<VDB, I>,
    animation: RecycleViewAnimation? = RecycleViewAnimation.NONE,
    bind: (binding: VDB, itemInfo: ItemInfo<T>?, listener: I?) -> Unit = { _, _, _ -> },
    orientation: Int? = HaloLinearLayoutManager.VERTICAL,
    layoutManagers: RecyclerView.LayoutManager? = null,
    //isSetAdapter: Boolean = true
): RecyclerView.Adapter<HolderBase<VDB, I>> {
    val recycleViewAdapter = RecycleViewBase(listData, holder, bind)
    //if (isSetAdapter) {
    layoutManagers?.apply {
        this@setContentView.layoutManager = this
    } ?: run {
        orientation?.apply {
            this@setContentView.layoutManager = HaloLinearLayoutManager(context, this)
        }
    }
    this.itemAnimator = null

    //}
    when (animation) {
        RecycleViewAnimation.NONE -> {

        }
        RecycleViewAnimation.DOWN_TO_UP -> {

        }
        RecycleViewAnimation.UP_TO_DOWN -> {

        }
        RecycleViewAnimation.LEFT_TO_RIGHT -> {

        }
        RecycleViewAnimation.RIGHT_TO_LEFT -> {

        }
    }
    this.adapter = recycleViewAdapter
    return recycleViewAdapter
}

class RecycleViewBase<T : Any, VDB : ViewDataBinding, I : Any?>(
    var list: List<T>,
    var holder: HolderBase<VDB, I>,
    var bind: (binding: VDB, itemInfo: ItemInfo<T>?, interact: I?) -> Unit,
) : RecyclerView.Adapter<HolderBase<VDB, I>>() {


    //var item_load_more: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBase<VDB, I> {
        return holder.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: HolderBase<VDB, I>, position: Int) {
        holder.setIsRecyclable(false)
        holder.apply {
            val isLastItem = position == (list.size - 1)
            //"last item :... ${(position)} ___ ${list.size} __ $isLastItem ".Log()
            val itemInfo = ItemInfo(
                position = position,
                data = list.getOrNull(position),
                isLastItem = isLastItem
            )
            bind { _binding, _listener ->
                bind(_binding, itemInfo, _listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

open class HolderBase<VDB : ViewDataBinding, I : Any?>(
    var context: Context,
    var layoutId: Int,
    var listener: I? = null,
    var width: Int? = null,
) : RecyclerView.ViewHolder(getView(context, layoutId, width)) {
    fun onCreateViewHolder(parent: ViewGroup): HolderBase<VDB, I> {
        return HolderBase(parent.context, layoutId, listener, width)
    }

    fun bind(action: (binding: VDB, listener: I?) -> Unit) {
        val binding: VDB? = DataBindingUtil.bind(itemView)
        //var binding :VDB= ItemViewBinding.inflate()
        binding?.let { _binding ->
            action(_binding, listener)
        }
        binding?.executePendingBindings()
    }
}


fun getView(context: Context, layoutId: Int, width: Int? = null): View {
    val view = View.inflate(context, layoutId, null).apply {
        //"width :.. $width ".Log()
        layoutParams = if (width != null) {
            LinearLayout.LayoutParams(
                SizeUtils.dp2px(width.toFloat()),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        } else {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
    return view
}



