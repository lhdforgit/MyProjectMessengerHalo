/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.linkedt;

import androidx.annotation.NonNull;

/**
 * Abstract hashtag to be used with {@link HashtagArrayAdapter}.
 */
public interface Hashtagable {

    /**
     * Unique id of this hashtag.
     */
    @NonNull
    CharSequence getId();

    /**
     * Optional count, located right to hashtag name.
     */
    int getCount();
}