/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.google.gson.annotations.SerializedName;

public class Images implements Parcelable {
    @SerializedName("fixed_height")
    private Image fixedHeight;
    @SerializedName("fixed_height_still")
    private Image fixedHeightStill;
    @SerializedName("fixed_height_downsampled")
    private Image fixedHeightDownsampled;
    @SerializedName("fixed_width")
    private Image fixedWidth;
    @SerializedName("fixed_width_still")
    private Image fixedWidthStill;
    @SerializedName("fixed_width_downsampled")
    private Image fixedWidthDownsampled;
    @SerializedName("fixed_height_small")
    private Image fixedHeightSmall;
    @SerializedName("fixed_height_small_still")
    private Image fixedHeightSmallStill;
    @SerializedName("fixed_width_small")
    private Image fixedWidthSmall;
    @SerializedName("fixed_width_small_still")
    private Image fixedWidthSmallStill;
    private Image downsized;
    @SerializedName("downsized_still")
    private Image downsizedStill;
    @SerializedName("downsized_large")
    private Image downsizedLarge;
    @SerializedName("downsized_medium")
    private Image downsizedMedium;
    private Image original;
    @SerializedName("original_still")
    private Image originalStill;
    private Image looping;
    private Image preview;
    @SerializedName("downsized_small")
    private Image downsizedSmall;

    private String mediaId;

    public Images() {
    }

    public Images(Parcel in) {
        fixedHeight = in.readParcelable(Image.class.getClassLoader());
        fixedHeightStill = in.readParcelable(Image.class.getClassLoader());
        fixedHeightDownsampled = in.readParcelable(Image.class.getClassLoader());
        fixedWidth = in.readParcelable(Image.class.getClassLoader());
        fixedWidthStill = in.readParcelable(Image.class.getClassLoader());
        fixedWidthDownsampled = in.readParcelable(Image.class.getClassLoader());
        fixedHeightSmall = in.readParcelable(Image.class.getClassLoader());
        fixedHeightSmallStill = in.readParcelable(Image.class.getClassLoader());
        fixedWidthSmall = in.readParcelable(Image.class.getClassLoader());
        fixedWidthSmallStill = in.readParcelable(Image.class.getClassLoader());
        downsized = in.readParcelable(Image.class.getClassLoader());
        downsizedStill = in.readParcelable(Image.class.getClassLoader());
        downsizedLarge = in.readParcelable(Image.class.getClassLoader());
        downsizedMedium = in.readParcelable(Image.class.getClassLoader());
        original = in.readParcelable(Image.class.getClassLoader());
        originalStill = in.readParcelable(Image.class.getClassLoader());
        looping = in.readParcelable(Image.class.getClassLoader());
        preview = in.readParcelable(Image.class.getClassLoader());
        downsizedSmall = in.readParcelable(Image.class.getClassLoader());
        mediaId = in.readString();
    }

    /**
     * Height set to 200px. Good for mobile use
     */
    public Image getFixedHeight() {
        return fixedHeight;
    }

    /**
     * Static preview image for fixed_height
     */
    public Image getFixedHeightStill() {
        return fixedHeightStill;
    }

    /**
     * Height set to 200px. Reduced to 6 frames to minimize file size to the lowest.
     * Works well for unlimited scroll on mobile and as animated previews. See Giphy.com on mobile web as an example.
     */
    public Image getFixedHeightDownsampled() {
        return fixedHeightDownsampled;
    }

    void setFixedHeightDownsampled(Image fixedHeightDownsampled) {
        this.fixedHeightDownsampled = fixedHeightDownsampled;
    }

    /**
     * Width set to 200px. Good for mobile use
     */
    public Image getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Static preview image for fixed_width
     */
    public Image getFixedWidthStill() {
        return fixedWidthStill;
    }

    /**
     * Width set to 200px. Reduced to 6 frames. Works well for unlimited scroll on mobile and as animated previews.
     */
    public Image getFixedWidthDownsampled() {
        return fixedWidthDownsampled;
    }

    void setFixedWidthDownsampled(Image fixedWidthDownsampled) {
        this.fixedWidthDownsampled = fixedWidthDownsampled;
    }

    /**
     * Height set to 100px. Good for mobile keyboards
     */
    public Image getFixedHeightSmall() {
        return fixedHeightSmall;
    }

    void setFixedHeightSmall(Image fixedHeightSmall) {
        this.fixedHeightSmall = fixedHeightSmall;
    }

    /**
     * Static preview image for fixed_height_small
     */
    public Image getFixedHeightSmallStill() {
        return fixedHeightSmallStill;
    }

    void setFixedHeightSmallStill(Image fixedHeightSmallStill) {
        this.fixedHeightSmallStill = fixedHeightSmallStill;
    }

    /**
     * Width set to 100px. Good for mobile keyboards
     */
    public Image getFixedWidthSmall() {
        return fixedWidthSmall;
    }

    void setFixedWidthSmall(Image fixedWidthSmall) {
        this.fixedWidthSmall = fixedWidthSmall;
    }

    /**
     * Static preview image for fixed_width_small
     */
    public Image getFixedWidthSmallStill() {
        return fixedWidthSmallStill;
    }

    void setFixedWidthSmallStill(Image fixedWidthSmallStill) {
        this.fixedWidthSmallStill = fixedWidthSmallStill;
    }

    /**
     * File size under 2mb
     */
    public Image getDownsized() {
        return downsized;
    }

    /**
     * Static preview image for downsized
     */
    public Image getDownsizedStill() {
        return downsizedStill;
    }

