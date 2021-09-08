/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.materialdialogs

import com.halo.widget.materialdialogs.internal.button.DialogActionButtonLayout

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
enum class WhichButton(val index: Int) {
    POSITIVE(DialogActionButtonLayout.INDEX_POSITIVE),
    NEGATIVE(DialogActionButtonLayout.INDEX_NEGATIVE),
    NEUTRAL(DialogActionButtonLayout.INDEX_NEUTRAL);

    companion object {
        fun fromIndex(index: Int) = when (index) {
            DialogActionButtonLayout.INDEX_POSITIVE -> POSITIVE
            DialogActionButtonLayout.INDEX_NEGATIVE -> NEGATIVE
            DialogActionButtonLayout.INDEX_NEUTRAL -> NEUTRAL
            else -> throw IndexOutOfBoundsException("$index is not an action button index.")
        }
    }
}
