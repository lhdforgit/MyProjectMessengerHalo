/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import com.hahalolo.player.core.PlayerParameters

internal interface PlayerParametersChangeListener {

  fun onPlayerParametersChanged(parameters: PlayerParameters)
}
