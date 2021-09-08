package com.hahalolo.pickercolor.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.pickercolor.listener.ColorListener
import com.hahalolo.pickercolor.model.ColorShape
import com.hahalolo.pickercolor.util.SharedPref
import kotlinx.android.synthetic.main.dialog_color_picker.view.*

/**
 * Adapter for Recent Color
 */
class RecentColorAdapter(private val colors: List<String>) :
    RecyclerView.Adapter<RecentColorAdapter.MaterialColorViewHolder>() {

    private var colorShape = ColorShape.CIRCLE
    private var colorListener: ColorListener? = null
    private var emptyColor = "#E0E0E0"

    fun setColorListener(listener: ColorListener) {
        this.colorListener = listener
    }

    fun setEmptyColor(color: String) {
        this.emptyColor = color
    }

    fun setColorShape(colorShape: ColorShape) {
        this.colorShape = colorShape
    }

    override fun getItemCount() = SharedPref.RECENT_COLORS_LIMIT

    fun getItem(position: Int): String {
        return if (position < colors.size) {
            colors[position]
        } else {
            emptyColor
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialColorViewHolder {
        val rootView = ColorViewBinding.inflateAdapterItemView(parent)
        return MaterialColorViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: MaterialColorViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class MaterialColorViewHolder(private val rootView: View) :
        RecyclerView.ViewHolder(rootView) {

        private val colorView = rootView.colorView

        init {
            rootView.setOnClickListener {
                val index = it.tag as Int
                if (index < colors.size) {
                    val color = getItem(index)
                    colorListener?.onColorSelected(Color.parseColor(color), color)
                }
            }
        }

        fun bind(position: Int) {
            val color = getItem(position)
            rootView.tag = position
            ColorViewBinding.setBackgroundColor(colorView, color)
            ColorViewBinding.setCardRadius(colorView, colorShape)
        }
    }
}
