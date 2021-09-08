/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Media implements Parcelable {
    private MediaType type;
    private String id;
    private String slug;
    private String url;
    @SerializedName("bitly_gif_url")
    private String bitlyGifUrl;
    @SerializedName("bitly_url")
    private String bitlyUrl;
    @SerializedName("embed_url")
    private String embedUrl;
    private String source;
    private String title;
    private RatingType rating;
    @SerializedName("content_url")
    private String contentUrl;
    private List<String> tags;
    @SerializedName("featured_tags")
    private List<String> featuredTags;
    private User user;
    private Images images;

    @SerializedName("source_tld")
    private String sourceTld;
    @SerializedName("source_post_url")
    private String sourcePostUrl;

    @SerializedName("update_datetime")
    private Date updateDate;
    @SerializedName("create_datetime")
    private Date createDate;
    @SerializedName("import_datetime")
    private Date importDate;
    @SerializedName("trending_datetime")
    private Date trendingDate;

    @SerializedName("is_hidden")
    private boolean isHidden;
    @SerializedName("is_removed")
    private boolean isRemoved;
    @SerializedName("is_community")
    private boolean isCommunity;
    @SerializedName("is_anonymous")
    private boolean isAnonymous;
    @SerializedName("is_featured")
    private boolean isFeatured;
    @SerializedName("is_realtime")
    private boolean isRealtime;
    @SerializedName("is_indexable")
    private boolean isIndexable;
    @SerializedName("is_sticker")
    private boolean isSticker;
    @SerializedName("bottle_data")
    private BottleData bottleData;

    @Nullable
    private transient Bundle userDictionary;

    public Media() {
    }

    public Media(Parcel in) {
        final int mediaTypeOrdinal = in.readInt();
        type = mediaTypeOrdinal != -1 ? MediaType.values()[mediaTypeOrdinal] : null;
        id = in.readString();
        slug = in.readString();
        url = in.readString();
        bitlyGifUrl = in.readString();
        bitlyUrl = in.readString();
        embedUrl = in.readString();
        source = in.readString();
        final int ratingOrdinal = in.readInt();
        rating = ratingOrdinal != -1 ? RatingType.values()[ratingOrdinal] : null;
        contentUrl = in.readString();
        tags = in.createStringArrayList();
        featuredTags = in.createStringArrayList();
        user = in.readParcelable(User.class.getClassLoader());
        images = in.readParcelable(Images.class.getClassLoader());
        sourceTld = in.readString();
        sourcePostUrl = in.readString();

        final long updateDateLong = in.readLong();
        updateDate = updateDateLong != -1 ? new Date(updateDateLong) : null;
        final long createDateLong = in.readLong();
        createDate = createDateLong != -1 ? new Date(createDateLong) : null;
        final long importDateLong = in.readLong();
        importDate = importDateLong != -1 ? new Date(importDateLong) : null;
        final long trendingDateLong = in.readLong();
        trendingDate = trendingDateLong != -1 ? new Date(trendingDateLong) : null;

        isHidden = in.readByte() != 0;
        isRemoved = in.readByte() != 0;
        isCommunity = in.readByte() != 0;
        isAnonymous = in.readByte() != 0;
        isFeatured = in.readByte() != 0;
        isRealtime = in.readByte() != 0;
        isIndexable = in.readByte() != 0;
        isSticker = in.readByte() != 0;

        title = in.readString();

        bottleData = in.readParcelable(BottleData.class.getClassLoader());

        userDictionary = in.readBundle();
    }

    /**
     * @return media type. Can be gif or sticker
     */
    public MediaType getType() {
        return type;
    }

    void setType(MediaType type) {
        this.type = type;
    }

    /**
     * @return id of the object
     */
    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    /**
     * @return slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return bitly version of the url
     */
    public String getBitlyGifUrl() {
        return bitlyGifUrl;
    }

    /**
     * @return bitly version of the gif url
     */
    public String getBitlyUrl() {
        return bitlyUrl;
    }

    /**
     * @return embed url
     */
    public String getEmbedUrl() {
        return embedUrl;
    }

    /**
     * @return source
     */
    public String getSource() {
        return source;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return rating of the gif
     */
    public RatingType getRating() {
        return rating;
    }

    /**
     * @return content url
     */
    public String getContentUrl() {
        return contentUrl;
    }

    /**
     * @return tags associated with the gif
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @return featured tags
     */
    public List<String> getFeaturedTags() {
        return featuredTags;
    }

    /**
     * @return user who uploaded the gif
     */
    public User getUser() {
        return user;
    }

    void setUser(User user) {
        this.user = user;
    }

    /**
     * @return images collection that contains all images types
     */
    public Images getImages() {
        return images;
    }

    void setImages(Images images) {
        this.images = images;
    }

    /**
     * @return source tld
     */
    public String getSourceTld() {
        return sourceTld;
    }

    /**
     * @return source post url
     */
    public String getSourcePostUrl() {
        return sourcePostUrl;
    }

    /**
     * @return date when the gif was updated
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return date when the gif was created
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return date when the gif was imported
     */
    public Date getImportDate() {
        return importDate;
    }

    /**
     * @return date when the gif was trending
     */
    public Date getTrendingDate() {
        return trendingDate;
    }

    /**
     * @return true if gif is hidden, false otherwise
     */
    public boolean getIsHidden() {
        return isHidden;
    }

    /**
     * @return true if this gif was removed, false otherwise
     */
    public boolean getIsRemoved() {
        return isRemoved;
    }

    /**
     * @return true if is comunity gif
     */
    public boolean getIsCommunity() {
        return isCommunity;
    }

    /**
     * @return true if is anonymous
     */
    public boolean getIsAnonymous() {
        return isAnonymous;
    }

    /**
     * @return true if is featured, false otherwise
     */
    public boolean getIsFeatured() {
        return isFeatured;
    }

    /**
     * @return true if realtime
     */
    public boolean getIsRealtime() {
        return isRealtime;
    }

    /**
     * @return true if indexable
     */
    public boolean getIsIndexable() {
        return isIndexable;
    }

    /**
     * @return true if sticker, false otherwise
     */
    public boolean getIsSticker() {
        return isSticker;
    }

    /**
     * @return bottleData
     */
    public BottleData getBottleData() {
        return bottleData;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setBottleData(BottleData bottleData) {
        this.bottleData = bottleData;
    }

    /**
     * @return tid
     */
    public String getTID() {
        return bottleData != null ? bottleData.getTid() : null;
    }

    /**
     * @return userDictionary
     */
    @Nullable
    public Bundle getUserDictionary() {
        return userDictionary;
    }

    /**
     * Set userDictionary
     */
    public void setUserDictionary(@Nullable Bundle bundle) {
        userDictionary = bundle;
    }

    /**
     * Passed down the media id to the @images field and call postProcess function on @images field
     */
    public void postProcess() {
        if (images != null) {
            images.setMediaId(id);
            images.postProcess();
        }
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type != null ? type.ordinal() : -1);
        parcel.writeString(id);
        parcel.writeString(slug);
        parcel.writeString(url);
        parcel.writeString(bitlyGifUrl);
        parcel.writeString(bitlyUrl);
        parcel.writeString(embedUrl);
        parcel.writeString(source);
        parcel.writeInt(rating != null ? rating.ordinal() : -1);
        parcel.writeString(contentUrl);
        parcel.writeStringList(tags);
        parcel.writeStringList(featuredTags);
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(images, i);
        parcel.writeString(sourceTld);
        parcel.writeString(sourcePostUrl);

        parcel.writeLong(updateDate != null ? updateDate.getTime() : -1);
        parcel.writeLong(createDate != null ? createDate.getTime() : -1);
        parcel.writeLong(importDate != null ? importDate.getTime() : -1);
        parcel.writeLong(trendingDate != null ? trendingDate.getTime() : -1);

        parcel.writeByte((byte) (isHidden ? 1 : 0));
        parcel.writeByte((byte) (isRemoved ? 1 : 0));
        parcel.writeByte((byte) (isCommunity ? 1 : 0));
        parcel.writeByte((byte) (isAnonymous ? 1 : 0));
        parcel.writeByte((byte) (isFeatured ? 1 : 0));
        parcel.writeByte((byte) (isRealtime ? 1 : 0));
        parcel.writeByte((byte) (isIndexable ? 1 : 0));
        parcel.writeByte((byte) (isSticker ? 1 : 0));

        parcel.writeString(title);

        parcel.writeParcelable(bottleData, i);

        parcel.writeBundle(userDictionary);
    }
}
