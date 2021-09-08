/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.entities;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.halo.presentation.gallery.utils.MimeType;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public class Item implements Parcelable {
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        @Nullable
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    public static final long ITEM_ID_CAPTURE = -1;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "Capture";
    public final long id;
    public final String mimeType;
    public Uri uri;
    public final long size;
    public final long duration; // only for video, in ms
    public String description;
    public String path;
    public String uploadId;
    public int width;
    public int height;

    public Item(long id, String mimeType, long size, long duration) {
        this.id = id;
        this.mimeType = mimeType;
        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // ?
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
    }

    public Item(long id, Uri uri, String mimeType) {
        this.id = id;
        this.mimeType = mimeType;
        this.uri = uri;
        size = -1;
        duration = -1;
    }

    public Item(Uri uri, String mimeType) {
        this.id = 0L;
        this.mimeType = mimeType;
        this.uri = uri;
        size = -1;
        duration = -1;
    }

    public Item(long id, Uri uri, String mimeType, long size, int width, int height) {
        this.id = id;
        this.mimeType = mimeType;
        this.uri = uri;
        this.size = size;
        duration = -1;
        this.width = width;
        this.height = height;
    }

    /**
     * Item data for editor from Share Receiving data
     *
     * @param id       id of media
     * @param path     path of media
     * @param uri      uri of media
     * @param mimeType mime type of media
     */
    public Item(long id, String path, Uri uri, String mimeType) {
        this.id = id;
        this.mimeType = mimeType;
        this.path = path;
        this.uri = uri;
        size = -1;
        duration = -1;
    }

    private Item(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        uri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
        duration = source.readLong();
        description = source.readString();
        path = source.readString();
        uploadId = source.readString();
    }

    public static Item valueOf(Cursor cursor) {
        return new Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeParcelable(uri, 0);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeString(description);
        dest.writeString(path);
        dest.writeString(uploadId);
    }

    public Uri getContentUri() {
        return uri;
    }

    public long getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public String getDescription() {
        return Strings.nullToEmpty(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public boolean isCapture() {
        return id == ITEM_ID_CAPTURE;
    }

    public boolean isImage() {
        if (mimeType == null) return false;
        return mimeType.equals(MimeType.JPEG.toString())
                || mimeType.equals(MimeType.PNG.toString())
                || mimeType.equals(MimeType.GIF.toString())
                || mimeType.equals(MimeType.BMP.toString())
                || mimeType.equals(MimeType.WEBP.toString());
    }

    public boolean isGif() {
        if (mimeType == null) return false;
        return mimeType.equals(MimeType.GIF.toString());
    }

    public boolean isVideo() {
        if (mimeType == null) return false;
        return mimeType.equals(MimeType.MPEG.toString())
                || mimeType.equals(MimeType.MP4.toString())
                || mimeType.equals(MimeType.QUICKTIME.toString())
                || mimeType.equals(MimeType.THREEGPP.toString())
                || mimeType.equals(MimeType.THREEGPP2.toString())
                || mimeType.equals(MimeType.MKV.toString())
                || mimeType.equals(MimeType.WEBM.toString())
                || mimeType.equals(MimeType.TS.toString())
                || mimeType.equals(MimeType.AVI.toString())
                // Receiving simple data from other apps
                || mimeType.startsWith("video/");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }

        Item other = (Item) obj;
        return id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType)
                || (mimeType == null && other.mimeType == null))
                && (uri != null && uri.equals(other.uri)
                || (uri == null && other.uri == null))
                && (path != null && path.equals(other.path)
                || (path == null && other.path == null))
                && size == other.size
                && duration == other.duration;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode();
        }
        if (uri != null) {
            result = 31 * result + uri.hashCode();
        }
        if (path != null) {
            result = 31 * result + path.hashCode();
        }
        result = 31 * result + Long.valueOf(size).hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", mimeType='" + mimeType + '\'' +
                ", uri=" + uri +
                ", size=" + size +
                ", duration=" + duration +
                ", description='" + (description != null ? description : "") + '\'' +
                '}';
    }
}
