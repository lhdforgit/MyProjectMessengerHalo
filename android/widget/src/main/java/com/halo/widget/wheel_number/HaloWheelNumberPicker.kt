package com.halo.widget.wheel_number

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.joinJobAndCancel
import com.halo.widget.databinding.HaloWheelPickerTextItemBinding
import com.halo.widget.databinding.LayoutHaloWheelNumberPickerBinding
import com.halo.widget.recycleview.ofTypeAndGet
import com.halo.widget.snaphelper.RecyclerViewGravitySnapHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay


class HaloWheelNumberPicker @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    def: Int = 0,
    defStyle: Int = 0
) : FrameLayout(context, attributes, def, defStyle) {
    val binding: LayoutHaloWheelNumberPickerBinding =
        LayoutHaloWheelNumberPickerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, SizeUtils.dp2px(250f))
    }

    var maxValue: Int = 17
    var minValue: Int = 1
    var displayValue = mutableListOf<String>()
        set(value) {
            if (value.isNullOrEmpty()) return
            field = value
            adapter.submit(value)
            if (value.size > 0) {
                val displayIntVal =
                    1 + value.size * ((Int.MAX_VALUE / 2).div(value.size))
                binding.recyclerView.scrollToPosition(displayIntVal)
            }
        }
    var currentValue: Int = 1
        set(value) {
            if (value in minValue..maxValue) {
                val displayIntVal =
                    value.plus(minValue) + displayValue.size * ((Int.MAX_VALUE / 2).div(
                        displayValue.size
                    ))
                binding.recyclerView.scrollToPosition(displayIntVal)
            } else throw Error("out of index min:$minValue..max:$maxValue")
            field = value
        }
    private var callback: ((Int) -> Unit)? = null
    private var adapter =
        HaloWheelNumberPickerAdapter<String>(displayValue) { position ->
            //todo onClick
        }

    fun setOnValueChangeListener(callback: (Int) -> Unit) {
        this.callback = callback
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        RecyclerViewGravitySnapHelper(Gravity.CENTER).apply {
            this.setMaxFlingDistance(SizeUtils.dp2px(1f))
            this.setScrollMsPerInch(50f)
            this.setMaxFlingSizeFraction(0.5f)
            this.attachToRecyclerView(binding.recyclerView)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scope?.joinJobAndCancel {
                            delay(50)
                            recyclerView.layoutManager.ofTypeAndGet<LinearLayoutManager>()
                                ?.apply {
                                    val center = findFirstVisibleItemPosition()
                                        .plus(3)
                                        .rem(displayValue.size)
                                    if (center == 0) {
                                        callback?.invoke(displayValue.size)
                                    } else callback?.invoke(center)
                                }
                        }
                    }
                }
            }
        })
    }

    private var scope: CoroutineScope? = CoroutineScope(Dispatchers.Main)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scope = CoroutineScope(Dispatchers.Main)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope?.cancel()
        scope = null
    }
}

/**
 * ========================================================================================
 * */
open class SingleTypeBaseViewHolder<Item>(
    val binding: HaloWheelPickerTextItemBinding,
    val onClickItem: (position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    var value: Item? = null
    var displayPosition: Int? = null

    init {
        itemView.setOnClickListener {
            displayPosition?.apply {
                onClickItem(this)
            }
        }
    }

    fun bind(
        value: Item,
        position: Int
    ) {
        binding.tv.text = value.ofTypeAndGet()
        displayPosition = position
    }
}

class HaloWheelNumberPickerAdapter<Item>(
    private val displayValue: MutableList<Item>,
    val onClickItem: (position: Int) -> Unit
) : RecyclerView.Adapter<SingleTypeBaseViewHolder<Item>>() {

    var currPosition: Int = 0

    override fun onBindViewHolder(holder: SingleTypeBaseViewHolder<Item>, position: Int) {
        if (displayValue.size > 0) {
            val positionIndex = position.rem(displayValue.size)
            val positionValItem = displayValue[positionIndex]
            currPosition = positionIndex
            holder.bind(positionValItem, positionIndex)
        }
    }

    override fun getItemCount(): Int = Integer.MAX_VALUE

    fun submit(newList: MutableList<Item>) {
        displayValue.clear()
        displayValue.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SingleTypeBaseViewHolder<Item> {
        val itemLayout = HaloWheelPickerTextItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleTypeBaseViewHolder<Item>(itemLayout) {
            val displayIntVal = it.rem(displayValue.size)
            onClickItem.invoke(displayIntVal)
        }
    }
}