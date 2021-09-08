/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.entities;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.halo.R;
import com.halo.presentation.gallery.loader.AlbumLoader;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Nullable
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    public static final String ALBUM_ID_ALL = String.valueOf(-1);
    public static final String ALBUM_NAME_ALL = "All";

    private String mId;
    private String mDisplayName;
    private long mCount;

    public Album(String id, String albumName, long count) {
        mId = id;
        mDisplayName = albumName;
        mCount = count;
    }

    private Album(Parcel source) {
        mId = source.readString();
        mDisplayName = source.readString();
        mCount = source.readLong();
    }

    public Album() {
    }

    /**
     * Constructs a new {@link Album} entity from the {@link Cursor}.
     * This method is not responsible for managing cursor resource, such as close, iterate, and so on.
     */
    public static Album valueOf(Cursor cursor) {
        if (cursor == null) {
            return new Album();
        }
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mDisplayName);
        dest.writeLong(mCount);
    }

    public String getId() {
        return mId;
    }

    public long getCount() {
        return mCount;
    }

    public void addCaptureCount() {
        mCount++;
    }

    public String getDisplayName(Context context) {
        if (isAll()) {
            return context.getString(R.string.album_name_all);
        }
        return mDisplayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public boolean isAll() {
        return ALBUM_ID_ALL.equals(mId);
    }

    public boolean isEmpty() {
        return mCount == 0;
    }
}