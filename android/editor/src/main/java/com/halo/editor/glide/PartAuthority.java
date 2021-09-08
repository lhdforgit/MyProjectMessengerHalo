/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.glide;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.editor.providers.BlobProvider;

import java.io.IOException;
import java.io.InputStream;

public class PartAuthority {

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BlobProvider.AUTHORITY, BlobProvider.PATH, BlobProvider.MATCH);
    }

    public static InputStream getAttachmentThumbnailStream(@NonNull Context context, @NonNull Uri uri)
            throws IOException {
        return getAttachmentStream(context, uri);
    }

    public static InputStream getAttachmentStream(@NonNull Context context, @NonNull Uri uri)
            throws IOException {
        int match = uriMatcher.match(uri);
        try {
            if (match == BlobProvider.MATCH) {
                return BlobProvider.getInstance().getStream(context, uri);
            }
            return context.getContentResolver().openInputStream(uri);
        } catch (SecurityException se) {
            throw new IOException(se);
        }
    }

    public static boolean isLocalUri(final @NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == BlobProvider.MATCH) {
            return true;
        }
        return false;
    }

    public static @Nullable
    String getAttachmentFileName(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        if (match == BlobProvider.MATCH) {
            return BlobProvider.getFileName(uri);
        }
        return null;
    }

    public static @Nullable Long getAttachmentSize(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        if (match == BlobProvider.MATCH) {
            return BlobProvider.getFileSize(uri);
        }
        return null;
    }

    public static @Nullable String getAttachmentContentType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        if (match == BlobProvider.MATCH) {
            return BlobProvider.getMimeType(uri);
        }
        return null;
    }
}
