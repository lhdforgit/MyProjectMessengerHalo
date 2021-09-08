/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Pagination implements Parcelable {
    @SerializedName("total_count")
    private int totalCount;
    private int count;
    private int offset;
    @SerializedName("next_page")
    private String nextPage;
    @SerializedName("next_cursor")
    private String nextCursor;

    public Pagination() {
    }

    public Pagination(Parcel in) {
        totalCount = in.readInt();
        count = in.readInt();
        offset = in.readInt();
        nextPage = in.readString();
        nextCursor = in.readString();
    }

    /**
     * @return total number of results
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return number of results in the current response
     */
    public int getCount() {
        return count;
    }

    /**
     * @return offset used for current response
     */
    public int getOffset() {
        return offset;
    }


    /**
     * @return next page of results
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * @return a cursor pointing to the next page of results
     */
    public String getNextCursor() {
        return nextCursor;
    }

    public static final Creator<Pagination> CREATOR = new Creator<Pagination>() {
        @Override
        public Pagination createFromParcel(Parcel in) {
            return new Pagination(in);
        }

        @Override
        public Pagination[] newArray(int size) {
            return new Pagination[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(totalCount);
        parcel.writeInt(count);
        parcel.writeInt(offset);
        parcel.writeString(nextPage);
        parcel.writeString(nextCursor);
    }
}
