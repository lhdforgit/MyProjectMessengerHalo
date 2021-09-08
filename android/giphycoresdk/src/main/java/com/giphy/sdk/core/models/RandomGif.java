/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import com.giphy.sdk.core.models.enums.MediaType;
import com.google.gson.annotations.SerializedName;

public class RandomGif {
    private MediaType type;
    private String id;
    private String url;
    @SerializedName("image_original_url")
    private String imageOriginalUrl;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("image_mp4_url")
    private String imageMp4Url;
    @SerializedName("image_frames")
    private int imageFrames;
    @SerializedName("image_width")
    private int imageWidth;
    @SerializedName("image_height")
    private int imageHeight;
    @SerializedName("fixed_height_downsampled_url")
    private String fixedHeightDownsampledUrl;
    @SerializedName("fixed_height_downsampled_width")
    private int fixedHeightDownsampledWidth;
    @SerializedName("fixed_height_downsampled_height")
    private int fixedHeightDownsampledHeight;
    @SerializedName("fixed_width_downsampled_url")
    private String fixedWidthDownsampledUrl;
    @SerializedName("fixed_width_downsampled_width")
    private int fixedWidthDownsampledWidth;
    @SerializedName("fixed_width_downsampled_height")
    private int fixedWidthDownsampledHeight;
    @SerializedName("fixed_height_small_url")
    private String fixedHeightSmallUrl;
    @SerializedName("fixed_height_small_still_url")
    private String fixedHeightSmallStillUrl;
    @SerializedName("fixed_height_small_width")
    private int fixedHeightSmallWidth;
    @SerializedName("fixed_height_small_height")
    private int fixedHeightSmallHeight;
    @SerializedName("fixed_width_small_url")
    private String fixedWidthSmallUrl;
    @SerializedName("fixed_width_small_still_url")
    private String fixedWidthSmallStillUrl;
    @SerializedName("fixed_width_small_width")
    private int fixedWidthSmallWidth;
    @SerializedName("fixed_width_small_height")
    private int fixedWidthSmallHeight;
    private String username;
    private String caption;

    public Media toGif() {
        final Media media = new Media();
        media.setId(id);
        media.setType(type);
        media.setUrl(url);

        media.setUser(new User());
        media.getUser().setUsername(username);

        media.setImages(new Images());

        media.getImages().setOriginal(new Image());
        media.getImages().getOriginal().setGifUrl(imageOriginalUrl);
        media.getImages().getOriginal().setMp4Url(imageMp4Url);
        media.getImages().getOriginal().setFrames(imageFrames);
        media.getImages().getOriginal().setWidth(imageWidth);
        media.getImages().getOriginal().setHeight(imageHeight);

        media.getImages().setFixedHeightDownsampled(new Image());
        media.getImages().getFixedHeightDownsampled().setGifUrl(fixedHeightDownsampledUrl);
        media.getImages().getFixedHeightDownsampled().setWidth(fixedHeightDownsampledWidth);
        media.getImages().getFixedHeightDownsampled().setHeight(fixedHeightDownsampledHeight);

        media.getImages().setFixedWidthDownsampled(new Image());
        media.getImages().getFixedWidthDownsampled().setGifUrl(fixedWidthDownsampledUrl);
        media.getImages().getFixedWidthDownsampled().setWidth(fixedWidthDownsampledWidth);
        media.getImages().getFixedWidthDownsampled().setHeight(fixedWidthDownsampledHeight);

        media.getImages().setFixedHeightSmall(new Image());
        media.getImages().getFixedHeightSmall().setGifUrl(fixedHeightSmallUrl);
        media.getImages().getFixedHeightSmall().setWidth(fixedHeightSmallWidth);
        media.getImages().getFixedHeightSmall().setHeight(fixedHeightSmallHeight);

        media.getImages().setFixedWidthSmall(new Image());
        media.getImages().getFixedWidthSmall().setGifUrl(fixedWidthSmallUrl);
        media.getImages().getFixedWidthSmall().setWidth(fixedWidthSmallWidth);
        media.getImages().getFixedWidthSmall().setHeight(fixedWidthSmallHeight);

        media.getImages().setFixedHeightSmallStill(new Image());
        media.getImages().getFixedHeightSmallStill().setGifUrl(fixedHeightSmallStillUrl);

        media.getImages().setFixedWidthSmallStill(new Image());
        media.getImages().getFixedWidthSmallStill().setGifUrl(fixedWidthSmallStillUrl);

        return media;
    }
}
