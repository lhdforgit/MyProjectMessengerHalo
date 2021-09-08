/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

/**
 * A function which takes 3 inputs and returns 1 output.
 */
public interface Function3<A, B, C, D> {
  D apply(A a, B b, C c);
}
