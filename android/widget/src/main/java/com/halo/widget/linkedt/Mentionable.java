/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.linkedt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Abstract mention to be used with {@link MentionArrayAdapter}.
 */
public interface Mentionable {

    /**
     * Unique id of this mention.
     */
    @NonNull
    CharSequence getUsername();

    /**
     * Optional display name, located above username.
     */
    @Nullable
    CharSequence getDisplayname();

    /**
     * Optional avatar, may be Drawable, resources, or string url pointing to image.
     */
    @Nullable
    Object getAvatar();
}