/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils.glide;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.halo.common.utils.ThumbImageUtils;
import com.halo.data.entities.media.MediaEntity;

import java.io.InputStream;

/**
 * @author ngannd
 * Create by ngannd on 24/01/2019
 */
public class MediaModelLoader extends BaseGlideUrlLoader<MediaEntity> {

    public static class Factory implements ModelLoaderFactory<MediaEntity, InputStream> {
        private final ModelCache<MediaEntity, GlideUrl> modelCache = new ModelCache<>(500);

        @NonNull
        @Override
        public ModelLoader<MediaEntity, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new MediaModelLoader(
                    multiFactory.build(GlideUrl.class, InputStream.class), modelCache);
        }

        @Override
        public void teardown() {
        }
    }

    private MediaModelLoader(ModelLoader<GlideUrl, InputStream> urlLoader, ModelCache<MediaEntity, GlideUrl> modelCache) {
        super(urlLoader, modelCache);
    }

    @Override
    public boolean handles(@NonNull MediaEntity model) {
        return true;
    }

    @Override
    protected String getUrl(MediaEntity model, int width, int height, Options options) {
        String url = model != null ? model.getPath() : "";
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        return ThumbImageUtils.thumb(url);
    }
}
