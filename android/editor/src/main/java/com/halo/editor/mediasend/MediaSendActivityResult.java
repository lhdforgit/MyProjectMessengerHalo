/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.mediasend;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.halo.editor.util.MediaUtil;
import com.halo.editor.util.ParcelUtil;

import java.util.Collection;

/**
 * A class that lets us nicely format data that we'll send back to previous activity.
 */
public class MediaSendActivityResult implements Parcelable {

    private Collection<Media> uploadedMedia;

    public @NonNull static MediaSendActivityResult createResult(Collection<Media> medias) {
        MediaSendActivityResult mediaSendActivityResult = new MediaSendActivityResult();
        mediaSendActivityResult.setUploadedMedia(medias);
        return mediaSendActivityResult;
    }

    public MediaSendActivityResult() {
    }

    public MediaSendActivityResult(Collection<Media> uploadedMedia) {
        this.uploadedMedia = uploadedMedia;
    }

    private MediaSendActivityResult(Parcel in) {
        this.uploadedMedia = ParcelUtil.readParcelableCollection(in, Media.class);
    }

    public Collection<Media> getUploadedMedia() {
        return uploadedMedia;
    }

    public Boolean haveVideo() {
        boolean have = false;
        if (uploadedMedia != null && !uploadedMedia.isEmpty()) {
            for (Media m : uploadedMedia) {
                if (MediaUtil.isVideoType(m.getMimeType())) {
                    have = true;
                }
            }
        }
        return have;
    }

    public void setUploadedMedia(Collection<Media> uploadedMedia) {
        this.uploadedMedia = uploadedMedia;
    }

    public static final Creator<MediaSendActivityResult> CREATOR = new Creator<MediaSendActivityResult>() {
        @Override
        public MediaSendActivityResult createFromParcel(Parcel in) {
            return new MediaSendActivityResult(in);
        }

        @Override
        public MediaSendActivityResult[] newArray(int size) {
            return new MediaSendActivityResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtil.writeParcelableCollection(dest, uploadedMedia);
    }

    @Override
    public String toString() {
        return "MediaSendActivityResult{" +
                "uploadedMedia=" + uploadedMedia +
                '}';
    }
}
