/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.materialdialogs.internal.button

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import com.google.android.material.button.MaterialButton
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.Theme
import com.halo.widget.materialdialogs.utils.*

/**
 * Represents an action button in a dialog, positive, negative, or neutral. Handles switching
 * out its selector, padding, and text alignment based on whether buttons are in stacked mode or not.
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal class DialogActionButton(
        context: Context,
        attrs: AttributeSet? = null
) : MaterialButton(context, attrs) {

    private val paddingDefault = dimenPx(R.dimen.md_action_button_padding_horizontal)
    private val paddingStacked = dimenPx(R.dimen.md_stacked_action_button_padding_horizontal)

    private var enabledColor: Int = 0
    private var disabledColor: Int = 0

    init {
        isClickable = true
        isFocusable = true
    }

    fun update(
            baseContext: Context,
            appContext: Context,
            stacked: Boolean
    ) {
        // Text color
        val theme = Theme.inferTheme(appContext)
        enabledColor = getColor(appContext, attr = R.attr.colorAccent)
        val disabledColorRes =
                if (theme == Theme.LIGHT) R.color.text_secondary
                else R.color.text_light
        disabledColor = getColor(baseContext, res = disabledColorRes)
        setTextColor(enabledColor)

        // Selector
        val selectorAttr = if (stacked) R.attr.md_item_selector else R.attr.md_button_selector
        background = getDrawable(baseContext, attr = selectorAttr)

        // Padding
        val sidePadding = if (stacked) paddingStacked else paddingDefault
        updatePadding(left = sidePadding, right = sidePadding)

        // Text alignment
        if (stacked) setGravityEndCompat()
        else gravity = CENTER

        // Invalidate in case enabled state was changed before this method executed
        isEnabled = isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setTextColor(if (enabled) enabledColor else disabledColor)
    }
}
