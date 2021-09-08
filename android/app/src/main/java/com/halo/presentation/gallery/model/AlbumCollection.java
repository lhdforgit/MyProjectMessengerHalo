/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.halo.presentation.gallery.loader.AlbumLoader;

import java.lang.ref.WeakReference;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;
    private int mCurrentSelection;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        return AlbumLoader.newInstance(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mCallbacks.onAlbumLoad(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mCallbacks.onAlbumReset();
    }

    public void onCreate(FragmentActivity activity, AlbumCallbacks callbacks) {
        mContext = new WeakReference<>(activity);
        mLoaderManager = activity.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCurrentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTION, mCurrentSelection);
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
