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

import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumMediaCallbacks mCallbacks;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }

        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            throw new IllegalArgumentException("Album can not be null");
        }

        return AlbumMediaLoader.newInstance(context, album,
                album.isAll() && args.getBoolean(ARGS_ENABLE_CAPTURE, false));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumMediaLoad(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumMediaReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull AlbumMediaCallbacks callbacks) {
        mContext = new WeakReference<>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void load(@NonNull Album target) {
        load(target, false);
    }

    public void load(@NonNull Album target, boolean enableCapture) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public interface AlbumMediaCallbacks {

        void onAlbumMediaLoad(Cursor cursor);

        void onAlbumMediaReset();
    }
}
