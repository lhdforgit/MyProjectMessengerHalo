/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    private String id;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("banner_url")
    private String bannerUrl;
    @SerializedName("profile_url")
    private String profileUrl;
    private String username;
    @SerializedName("display_name")
    private String displayName;
    private String twitter;
    @SerializedName("is_public")
    private boolean isPublic;
    @SerializedName("attribution_display_name")
    private String attributionDisplayName;
    private String name;
    private String description;
    @SerializedName("facebook_url")
    private String facebookUrl;
    @SerializedName("twitter_url")
    private String twitterUrl;
    @SerializedName("instagram_url")
    private String instagramUrl;
    @SerializedName("tumblr_url")
    private String tumblrUrl;
    @SerializedName("suppress_chrome")
    private boolean suppressChrome;
    @SerializedName("website_url")
    private String websiteUrl;
    @SerializedName("website_display_url")
    private String websiteDisplayUrl;
    @SerializedName("is_verified")
    private boolean isVerified;

    public User() {
    }

    public User(final Parcel in) {
        id = in.readString();
        avatarUrl = in.readString();
        bannerUrl = in.readString();
        profileUrl = in.readString();
        username = in.readString();
        displayName = in.readString();
        twitter = in.readString();
        isPublic = in.readByte() != 0;
        attributionDisplayName = in.readString();
        name = in.readString();
        description = in.readString();
        facebookUrl = in.readString();
        twitterUrl = in.readString();
        instagramUrl = in.readString();
        tumblrUrl = in.readString();
        suppressChrome = in.readByte() != 0;
        websiteUrl = in.readString();
        websiteDisplayUrl = in.readString();
        isVerified = in.readByte() != 0;
    }

    /**
     * @return user id
     */
    public String getId() {
        return id;
    }

    /**
     * @return avatar url
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @return banner url
     */
    public String getBannerUrl() {
        return bannerUrl;
    }

    /**
     * @return profile url
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return twitter handle
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * @return true if the user is public, false otherwise
     */
    public boolean getIsPublic() {
        return isPublic;
    }

    /**
     * @return attribution display name
     */
    public String getAttributionDisplayName() {
        return attributionDisplayName;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return facebook url
     */
    public String getFacebookUrl() {
        return facebookUrl;
    }

    /**
     * @return twitter url
     */
    public String getTwitterUrl() {
        return twitterUrl;
    }

    /**
     * @return instagram url
     */
    public String getInstagramUrl() {
        return instagramUrl;
    }

    /**
     * @return tumblr url
     */
    public String getTumblrUrl() {
        return tumblrUrl;
    }

    /**
     * @return supress chrome
     */
    public boolean isSuppressChrome() {
        return suppressChrome;
    }

    /**
     * @return website url
     */
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    /**
     * @return displayable url of the website
     */
    public String getWebsiteDisplayUrl() {
        return websiteDisplayUrl;
    }

    public boolean getVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(avatarUrl);
        parcel.writeString(bannerUrl);
        parcel.writeString(profileUrl);
        parcel.writeString(username);
        parcel.writeString(displayName);
        parcel.writeString(twitter);
        parcel.writeByte((byte) (isPublic ? 1 : 0));
        parcel.writeString(attributionDisplayName);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(facebookUrl);
        parcel.writeString(twitterUrl);
        parcel.writeString(instagramUrl);
        parcel.writeString(tumblrUrl);
        parcel.writeByte((byte) (suppressChrome ? 1 : 0));
        parcel.writeString(websiteUrl);
        parcel.writeString(websiteDisplayUrl);
        parcel.writeByte((byte) (isVerified ? 1 : 0));
    }
}