    /**
     * File size under 8mb
     */
    public Image getDownsizedLarge() {
        return downsizedLarge;
    }

    /**
     * File size under 5mb
     */
    public Image getDownsizedMedium() {
        return downsizedMedium;
    }

    /**
     * Original file size and file dimensions. Good for desktop use
     */
    public Image getOriginal() {
        return original;
    }

    void setOriginal(Image original) {
        this.original = original;
    }

    /**
     * Preview image for original
     */
    public Image getOriginalStill() {
        return originalStill;
    }

    /**
     * Duration set to loop for 15 seconds. Only recommended for this exact use case
     */
    public Image getLooping() {
        return looping;
    }

    /**
     * File size under 50kb. Duration may be truncated to meet file size requirements. Good for thumbnails and previews.
     */
    public Image getPreview() {
        return preview;
    }

    /**
     * File size under 200kb
     */
    public Image getDownsizedSmall() {
        return downsizedSmall;
    }

    /**
     * ID of the Represented Object
     */
    public String getMediaId() {
        return mediaId;
    }

    void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    /**
     * Passed down the rendition type and media id to each image
     */
    void postProcess() {
        if (original != null) {
            original.setMediaId(mediaId);
            original.setRenditionType(RenditionType.original);
        }
        if (originalStill != null) {
            originalStill.setMediaId(mediaId);
            originalStill.setRenditionType(RenditionType.originalStill);
        }
        if (fixedHeight != null) {
            fixedHeight.setMediaId(mediaId);
            fixedHeight.setRenditionType(RenditionType.fixedHeight);
        }
        if (fixedHeightStill != null) {
            fixedHeightStill.setMediaId(mediaId);
            fixedHeightStill.setRenditionType(RenditionType.fixedHeightStill);
        }
        if (fixedHeightDownsampled != null) {
            fixedHeightDownsampled.setMediaId(mediaId);
            fixedHeightDownsampled.setRenditionType(RenditionType.fixedHeightDownsampled);
        }
        if (fixedWidth != null) {
            fixedWidth.setMediaId(mediaId);
            fixedWidth.setRenditionType(RenditionType.fixedWidth);
        }
        if (fixedWidthStill != null) {
            fixedWidthStill.setMediaId(mediaId);
            fixedWidthStill.setRenditionType(RenditionType.fixedWidthStill);
        }
        if (fixedWidthDownsampled != null) {
            fixedWidthDownsampled.setMediaId(mediaId);
            fixedWidthDownsampled.setRenditionType(RenditionType.fixedWidthDownsampled);
        }
        if (fixedHeightSmall != null) {
            fixedHeightSmall.setMediaId(mediaId);
            fixedHeightSmall.setRenditionType(RenditionType.fixedHeightSmall);
        }
        if (fixedHeightSmallStill != null) {
            fixedHeightSmallStill.setMediaId(mediaId);
            fixedHeightSmallStill.setRenditionType(RenditionType.fixedHeightSmallStill);
        }
        if (fixedWidthSmall != null) {
            fixedWidthSmall.setMediaId(mediaId);
            fixedWidthSmall.setRenditionType(RenditionType.fixedWidthSmall);
        }
        if (fixedWidthSmallStill != null) {
            fixedWidthSmallStill.setMediaId(mediaId);
            fixedWidthSmallStill.setRenditionType(RenditionType.fixedWidthSmallStill);
        }
        if (downsized != null) {
            downsized.setMediaId(mediaId);
            downsized.setRenditionType(RenditionType.downsized);
        }
        if (downsizedStill != null) {
            downsizedStill.setMediaId(mediaId);
            downsizedStill.setRenditionType(RenditionType.downsizedStill);
        }
        if (downsizedLarge != null) {
            downsizedLarge.setMediaId(mediaId);
            downsizedLarge.setRenditionType(RenditionType.downsizedLarge);
        }
        if (downsizedMedium != null) {
            downsizedMedium.setMediaId(mediaId);
            downsizedMedium.setRenditionType(RenditionType.downsizedMedium);
        }
        if (looping != null) {
            looping.setMediaId(mediaId);
            looping.setRenditionType(RenditionType.looping);
        }
        if (preview != null) {
            preview.setMediaId(mediaId);
            preview.setRenditionType(RenditionType.preview);
        }
        if (downsizedSmall != null) {
            downsizedSmall.setMediaId(mediaId);
            downsizedSmall.setRenditionType(RenditionType.downsizedSmall);
        }
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(fixedHeight, i);
        parcel.writeParcelable(fixedHeightStill, i);
        parcel.writeParcelable(fixedHeightDownsampled, i);
        parcel.writeParcelable(fixedWidth, i);
        parcel.writeParcelable(fixedWidthStill, i);
        parcel.writeParcelable(fixedWidthDownsampled, i);
        parcel.writeParcelable(fixedHeightSmall, i);
        parcel.writeParcelable(fixedHeightSmallStill, i);
        parcel.writeParcelable(fixedWidthSmall, i);
        parcel.writeParcelable(fixedWidthSmallStill, i);
        parcel.writeParcelable(downsized, i);
        parcel.writeParcelable(downsizedStill, i);
        parcel.writeParcelable(downsizedLarge, i);
        parcel.writeParcelable(downsizedMedium, i);
        parcel.writeParcelable(original, i);
        parcel.writeParcelable(originalStill, i);
        parcel.writeParcelable(looping, i);
        parcel.writeParcelable(preview, i);
        parcel.writeParcelable(downsizedSmall, i);
        parcel.writeString(mediaId);
    }
}
