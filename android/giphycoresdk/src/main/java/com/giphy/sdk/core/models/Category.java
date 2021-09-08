/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category implements Parcelable {
    private String name;
    @SerializedName("name_encoded")
    private String nameEncoded;
    private Media gif;
    @SerializedName("subcategories")
    private List<Category> subCategories;
    private String encodedPath;

    public Category() {
    }

    public Category(Parcel in) {
        name = in.readString();
        nameEncoded = in.readString();
        gif = in.readParcelable(Media.class.getClassLoader());
        subCategories = in.createTypedArrayList(Category.CREATOR);
        encodedPath = in.readString();
    }

    public Category(String name, String nameEncoded) {
        this.name = name;
        this.nameEncoded = nameEncoded;
    }

    public Category(String name, String nameEncoded, Media gif) {
        this.name = name;
        this.nameEncoded = nameEncoded;
        this.gif = gif;
    }

    /**
     * @return category name
     */
    public String getName() {
        return name;
    }

    /**
     * @return encoded category name, used for constructing the URL to fetch gifs
     */
    public String getNameEncoded() {
        return nameEncoded;
    }

    /**
     * @return preview gif of the category
     */
    public Media getGif() {
        return gif;
    }

    /**
     * @return subcategories of the category
     */
    public List<Category> getSubCategories() {
        return subCategories;
    }

    /**
     * @return URL Encoded path of the Category (to make sure we have the full-path for subcategories).
     */
    public String getEncodedPath() {
        return encodedPath;
    }

    public void setEncodedPath(String encodedPath) {
        this.encodedPath = encodedPath;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(nameEncoded);
        parcel.writeParcelable(gif, i);
        parcel.writeTypedList(subCategories);
        parcel.writeString(encodedPath);
    }
}
