/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TermSuggestion implements Parcelable {
    @SerializedName("name")
    private String term;

    public TermSuggestion() {
    }

    public TermSuggestion(Parcel in) {
        term = in.readString();
    }

    /**
     * @return term suggestion
     */
    public String getTerm() {
        return term;
    }

    public static final Creator<TermSuggestion> CREATOR = new Creator<TermSuggestion>() {
        @Override
        public TermSuggestion createFromParcel(Parcel in) {
            return new TermSuggestion(in);
        }

        @Override
        public TermSuggestion[] newArray(int size) {
            return new TermSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(term);
    }
}
