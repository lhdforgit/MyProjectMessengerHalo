/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.entities.SelectionSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 * Load all albums (grouped by bucket_id) into a single cursor.
 */
public class AlbumLoader extends CursorLoader {
    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    //public static final String COLUMN_URI = "uri";
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            //COLUMN_URI,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS " + COLUMN_COUNT};

    private static final String[] PROJECTION_29 = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE};

    // === params for showSingleMediaType: false ===
    private static final String SELECTION =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";
    private static final String SELECTION_29 =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    // =============================================

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE_29 =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }
    // =============================================

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_SINGLE_MEDIA_GIF_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") GROUP BY (bucket_id";
    private static final String SELECTION_FOR_SINGLE_MEDIA_GIF_TYPE_29 =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + "=?";

    private static String[] getSelectionArgsForSingleMediaGifType(int mediaType) {
        return new String[]{String.valueOf(mediaType), "image/gif"};
    }
    // =============================================

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(
                context,
                QUERY_URI,
                android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? PROJECTION : PROJECTION_29,
                selection,
                selectionArgs,
                BUCKET_ORDER_BY
        );
    }

    public static CursorLoader newInstance(Context context) {
        String selection;
        String[] selectionArgs;
        if (SelectionSpec.getInstance().onlyShowImages()) {
            selection = android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? SELECTION_FOR_SINGLE_MEDIA_TYPE : SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        } else if (SelectionSpec.getInstance().onlyShowVideos()) {
            selection = android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? SELECTION_FOR_SINGLE_MEDIA_TYPE : SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        } else {
            selection = android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? SELECTION : SELECTION_29;
            selectionArgs = SELECTION_ARGS;
        }
        return new AlbumLoader(context, selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            int totalCount = 0;
            //Uri allAlbumCoverUri = null;
            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            if (albums != null) {
                while (albums.moveToNext()) {
                    long fileId = albums.getLong(albums.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));
                    String bucketDisplayName = albums.getString(albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                    String mimeType = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                    //Uri uri = getUri(albums);
                    int count = albums.getInt(albums.getColumnIndex(COLUMN_COUNT));

                    otherAlbums.addRow(new String[]{Long.toString(fileId), Long.toString(bucketId), bucketDisplayName, mimeType, /*uri.toString(),*/
                            String.valueOf(count)});
                    totalCount += count;
                }
                /*if (albums.moveToFirst()) {
                    allAlbumCoverUri = getUri(albums);
                }*/
            }

            allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, null,
                    //allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                    String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        } else {
            int totalCount = 0;
            //Uri allAlbumCoverUri = null;

            // Pseudo GROUP BY
            @SuppressLint("UseSparseArrays")
            Map<Long, Long> countMap = new HashMap<>();
            if (albums != null) {
                while (albums.moveToNext()) {
                    long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));

                    Long count = countMap.get(bucketId);
                    if (count == null) {
                        count = 1L;
                    } else {
                        count++;
                    }
                    countMap.put(bucketId, count);
                }
            }

            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            if (albums != null) {
                if (albums.moveToFirst()) {
                    //allAlbumCoverUri = getUri(albums);

                    Set<Long> done = new HashSet<>();

                    do {
                        long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));

                        if (done.contains(bucketId)) {
                            continue;
                        }

                        long fileId = albums.getLong(albums.getColumnIndex(MediaStore.Files.FileColumns._ID));
                        String bucketDisplayName = albums.getString(albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                        String mimeType = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                        //Uri uri = getUri(albums);
                        long count = countMap.get(bucketId);

                        otherAlbums.addRow(new String[]{Long.toString(fileId), Long.toString(bucketId), bucketDisplayName, mimeType, /*uri.toString(),*/
                                String.valueOf(count)});
                        done.add(bucketId);

                        totalCount += count;
                    } while (albums.moveToNext());
                }
            }

            allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, null,
                    /*allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),*/
                    String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        }
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}