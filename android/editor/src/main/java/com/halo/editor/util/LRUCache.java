/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import java.util.LinkedHashMap;

public class LRUCache<K,V> extends LinkedHashMap<K,V> {

  private final int maxSize;

  public LRUCache(int maxSize) {
    this.maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry (Entry<K,V> eldest) {
    return size() > maxSize;
  }
}
