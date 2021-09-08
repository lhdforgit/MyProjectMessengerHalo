/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup.nodes;

/**
 * A boolean attribute that is written out without any value.
 * @deprecated just use null values (vs empty string) for booleans.
 */
public class BooleanAttribute extends Attribute {
    /**
     * Create a new boolean attribute from unencoded (raw) key.
     * @param key attribute key
     */
    public BooleanAttribute(String key) {
        super(key, null);
    }

    @Override
    protected boolean isBooleanAttribute() {
        return true;
    }
}
