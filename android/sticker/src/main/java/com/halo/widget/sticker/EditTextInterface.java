/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker;

import androidx.annotation.DimenRes;
import androidx.annotation.Px;

/**
 * Interface used to allow custom EmojiEditText objects on another project.
 * The implementer must be a class that inherits from {@link android.view.View}.
 */
public interface EditTextInterface {
    void backspace();

    float getEmojiSize();

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size
     */
    void setEmojiSize(@Px int pixels);

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    void setEmojiSize(@Px int pixels, boolean shouldInvalidate);

    /**
     * sets the emoji size in pixels with the provided resource and automatically invalidates the text and renders it with the new size
     */
    void setEmojiSizeRes(@DimenRes int res);

    /**
     * sets the emoji size in pixels with the provided resource and invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    void setEmojiSizeRes(@DimenRes int res, boolean shouldInvalidate);
}
