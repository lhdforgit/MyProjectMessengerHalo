/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.viewer.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.halo.widget.imageviewer.R;
import com.halo.widget.imageviewer.databinding.MediaViewerPhotoItemBinding;
import com.halo.widget.imageviewer.loader.ImageLoader;
import com.halo.widget.photoview.OnViewDragListener;

import org.jetbrains.annotations.NotNull;

public class HaloPhotoView<T> extends RelativeLayout {
    MediaViewerPhotoItemBinding binding;

    public HaloPhotoView(Context context) {
        super(context);
        initView();
    }

    public HaloPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HaloPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.media_viewer_photo_item,
                this,
                false);
        addView(binding.getRoot(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        binding.retryBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setOnViewDragListener();
    }

    public void loadImage(ImageLoader<T> imageLoader, T t) {
        binding.progress.setVisibility(VISIBLE);
        binding.retryBt.setVisibility(GONE);
        binding.progressBar.setVisibility(VISIBLE);
        imageLoader.loadImage(binding.photoView, requestListener, t);
    }

    public boolean isSacleZoom() {
        return binding.photoView.getScale() > 1.0f;
    }

    public void setOnViewDragListener() {
        binding.photoView.setEnabled(true);
        binding.photoView.setOnViewDragListener(new OnViewDragListener() {
            @Override
            public void onDrag(float dx, float dy) {
                binding.photoView.setAllowParentInterceptOnEdge(binding.photoView.getScale() == 1.0f);
            }
        });
    }

    RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            binding.progress.setVisibility(VISIBLE);
            binding.progressBar.setVisibility(GONE);
            binding.retryBt.setVisibility(VISIBLE);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            binding.progress.setVisibility(GONE);
            binding.progressBar.setVisibility(GONE);
            binding.retryBt.setVisibility(GONE);
            return false;
        }
    };

    @NotNull
    public void resetScale() {
        binding.photoView.setScale(1.0f, true);
    }

}
