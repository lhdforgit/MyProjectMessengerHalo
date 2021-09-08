/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.gallery;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ngannd
 * Create by ngannd on 11/12/2018
 * <p>
 * A data model containing data for a single media item.
 */
public class MediaStoreData implements Parcelable {

    public static final Creator<MediaStoreData> CREATOR = new Creator<MediaStoreData>() {
        @Override
        public MediaStoreData createFromParcel(Parcel parcel) {
            return new MediaStoreData(parcel);
        }

        @Override
        public MediaStoreData[] newArray(int i) {
            return new MediaStoreData[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    final long rowId;
    final Uri uri;
    final String mimeType;
    final long dateModified;
    private final Type type;
    final long dateTaken;
    final int width;
    final int height;
    final long size;

    public MediaStoreData(long rowId, Uri uri, String mimeType, long dateTaken, long dateModified,
                          Type type, int width, int height, long size) {
        this.rowId = rowId;
        this.uri = uri;
        this.dateModified = dateModified;
        this.mimeType = mimeType;
        this.type = type;
        this.dateTaken = dateTaken;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    private MediaStoreData(Parcel in) {
        rowId = in.readLong();
        uri = Uri.parse(in.readString());
        mimeType = in.readString();
        dateTaken = in.readLong();
        dateModified = in.readLong();
        type = Type.valueOf(in.readString());
        width = in.readInt();
        height = in.readInt();
        size = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(rowId);
        parcel.writeString(uri.toString());
        parcel.writeString(mimeType);
        parcel.writeLong(dateTaken);
        parcel.writeLong(dateModified);
        parcel.writeString(type.name());
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeLong(size);
    }

    @Override
    public String toString() {
        return "MediaStoreData{" +
                "rowId=" + rowId +
                ", uri=" + uri +
                ", mimeType='" + mimeType + '\'' +
                ", dateModified=" + dateModified +
                ", type=" + type +
                ", dateTaken=" + dateTaken +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                '}';
    }

    /**
     * The type of data.
     */
    public enum Type {
        VIDEO,
        IMAGE,
    }

    public long getRowId() {
        return rowId;
    }

    public Uri getUri() {
        return uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getDateModified() {
        return dateModified;
    }

    public Type getType() {
        return type;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSize() {
        return size;
    }
}