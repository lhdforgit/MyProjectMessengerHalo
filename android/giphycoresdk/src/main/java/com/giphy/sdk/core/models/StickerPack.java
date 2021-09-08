/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.giphy.sdk.core.models.enums.MediaType;
import com.google.gson.annotations.SerializedName;

public class StickerPack implements Parcelable {
    private String id;
    @SerializedName("display_name")
    private String displayName;
    private String parent;
    private String slug;
    private String type;
    @SerializedName("content_type")
    private MediaType contentType;
    @SerializedName("short_display_name")
    private String shortDisplayName;
    private String description;
    @SerializedName("has_children")
    private boolean hasChildren;
    private User user;
    @SerializedName("featured_gif")
    private Media featuredGif;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParent() {
        return parent;
    }

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public User getUser() {
        return user;
    }

    public Media getFeaturedGif() {
        return featuredGif;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.displayName);
        dest.writeString(this.parent);
        dest.writeString(this.slug);
        dest.writeString(this.type);
        dest.writeInt(this.contentType == null ? -1 : this.contentType.ordinal());
        dest.writeString(this.shortDisplayName);
        dest.writeString(this.description);
        dest.writeByte(this.hasChildren ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.featuredGif, flags);
    }

    public StickerPack() {
    }

    public StickerPack(Parcel in) {
        this.id = in.readString();
        this.displayName = in.readString();
        this.parent = in.readString();
        this.slug = in.readString();
        this.type = in.readString();
        int tmpContentType = in.readInt();
        this.contentType = tmpContentType == -1 ? null : MediaType.values()[tmpContentType];
        this.shortDisplayName = in.readString();
        this.description = in.readString();
        this.hasChildren = in.readByte() != 0;
        this.user = in.readParcelable(User.class.getClassLoader());
        this.featuredGif = in.readParcelable(Media.class.getClassLoader());
    }

    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel source) {
            return new StickerPack(source);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };

    public void updatePackName() {


    }
}
