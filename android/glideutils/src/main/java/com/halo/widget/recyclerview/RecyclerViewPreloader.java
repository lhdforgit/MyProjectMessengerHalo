/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.recyclerview;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.ListPreloader.PreloadModelProvider;
import com.bumptech.glide.ListPreloader.PreloadSizeProvider;
import com.bumptech.glide.RequestManager;

/**
 * @param <T> The type of the model being displayed in the {@link RecyclerView}.
 * @author ngannd
 * Create by ngannd on 11/12/2018
 *
 * <p>
 * Loads a few resources ahead in the direction of scrolling in any {@link RecyclerView} so that
 * images are in the memory cache just before the corresponding view in created in the list. Gives
 * the appearance of an infinitely large image cache, depending on scrolling speed, cpu speed, and
 * cache size.
 *
 * <p> Must be added as a listener to the {@link RecyclerView} using
 * {@link RecyclerView#addOnScrollListener(RecyclerView.OnScrollListener)}, or have its
 * corresponding methods called from another
 * {@link RecyclerView.OnScrollListener} to function. </p>
 *
 * <p> This class only works with {@link LinearLayoutManager} and
 * subclasses of {@link LinearLayoutManager}. </p>
 */
@SuppressWarnings("unused")
public final class RecyclerViewPreloader<T> extends RecyclerView.OnScrollListener {

    private final RecyclerToListViewScrollListener recyclerScrollListener;

    /** Helper constructor that accepts an {@link Activity}. */
    public RecyclerViewPreloader(
            @NonNull Activity activity,
            @NonNull PreloadModelProvider<T> preloadModelProvider,
            @NonNull PreloadSizeProvider<T> preloadDimensionProvider,
            int maxPreload) {
        this(Glide.with(activity), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /** Helper constructor that accepts an {@link FragmentActivity}. */
    public RecyclerViewPreloader(
            @NonNull FragmentActivity fragmentActivity,
            @NonNull PreloadModelProvider<T> preloadModelProvider,
            @NonNull PreloadSizeProvider<T> preloadDimensionProvider,
            int maxPreload) {
        this(Glide.with(fragmentActivity), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /** Helper constructor that accepts an {@link Fragment}. */
    public RecyclerViewPreloader(
            @NonNull Fragment fragment,
            @NonNull PreloadModelProvider<T> preloadModelProvider,
            @NonNull PreloadSizeProvider<T> preloadDimensionProvider,
            int maxPreload) {
        this(Glide.with(fragment), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /**
     * Helper constructor that accepts an {@link android.app.Fragment}.
     *
     * @deprecated Use constructor <code>RecyclerViewPreloader(Fragment, PreloadModelProvider<T>,
     * PreloadSizeProvider<T>)</code> instead.
     */
    @Deprecated
    public RecyclerViewPreloader(
            @NonNull android.app.Fragment fragment,
            @NonNull PreloadModelProvider<T> preloadModelProvider,
            @NonNull PreloadSizeProvider<T> preloadDimensionProvider,
            int maxPreload) {
        this(Glide.with(fragment), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /**
     * Constructor that accepts interfaces for providing the dimensions of images to preload, the list
     * of models to preload for a given position, and the request to use to load images.
     *
     * @param preloadModelProvider Provides models to load and requests capable of loading them.
     * @param preloadDimensionProvider Provides the dimensions of images to load.
     * @param maxPreload Maximum number of items to preload.
     */
    public RecyclerViewPreloader(
            @NonNull RequestManager requestManager,
            @NonNull PreloadModelProvider<T> preloadModelProvider,
            @NonNull PreloadSizeProvider<T> preloadDimensionProvider,
            int maxPreload) {

        ListPreloader<T> listPreloader =
                new ListPreloader<>(
                        requestManager, preloadModelProvider, preloadDimensionProvider, maxPreload);
        recyclerScrollListener = new RecyclerToListViewScrollListener(listPreloader);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        recyclerScrollListener.onScrolled(recyclerView, dx, dy);
    }
}
