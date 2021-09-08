/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottom_sheet

import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay

/*
inline fun <reified T : String> T.getIdOfString() : Int {

}*/


suspend fun FloatingActionButton.showFab(turnOn : Boolean){
    delay(10)
    this.isVisible = turnOn
}