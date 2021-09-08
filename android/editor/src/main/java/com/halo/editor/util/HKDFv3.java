/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

/**
 * Create by ndn
 * Create on 4/9/20
 * com.halo.editor.util
 */
public class HKDFv3 extends HKDF {
    @Override
    protected int getIterationStartOffset() {
        return 1;
    }
}
