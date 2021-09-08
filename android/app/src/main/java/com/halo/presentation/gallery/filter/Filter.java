/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.filter;

import android.content.Context;

import com.halo.presentation.gallery.SelectionCreator;
import com.halo.presentation.gallery.entities.IncapableCause;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.utils.MimeType;

import java.util.Set;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 * Filter for choosing a {@link Item}. You can add multiple Filters through
 * {@link SelectionCreator#addFilter(Filter)}.
 */
@SuppressWarnings("unused")
public abstract class Filter {
    /**
     * Convenient constant for a minimum value.
     */
    public static final int MIN = 0;
    /**
     * Convenient constant for a maximum value.
     */
    public static final int MAX = Integer.MAX_VALUE;
    /**
     * Convenient constant for 1024.
     */
    public static final int K = 1024;

    /**
     * Against what mime types this filter applies.
     */
    protected abstract Set<MimeType> constraintTypes();

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, {@link IncapableCause} if not selectable.
     */
    public abstract IncapableCause filter(Context context, Item item);

    /**
     * Whether an {@link Item} need filtering.
     */
    protected boolean needFiltering(Context context, Item item) {
        for (MimeType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), item.getContentUri())) {
                return true;
            }
        }
        return false;
    }
}
