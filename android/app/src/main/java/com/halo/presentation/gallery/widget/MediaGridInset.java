/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/12/18
 * <p>
 * Inset between media grid item
 */
public class MediaGridInset extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpacing;
    private boolean mIncludeEdge;

    /**
     * Construction
     *
     * @param spanCount   count span
     * @param spacing     spacing between item
     * @param includeEdge include edge
     */
    public MediaGridInset(int spanCount, int spacing, boolean includeEdge) {
        this.mSpanCount = spanCount;
        this.mSpacing = spacing;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mSpanCount; // item column

        if (mIncludeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = mSpacing - column * mSpacing / mSpanCount;
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mSpacing / mSpanCount;

            if (position < mSpanCount) { // top edge
                outRect.top = mSpacing;
            }
            outRect.bottom = mSpacing; // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * mSpacing / mSpanCount;
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;
            if (position >= mSpanCount) {
                outRect.top = mSpacing; // item top
            }
        }
    }
}
