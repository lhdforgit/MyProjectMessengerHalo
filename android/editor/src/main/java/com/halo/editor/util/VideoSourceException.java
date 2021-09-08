/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

public final class VideoSourceException extends Exception {

  VideoSourceException(String message) {
    super(message);
  }

  VideoSourceException(String message, Exception inner) {
    super(message, inner);
  }
}
