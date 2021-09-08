/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.materialdialogs.actions

import com.google.android.material.button.MaterialButton
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.WhichButton

/** Returns true if the dialog has visible action buttons. */
fun MaterialDialog.hasActionButtons() = view.buttonsLayout.visibleButtons.isNotEmpty()

/** Returns the underlying view for an action button in the dialog. */
fun MaterialDialog.getActionButton(which: WhichButton) =
        view.buttonsLayout.actionButtons[which.index] as MaterialButton

/** Enables or disables an action button. */
fun MaterialDialog.setActionButtonEnabled(
        which: WhichButton,
        enabled: Boolean
) {
    getActionButton(which).isEnabled = enabled
}
