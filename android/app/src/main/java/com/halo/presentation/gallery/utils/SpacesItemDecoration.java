/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/12/18
 * <p>
 * RecyclerViews support the concept of ItemDecoration: special offsets and drawing around each element.
 * recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
 * int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
 * recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
 * <p>
 * if you already use top and bottom padding as part of your RecyclerView (and use clipToPadding="false"), then you can restructure things slightly.
 * If you don't however, you'd just be moving the if check to be the last time (as you'd still want the bottom padding on the last item).
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}
