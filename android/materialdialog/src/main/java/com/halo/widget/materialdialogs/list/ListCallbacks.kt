/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.list

import com.halo.widget.materialdialogs.MaterialDialog

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */

typealias ItemListener =
        ((dialog: MaterialDialog, index: Int, text: String) -> Unit)?

typealias SingleChoiceListener =
        ((dialog: MaterialDialog, index: Int, text: String) -> Unit)?

typealias MultiChoiceListener =
        ((dialog: MaterialDialog, indices: IntArray, items: List<String>) -> Unit)?
