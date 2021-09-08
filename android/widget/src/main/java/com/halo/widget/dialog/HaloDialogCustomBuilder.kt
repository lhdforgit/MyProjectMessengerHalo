package com.halo.widget.dialog

import android.view.View

abstract class HaloDialogCustomBuilder {
    var title: String? = null
    var icon: Int? = null
    var description: String? = null
    var descriptionStyle: CharSequence? = null
    var onClickPrimary: View.OnClickListener? = null
    var onClickSuccess: View.OnClickListener? = null
    var onClickWarning: View.OnClickListener? = null
    var onClickCancel: View.OnClickListener? = null
    var textPrimary: String? = null
    var textSuccess: String? = null
    var textWarning: String? = null
    var textCancel: String? = null
    var customView : View? = null

    abstract fun setIcon(): HaloDialogCustomBuilder

    abstract fun build() : HaloDialogCustom

    fun setTitle(title: String?): HaloDialogCustomBuilder {
        this.title = title
        return this
    }

    fun setDescription(description: String?): HaloDialogCustomBuilder {
        this.description = description
        return this
    }

    fun setDescription(descriptionStyle: CharSequence?): HaloDialogCustomBuilder {
        this.descriptionStyle = descriptionStyle
        return this
    }

    fun setIcon(icon: Int?): HaloDialogCustomBuilder {
        this.icon = icon
        return this
    }

    fun setOnClickPrimary(onClickPrimary: View.OnClickListener?): HaloDialogCustomBuilder {
        this.onClickPrimary = onClickPrimary
        return this
    }

    fun setOnClickSuccess(onClickSuccess: View.OnClickListener?): HaloDialogCustomBuilder {
        this.onClickSuccess = onClickSuccess
        return this
    }

    fun setOnClickWarning(onClickWarning: View.OnClickListener?): HaloDialogCustomBuilder {
        this.onClickWarning = onClickWarning
        return this
    }

    fun setOnClickCancel(onClickCancel: View.OnClickListener?): HaloDialogCustomBuilder {
        this.onClickCancel = onClickCancel
        return this
    }

    fun setTextPrimary(textPrimary: String?): HaloDialogCustomBuilder {
        this.textPrimary = textPrimary
        return this
    }

    fun setTextSuccess(textSuccess: String?): HaloDialogCustomBuilder {
        this.textSuccess = textSuccess
        return this
    }

    fun setTextWarning(textWarning: String?): HaloDialogCustomBuilder {
        this.textWarning = textWarning
        return this
    }

    fun setTextCancel(textCancel: String?): HaloDialogCustomBuilder {
        this.textCancel = textCancel
        return this
    }

    fun setCustomView(customView: View?): HaloDialogCustomBuilder {
        this.customView = customView
        return this
    }

    fun setDisableDismiss() {
        build().isCancelable = true
    }
}