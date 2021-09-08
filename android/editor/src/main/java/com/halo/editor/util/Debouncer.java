/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.os.Handler;

/**
 * A class that will throttle the number of runnables executed to be at most once every specified
 * interval. However, it could be longer if events are published consistently.
 *
 * Useful for performing actions in response to rapid user input, such as inputting text, where you
 * don't necessarily want to perform an action after <em>every</em> input.
 *
 * See http://rxmarbles.com/#debounce
 */
public class Debouncer {

  private final Handler handler;
  private final long    threshold;

  /**
   * @param threshold Only one runnable will be executed via {@link #publish(Runnable)} every
   *                  {@code threshold} milliseconds.
   */
  public Debouncer(long threshold) {
    this.handler   = new Handler();
    this.threshold = threshold;
  }

  public void publish(Runnable runnable) {
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(runnable, threshold);
  }

  public void clear() {
    handler.removeCallbacksAndMessages(null);
  }
}
