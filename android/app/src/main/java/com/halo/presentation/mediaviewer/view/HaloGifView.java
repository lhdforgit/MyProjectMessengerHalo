/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.halo.R;
import com.halo.databinding.MediaViewerGifItemBinding;

public class HaloGifView extends RelativeLayout {
    private MediaViewerGifItemBinding binding;
    private String url;
    private RequestManager requestManager;

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
        if (!TextUtils.isEmpty(url)) loadPath(url);
    }

    public HaloGifView(Context context) {
        super(context);
        initView();
    }

    public HaloGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HaloGifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.media_viewer_gif_item,
                this,
                false);
        addView(binding.getRoot(),
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        binding.retryBt.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(url)) loadPath(url);
        });
    }

    public void loadPath(String url) {
        binding.progress.setVisibility(VISIBLE);
        binding.progressBar.setVisibility(VISIBLE);
        binding.retryBt.setVisibility(GONE);
        this.url = url;
        if (requestManager != null) {
            requestManager
                    .asGif()
                    .addListener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            binding.progress.setVisibility(VISIBLE);
                            binding.progressBar.setVisibility(GONE);
                            binding.retryBt.setVisibility(VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progress.setVisibility(GONE);
                            binding.progressBar.setVisibility(GONE);
                            binding.retryBt.setVisibility(GONE);
                            return false;
                        }

                    })
                    .load(url)
                    .into(binding.gifDrawer);
        }
    }

    public void invalidateView() {
        if (binding.gifDrawer != null) {
            if (requestManager != null) {
                requestManager.clear(binding.gifDrawer);
            }
            binding.gifDrawer.setImageDrawable(null);
        }
    }
}
