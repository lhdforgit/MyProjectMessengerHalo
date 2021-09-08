/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import java.io.IOException;

/**
 * A function which takes 1 input and returns 1 output, and is capable of throwing an IO Exception.
 */
public interface IOFunction<I, O> {
  O apply(I input) throws IOException;
}
