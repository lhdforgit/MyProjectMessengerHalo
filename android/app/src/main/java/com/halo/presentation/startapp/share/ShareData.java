/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.startapp.share;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.common.base.Optional;
import com.halo.editor.mediasend.Media;

import java.util.ArrayList;
import java.util.List;

class ShareData {

    private final Optional<Uri> uri;
    private final Optional<String> mimeType;
    private final Optional<ArrayList<Media>> media;
    private final boolean external;

    static ShareData forIntentData(@NonNull Uri uri, @NonNull String mimeType, boolean external) {
        return new ShareData(Optional.of(uri), Optional.of(mimeType), Optional.absent(), external);
    }

    static ShareData forPrimitiveTypes() {
        return new ShareData(Optional.absent(), Optional.absent(), Optional.absent(), true);
    }

    static ShareData forMedia(@NonNull List<Media> media) {
        return new ShareData(Optional.absent(), Optional.absent(), Optional.of(new ArrayList<>(media)), true);
    }

    private ShareData(Optional<Uri> uri, Optional<String> mimeType, Optional<ArrayList<Media>> media, boolean external) {
        this.uri = uri;
        this.mimeType = mimeType;
        this.media = media;
        this.external = external;
    }

    boolean isForIntent() {
        return uri.isPresent();
    }

    boolean isForPrimitive() {
        return !uri.isPresent() && !media.isPresent();
    }

    boolean isForMedia() {
        return media.isPresent();
    }

    public @NonNull
    Uri getUri() {
        return uri.get();
    }

    public @NonNull
    String getMimeType() {
        return mimeType.get();
    }

    public @NonNull
    ArrayList<Media> getMedia() {
        return media.isPresent() ? media.get() : new ArrayList<>();
    }

    public boolean isExternal() {
        return external;
    }
}
