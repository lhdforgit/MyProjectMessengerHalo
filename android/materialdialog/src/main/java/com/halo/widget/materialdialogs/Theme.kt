/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs

import android.R.attr
import android.content.Context
import androidx.annotation.StyleRes
import com.halo.widget.materialdialogs.utils.getColor
import com.halo.widget.materialdialogs.utils.isColorDark

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal enum class Theme(@StyleRes val styleRes: Int) {
    LIGHT(R.style.MD_Light),
    DARK(R.style.MD_Dark);

    companion object {
        fun inferTheme(context: Context): Theme {
            val isPrimaryDark =
                    getColor(context = context, attr = attr.textColorPrimary).isColorDark()
            return if (isPrimaryDark) LIGHT else DARK
        }
    }
}
