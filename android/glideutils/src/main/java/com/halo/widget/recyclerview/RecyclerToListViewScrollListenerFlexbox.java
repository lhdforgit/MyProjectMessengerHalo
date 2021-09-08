/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.recyclerview;

import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

/**
 * @author ngannd
 * Create by ngannd on 11/12/2018
 * <p>
 * Converts {@link RecyclerView.OnScrollListener} events to
 * {@link AbsListView} scroll events.
 *
 * <p>Requires that the the recycler view be using a {@link com.google.android.flexbox.FlexboxLayoutManager} subclass.
 */
@SuppressWarnings("WeakerAccess")
public final class RecyclerToListViewScrollListenerFlexbox extends RecyclerView.OnScrollListener {

    public static final int UNKNOWN_SCROLL_STATE = Integer.MIN_VALUE;
    private final AbsListView.OnScrollListener scrollListener;
    private int lastFirstVisible = -1;
    private int lastVisibleCount = -1;
    private int lastItemCount = -1;

    public RecyclerToListViewScrollListenerFlexbox(@NonNull AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        try {
            int listViewState;
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_IDLE;
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_FLING;
                    break;
                default:
                    listViewState = UNKNOWN_SCROLL_STATE;
            }

            scrollListener.onScrollStateChanged(null /*view*/, listViewState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        try {
            FlexboxLayoutManager layoutManager = (FlexboxLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
                int itemCount = recyclerView.getAdapter() == null ? 0 : recyclerView.getAdapter().getItemCount();

                if (firstVisible != lastFirstVisible || visibleCount != lastVisibleCount
                        || itemCount != lastItemCount) {
                    scrollListener.onScroll(null, firstVisible, visibleCount, itemCount);
                    lastFirstVisible = firstVisible;
                    lastVisibleCount = visibleCount;
                    lastItemCount = itemCount;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}