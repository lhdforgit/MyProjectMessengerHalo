package com.hahalolo.pickercolor.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.pickercolor.R
import com.hahalolo.pickercolor.listener.ColorListener
import com.hahalolo.pickercolor.model.ColorShape
import com.hahalolo.pickercolor.util.ColorUtil
import kotlinx.android.synthetic.main.adapter_material_color_picker.view.*

/**
 * Material Color Listing
 */
class MaterialColorPickerAdapter(private val colors: List<String>) :
    RecyclerView.Adapter<MaterialColorPickerAdapter.MaterialColorViewHolder>() {

    private var color = ""
    private var colorShape = ColorShape.CIRCLE
    private var isTickColorPerCard = false
    private var listener: ColorListener? = null

    init {
    }

    fun setListener(listener: ColorListener?) {
        this.listener = listener
    }

    fun setColorShape(colorShape: ColorShape) {
        this.colorShape = colorShape
    }

    fun setDefaultColor(color: String) {
        this.color = color
    }

    fun setTickColorPerCard(tickColorPerCard: Boolean) {
        this.isTickColorPerCard = tickColorPerCard
    }

    fun getSelectedColor() = color

    fun getItem(position: Int) = colors[position]

    override fun getItemCount() = colors.size

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
        fun bind(position: Int) {
            kotlin.runCatching {
                rootView.tag = position
                var colorInt: Int = 0
                val color = getItem(position)
                colorInt = if (ColorUtil.isFormatHexColor(color)) {
                    ColorViewBinding.setBackgroundColor(colorView, color)
                    ColorUtil.parseColor(color)
                } else {
                    colorView.setBackgroundResource(
                        ColorUtil.getResId(
                            color,
                            R.drawable::class.java
                        )
                    )
                    ColorUtil.getResId(color, R.drawable::class.java)
                }
                ColorViewBinding.setCardRadius(colorView, colorShape)

                itemView.setOnClickListener {
                    listener?.onColorSelected(colorInt, color)
                }
            }
        }
    }
}
