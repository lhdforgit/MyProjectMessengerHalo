package com.hahalolo.pickercolor.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.hahalolo.pickercolor.R
import com.hahalolo.pickercolor.model.ColorShape

/**
 * Common method for {@see RecentColorAdapter and MaterialColorPickerAdapter}
 */
object ColorViewBinding {

    /**
     *  Inflate ColorPicker Adapter Item View and return it
     *
     *  @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     *
     *  @return View A Adapter Item View
     */
    fun inflateAdapterItemView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(
            R.layout.adapter_material_color_picker,
            parent,
            false
        )
    }

    /**
     * Set Card Background Color from provided hex color
     *
     * @param cardView CardView on which Background color will applied
     * @param hexColor String Color Hex. eg. #3399ff
     */
    fun setBackgroundColor(cardView: CardView, hexColor: String) {
        cardView.setCardBackgroundColor(Color.parseColor(hexColor))
    }

    fun setBackgroundColor(imageView: ImageView, srcId: Int) {
        imageView.setImageResource(srcId)
    }

    /**
     * Set Card Corner Radius if provided shape is Square
     *
     * @param cardView CardView on which Corner Radius will applied
     * @param colorShape ColorShape Square or Round
     */
    fun setCardRadius(cardView: CardView, colorShape: ColorShape) {
        if (colorShape == ColorShape.SQAURE) {
            val resources = cardView.context.resources
            cardView.radius = resources.getDimension(R.dimen.color_card_square_radius)
        }
    }
}
