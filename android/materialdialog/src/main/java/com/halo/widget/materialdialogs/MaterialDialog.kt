/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
@file:Suppress("unused")

package com.halo.widget.materialdialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.halo.widget.materialdialogs.Theme.Companion.inferTheme
import com.halo.widget.materialdialogs.WhichButton.*
import com.halo.widget.materialdialogs.actions.getActionButton
import com.halo.widget.materialdialogs.callbacks.invokeAll
import com.halo.widget.materialdialogs.internal.list.DialogAdapter
import com.halo.widget.materialdialogs.internal.list.DialogRecyclerView
import com.halo.widget.materialdialogs.internal.main.DialogLayout
import com.halo.widget.materialdialogs.internal.main.DialogScrollView
import com.halo.widget.materialdialogs.list.getListAdapter
import com.halo.widget.materialdialogs.utils.*

internal fun assertOneSet(
        method: String,
        b: Any?,
        a: Int?
) {
    if ((a == null || a == 0) && b == null) {
        throw IllegalArgumentException("$method: You must specify a resource ID or literal value")
    }
}

typealias DialogCallback = (MaterialDialog) -> Unit

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
class MaterialDialog(
        val windowContext: Context
) : Dialog(windowContext, inferTheme(windowContext).styleRes) {

    /**
     * A named config map, used like tags for extensions.
     *
     * Developers extending functionality of Material Dialogs should not use things
     * like static variables to store things. They instead should be stored at a dialog
     * instance level, which is what this provides.
     */
    val config: MutableMap<String, Any> = mutableMapOf()

    /** Returns true if auto dismiss is enabled. */
    var autoDismissEnabled: Boolean = true
        internal set

    var titleFont: Typeface? = null
        internal set
    var bodyFont: Typeface? = null
        internal set
    var buttonFont: Typeface? = null
        internal set

    internal val view: DialogLayout = inflate(R.layout.md_dialog_base)
    internal var textViewMessage: TextView? = null
    internal var contentScrollView: DialogScrollView? = null
    internal var contentScrollViewFrame: LinearLayout? = null
    internal var contentRecyclerView: DialogRecyclerView? = null
    internal var contentCustomView: View? = null

    internal val preShowListeners = mutableListOf<DialogCallback>()
    internal val showListeners = mutableListOf<DialogCallback>()
    internal val dismissListeners = mutableListOf<DialogCallback>()
    internal val cancelListeners = mutableListOf<DialogCallback>()

    private val positiveListeners = mutableListOf<DialogCallback>()
    private val negativeListeners = mutableListOf<DialogCallback>()
    private val neutralListeners = mutableListOf<DialogCallback>()

    init {
        setContentView(view)
        this.view.dialog = this
        setWindowConstraints()
        setDefaults()
    }

    /**
     * Shows an drawable to the left of the dialog title.
     *
     * @param res The drawable resource to display as the drawable.
     * @param drawable The drawable to display as the drawable.
     */
    fun icon(
            @DrawableRes res: Int? = null,
            drawable: Drawable? = null
    ): MaterialDialog {
        assertOneSet("icon", drawable, res)
        populateIcon(
                view.titleLayout.iconView,
                iconRes = res,
                icon = drawable
        )
        return this
    }

    /**
     * Shows a title, or header, at the top of the dialog.
     *
     * @param res The string resource to display as the title.
     * @param text The literal string to display as the title.
     */
    fun title(
            @StringRes res: Int? = null,
            text: String? = null
    ): MaterialDialog {
        assertOneSet("title", text, res)
        populateText(
                view.titleLayout.titleView,
                textRes = res,
                text = text,
                typeface = this.titleFont,
                textColor = R.attr.md_color_title
        )
        return this
    }

    /**
     * Shows a message, below the title, and above the action buttons (and checkbox prompt).
     *
     * @param res The string resource to display as the message.
     * @param text The literal string to display as the message.
     */
    fun message(
            @StringRes res: Int? = null,
            text: CharSequence? = null
    ): MaterialDialog {
        if (this.contentCustomView != null) {
            throw IllegalStateException("message() should be used BEFORE customView().")
        }
        addContentScrollView()
        addContentMessageView(res, text)
        return this
    }

    /**
     * Shows a positive action button, in the far right at the bottom of the dialog.
     *
     * @param res The string resource to display on the title.
     * @param text The literal string to display on the button.
     * @param click A listener to invoke when the button is pressed.
     */
    fun positiveButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            positiveListeners.add(click)
        }

        val btn = getActionButton(POSITIVE)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
                btn,
                textRes = res,
                text = text,
                fallback = android.R.string.ok,
                typeface = this.buttonFont
        )
        return this
    }

    /** Clears any positive action button listeners set via usages of [positiveButton]. */
    fun clearPositiveListeners(): MaterialDialog {
        this.positiveListeners.clear()
        return this
    }

    /**
     * Shows a negative action button, to the left of the positive action button (or at the far
     * right if there is no positive action button).
     *
     * @param res The string resource to display on the title.
     * @param text The literal string to display on the button.
     * @param click A listener to invoke when the button is pressed.
     */
    fun negativeButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            negativeListeners.add(click)
        }

        val btn = getActionButton(NEGATIVE)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
                btn,
                textRes = res,
                text = text,
                fallback = android.R.string.cancel,
                typeface = this.buttonFont
        )
        return this
    }

    /** Clears any negative action button listeners set via usages of [negativeButton]. */
    fun clearNegativeListeners(): MaterialDialog {
        this.negativeListeners.clear()
        return this
    }

    @Deprecated(
            "Use of neutral buttons is discouraged, see " +
                    "https://material.io/design/components/dialogs.html#actions."
    )
    fun neutralButton(
            @StringRes res: Int? = null,
            text: CharSequence? = null,
            click: DialogCallback? = null
    ): MaterialDialog {
        if (click != null) {
            neutralListeners.add(click)
        }

        val btn = getActionButton(NEUTRAL)
        if (res == null && text == null && btn.isVisible()) {
            // Didn't receive text and the button is already setup,
            // so just stop with the added listener.
            return this
        }

        populateText(
                btn,
                textRes = res,
                text = text,
                typeface = this.buttonFont
        )
        return this
    }

    @Deprecated(
            "Use of neutral buttons is discouraged, see " +
                    "https://material.io/design/components/dialogs.html#actions."
    )
    fun clearNeutralListeners(): MaterialDialog {
        this.neutralListeners.clear()
        return this
    }

    /**
     * Turns off auto dismiss. Action button and list item clicks won't dismiss the dialog on their
     * own. You have to handle dismissing the dialog manually with the [dismiss] method.
     */
    @CheckResult
    fun noAutoDismiss(): MaterialDialog {
        this.autoDismissEnabled = false
        return this
    }

    /** Turns debug mode on or off. Draws spec guides over dialog views. */
    @CheckResult
    fun debugMode(debugMode: Boolean = true): MaterialDialog {
        this.view.debugMode = debugMode
        return this
    }

    /** Opens the dialog. */
    override fun show() {
        preShow()
        super.show()
    }

    /** Applies multiple properties to the dialog and opens it. */
    inline fun show(func: MaterialDialog.() -> Unit): MaterialDialog {
        this.func()
        this.show()
        return this
    }

    /** A fluent version of [setCancelable]. */
    fun cancelable(cancelable: Boolean): MaterialDialog {
        this.setCancelable(cancelable)
        return this
    }

    /** A fluent version of [setCanceledOnTouchOutside]. */
    fun cancelOnTouchOutside(cancelable: Boolean): MaterialDialog {
        this.setCanceledOnTouchOutside(cancelable)
        return this
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }

    internal fun onActionButtonClicked(which: com.halo.widget.materialdialogs.WhichButton) {
        when (which) {
            POSITIVE -> {
                positiveListeners.invokeAll(this)
                val adapter = getListAdapter() as? DialogAdapter<*, *>
                adapter?.positiveButtonClicked()
            }
            NEGATIVE -> negativeListeners.invokeAll(this)
            NEUTRAL -> neutralListeners.invokeAll(this)
        }
        if (autoDismissEnabled) {
            dismiss()
        }
    }
}
