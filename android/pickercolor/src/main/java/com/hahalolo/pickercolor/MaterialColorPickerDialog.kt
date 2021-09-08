package com.hahalolo.pickercolor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.pickercolor.adapter.MaterialColorPickerAdapter
import com.hahalolo.pickercolor.listener.ColorListener
import com.hahalolo.pickercolor.listener.DismissListener
import com.hahalolo.pickercolor.model.ColorShape
import com.hahalolo.pickercolor.model.ColorSwatch
import com.hahalolo.pickercolor.util.ColorUtil
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.dialog.DialogFragmentBase

/**
 * Color Picker from Predefined color set in AlertDialog
 *
 */
class MaterialColorPickerDialog private constructor(
    context: Context,
    val title: String,
    val positiveButton: String,
    val negativeButton: String,
    val colorListener: ColorListener?,
    val dismissListener: DismissListener?,
    val defaultColor: String?,
    val colorSwatch: ColorSwatch,
    var colorShape: ColorShape,
    val colors: List<String>? = null,
    var isTickColorPerCard: Boolean = false
) : DialogFragmentBase(context) {

    class Builder(val context: Context) {
        private var title: String = ""
        private var positiveButton: String = ""
        private var negativeButton: String = ""
        private var colorListener: ColorListener? = null
        private var dismissListener: DismissListener? = null
        private var defaultColor: String? = null
        private var colorSwatch: ColorSwatch = ColorSwatch._300
        private var colorShape: ColorShape = ColorShape.CIRCLE
        private var colors: List<String>? = null
        private var isTickColorPerCard: Boolean = false

        /**
         * Set Dialog Title
         *
         * @param title String
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Set Dialog Title
         *
         * @param title StringRes
         */
        fun setTitle(@StringRes title: Int): Builder {
            //this.title = context.getString(title)
            return this
        }

        /**
         * Set Positive Button Text
         *
         * @param text String
         */
        fun setPositiveButton(text: String): Builder {
            this.positiveButton = text
            return this
        }

        /**
         * Set Positive Button Text
         *
         * @param text StringRes
         */
        fun setPositiveButton(@StringRes text: Int): Builder {
            //this.positiveButton = context.getString(text)
            return this
        }

        /**
         * Set Negative Button Text
         *
         * @param text String
         */
        fun setNegativeButton(text: String): Builder {
            this.negativeButton = text
            return this
        }

        /**
         * Set Negative Button Text
         *
         * @param text StringRes
         */
        fun setNegativeButton(@StringRes text: Int): Builder {
            //this.negativeButton = context.getString(text)
            return this
        }

        /**
         * Set Default Selected Color
         *
         * @param color String Hex Color
         */
        fun setDefaultColor(color: String): Builder {
            this.defaultColor = color
            return this
        }

        /**
         * Set Default Selected Color
         *
         * @param color Int ColorRes
         */
        fun setDefaultColor(@ColorRes color: Int): Builder {
            this.defaultColor = ColorUtil.formatColor(color)
            return this
        }

        /**
         * Set Color CardView Shape,
         *
         * @param colorShape ColorShape
         */
        fun setColorShape(colorShape: ColorShape): Builder {
            this.colorShape = colorShape
            return this
        }

        /**
         * Set Color Swatch
         *
         * @param colorSwatch ColorSwatch
         */
        fun setColorSwatch(colorSwatch: ColorSwatch): Builder {
            this.colorSwatch = colorSwatch
            return this
        }

        /**
         * Set Color Listener
         *
         * @param listener ColorListener
         */
        fun setColorListener(listener: ColorListener): Builder {
            this.colorListener = listener
            return this
        }

        /**
         * Set Color Listener
         *
         * @param listener (Int, String)->Unit
         */
        fun setColorListener(listener: (Int, String) -> Unit): Builder {
            this.colorListener = object : ColorListener {
                override fun onColorSelected(color: Int, colorHex: String) {
                    listener(color, colorHex)
                }
            }
            return this
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @param listener DismissListener
         */
        fun setDismissListener(listener: DismissListener?): Builder {
            this.dismissListener = listener
            return this
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @param listener listener: () -> Unit
         */
        fun setDismissListener(listener: () -> Unit): Builder {
            this.dismissListener = object : DismissListener {
                override fun onDismiss() {
                    listener.invoke()
                }
            }
            return this
        }

        /**
         * Provide PreDefined Colors,
         *
         * If colors is not empty, User can choose colors from provided list
         * If colors is empty, User can choose colors based on ColorSwatch
         *
         * @param colors List<String> List of Hex Colors
         */
        fun setColors(colors: List<String>): Builder {
            this.colors = colors
            return this
        }

        fun setColors(colors: Array<String>): Builder {
            this.colors = colors.toList()
            return this
        }

        /**
         * Provide PreDefined Colors,
         *
         * If colors is not empty, User can choose colors from provided list
         * If colors is empty, User can choose colors based on ColorSwatch
         *
         * @param colors List<Int> List of Color Resource
         */
        fun setColorRes(colors: List<Int>): Builder {
            this.colors = colors.map { ColorUtil.formatColor(it) }
            return this
        }

        fun setColorRes(colors: IntArray): Builder {
            this.colors = colors.map { ColorUtil.formatColor(it) }
            return this
        }

        /**
         * Set tick icon color, Default will be false
         *
         * If false,
         *     First the majority of color(dark/light) will be calculated
         *     If dark color count > light color count
         *          tick color will be WHITE
         *     else
         *          tick color will be BLACK
         *     Here, Tick color will be same card,
         *     Which might create issue with black and white color in list
         *
         * If true,
         *      based on the each color(dark/light) the card tick color will be decided
         *      Here, Tick color will be different for each card
         *
         * @param tickColorPerCard Boolean
         */
        fun setTickColorPerCard(tickColorPerCard: Boolean): Builder {
            this.isTickColorPerCard = tickColorPerCard
            return this
        }

        /**
         * Creates an {@link MaterialColorPickerDialog} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the dialog.
         */
        fun build(): MaterialColorPickerDialog {
            return MaterialColorPickerDialog(
                context = context,
                title = title,
                positiveButton = positiveButton,
                negativeButton = negativeButton,
                colorListener = colorListener,
                dismissListener = dismissListener,
                defaultColor = defaultColor,
                colorShape = colorShape,
                colorSwatch = colorSwatch,
                colors = colors,
                isTickColorPerCard = isTickColorPerCard
            )
        }

        /**
         * Show BottomSheet Dialog
         */
        fun showBottomSheet(fragmentManager: FragmentManager) {
            build().showBottomSheet(fragmentManager)
        }
    }

    /**
     * Show BottomSheet Dialog
     */
    fun showBottomSheet(fragmentManager: FragmentManager) {
        MaterialColorPickerBottomSheet.getInstance(this)
            .setColorListener(colorListener)
            .setDismissListener(dismissListener)
            .show(fragmentManager, "")
    }

    override fun onCreateHeaderView(header: HeaderDialog?): View? {
        header?.run {
            setTitle(title)
            setLogo(R.drawable.ic_chat_setting_header_color)
            return getView()
        }
        return null
    }

    override fun onCreateCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dialogView = inflater.inflate(R.layout.dialog_material_color_picker, container) as View
        initView(dialogView)
        return dialogView
    }

    private fun initView(dialogView: View) {
        context?.apply {
            // Setup Color Listing Adapter
            val colorList = colors ?: ColorUtil.getColors(this, colorSwatch.value)
            val adapter = MaterialColorPickerAdapter(colorList)

            adapter.setColorShape(colorShape)
            adapter.setListener(object : ColorListener {
                override fun onColorSelected(color: Int, colorHex: String) {
                    colorListener?.onColorSelected(color, colorHex)
                    dismiss()
                }
            })
            // Setup Color RecyclerView
            val materialColorRV = dialogView.findViewById<RecyclerView>(R.id.materialColorRV)
            materialColorRV.setHasFixedSize(true)
            materialColorRV.layoutManager = HaloGridLayoutManager(context, 4)
            materialColorRV.adapter = adapter
            dismissListener?.let { listener ->
                dialog?.setOnDismissListener {
                    listener.onDismiss()
                }
            }
        }
    }
}
