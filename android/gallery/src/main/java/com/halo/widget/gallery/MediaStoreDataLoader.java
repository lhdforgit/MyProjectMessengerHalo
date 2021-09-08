/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.gallery;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ngannd
 * Create by ngannd on 11/12/2018
 */
public class MediaStoreDataLoader extends AsyncTaskLoader<List<MediaStoreData>> {

    @SuppressLint("InlinedApi")
    private static final String[] IMAGE_PROJECTION =
            new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.MIME_TYPE,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.WIDTH,
                    MediaStore.Images.ImageColumns.HEIGHT
            };

    @SuppressLint("InlinedApi")
    private static final String[] VIDEO_PROJECTION =
            new String[]{
                    MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.MIME_TYPE,
                    MediaStore.Video.VideoColumns.SIZE,
                    MediaStore.Video.VideoColumns.WIDTH,
                    MediaStore.Video.VideoColumns.HEIGHT
            };

    private List<MediaStoreData> cached;
    private boolean observerRegistered = false;
    private final ForceLoadContentObserver forceLoadContentObserver = new ForceLoadContentObserver();

    private int limit = 0;
    // kb
    private long sizeLimit = 0;

    public MediaStoreDataLoader(Context context) {
        super(context);
    }

    public MediaStoreDataLoader(Context context, int limit) {
        this(context);
        this.limit = limit;
    }

    public MediaStoreDataLoader(Context context, int limit, long sizeLimit) {
        this(context, limit);
        this.sizeLimit = sizeLimit;
    }

    public MediaStoreDataLoader(Context context, long sizeLimit) {
        this(context);
        this.sizeLimit = sizeLimit;
    }

    @Override
    public void deliverResult(List<MediaStoreData> data) {
        if (!isReset() && isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (cached != null) {
            deliverResult(cached);
        }
        if (takeContentChanged() || cached == null) {
            forceLoad();
        }
        registerContentObserver();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();
        cached = null;
        unregisterContentObserver();
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        unregisterContentObserver();
    }

    @Override
    public List<MediaStoreData> loadInBackground() {
        List<MediaStoreData> data = queryImages();
        data.addAll(queryVideos());
        Collections.sort(data, (mediaStoreData, mediaStoreData2) ->
                Long.compare(mediaStoreData2.dateTaken, mediaStoreData.dateTaken));
        return data;
    }

    private List<MediaStoreData> queryImages() {
        return query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                MediaStoreData.Type.IMAGE);
    }

    private List<MediaStoreData> queryVideos() {
        return query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                MediaStoreData.Type.VIDEO);
    }

    private List<MediaStoreData> query(Uri contentUri, String[] projection,
                                       MediaStoreData.Type type) {
        final List<MediaStoreData> data = new ArrayList<>();
        @SuppressLint("InlinedApi")
        Cursor cursor = getContext().getContentResolver()
                .query(contentUri, projection, null, null,
                        MediaStore.MediaColumns.DATE_TAKEN + " DESC" + (limit > 0 ? " LIMIT " + limit : ""));

        if (cursor == null) {
            return data;
        }

        try {
            final int idColNum = cursor.getColumnIndexOrThrow(android.provider.BaseColumns._ID);
            @SuppressLint("InlinedApi") final int dateTakenColNum = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN);
            final int dateModifiedColNum = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
            final int mimeTypeColNum = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            final int sizeColNum = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
            final int widthColNum = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH);
            final int heightColNum = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColNum);
                long dateTaken = cursor.getLong(dateTakenColNum);
                String mimeType = cursor.getString(mimeTypeColNum);
                long dateModified = cursor.getLong(dateModifiedColNum);
                long size = cursor.getLong(sizeColNum);
                int width = cursor.getInt(widthColNum);
                int height = cursor.getInt(heightColNum);

                if (sizeLimit == 0 || size <= sizeLimit) {
                    data.add(new MediaStoreData(id, Uri.withAppendedPath(contentUri, Long.toString(id)),
                            mimeType, dateTaken, dateModified, type, width, height, size));
                }
            }
        } finally {
            cursor.close();
        }

        return data;
    }

    private void registerContentObserver() {
        if (!observerRegistered) {
            ContentResolver cr = getContext().getContentResolver();
            cr.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false,
                    forceLoadContentObserver);
            cr.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false,
                    forceLoadContentObserver);

            observerRegistered = true;
        }
    }

    private void unregisterContentObserver() {
        if (observerRegistered) {
            observerRegistered = false;

            getContext().getContentResolver().unregisterContentObserver(forceLoadContentObserver);
        }
    }
}
