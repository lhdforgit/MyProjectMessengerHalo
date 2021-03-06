/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

enum class MemoryMode {
  /**
   * In AUTO mode, halo will judge the preferred memory situation using [Master.preferredMemoryMode]
   * method.
   */
  AUTO,

  /**
   * In LOW mode, halo will always release resource of unselected Playables/Playbacks
   * (whose priority are >= 1).
   */
  LOW,

  /**
   * In NORMAL mode, halo will only reset the Playables/Playbacks whose distance to selected ones
   * are 1 (so 'next to' selected ones). Others will be released.
   */
  NORMAL,

  /**
   * In BALANCED mode, the release behavior is the same with 'NORMAL' mode, but unselected
   * Playables/Playbacks will not be reset.
   */
  BALANCED,

  /**
   * HIGH mode must be specified by client.
   *
   * In HIGH mode, any unselected Playables/Playbacks whose priorities are less than 8 will be
   * reset. Others will be released. This mode is memory-intensive and can be used in
   * many-videos-yet-low-memory-usage scenario like simple/short Videos.
   */
  HIGH,

  /**
   * "For the bravest only"
   *
   * INFINITE mode must be specified by client.
   *
   * In INFINITE mode, no unselected Playables/Playbacks will ever be released due to priority
   * change (though halo will release the resource once they are inactive).
   */
  INFINITE
}
