/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.halo.R;
import com.halo.databinding.MediaViewerImageItemBinding;
import com.halo.presentation.mediaviewer.drawee.OnScaleChangeListener;
import com.halo.presentation.mediaviewer.drawee.ZoomableDraweeView;

public class HaloZoomDrawerView extends RelativeLayout {
    MediaViewerImageItemBinding binding;
    private boolean isZoomingAllowed;
    private ImageRequestBuilder imageRequestBuilder;
    private OnScaleChangeListener onScaleChangeListener;
    private float scaleV;
    private boolean scaleB;
    private GenericDraweeHierarchy genericDraweeHierarchy;
    private String url;

    public void setImageRequestBuilder(ImageRequestBuilder imageRequestBuilder) {
        this.imageRequestBuilder = imageRequestBuilder;
    }

    public void setZoomingAllowed(boolean zoomingAllowed) {
        isZoomingAllowed = zoomingAllowed;
        binding.zoomableDrawer.setEnabled(isZoomingAllowed);
    }

    public HaloZoomDrawerView(Context context) {
        super(context);
        initView();
    }

    public HaloZoomDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HaloZoomDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.media_viewer_image_item,
                this,
                false);
        addView(binding.getRoot(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        binding.zoomableDrawer.setEnabled(isZoomingAllowed);
        binding.retryBt.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(url)) setController(url);
        });
    }

    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
        if (binding != null && binding.zoomableDrawer != null) {
            binding.zoomableDrawer.setOnScaleChangeListener(onScaleChangeListener);
        }
    }

    public boolean isScale() {
        if (binding != null && binding.zoomableDrawer != null) {
            return binding.zoomableDrawer.getScale() > 1.0f;
        }
        return false;
    }

    public void setScale(float v, boolean b) {
        this.scaleV = v;
        this.scaleB = b;
        if (binding != null && binding.zoomableDrawer != null) {
            binding.zoomableDrawer.setScale(v, b);
        }

    }

    public void setHierarchy(GenericDraweeHierarchy build) {
        this.genericDraweeHierarchy = build;
        if (binding != null && binding.zoomableDrawer != null) {
            binding.zoomableDrawer.setHierarchy(build);
        }
    }

    public void setController(String url) {
        this.url = url;
        if (binding != null && binding.zoomableDrawer != null) {

            PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder();
            controllerBuilder.setUri(url);
            controllerBuilder.setOldController(binding.zoomableDrawer.getController());
            controllerBuilder.setControllerListener(getDraweeControllerListener(binding.zoomableDrawer));
            if (imageRequestBuilder != null) {
                imageRequestBuilder.setSource(Uri.parse(url));
                controllerBuilder.setImageRequest(imageRequestBuilder.build());
            }
            Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(url));
            binding.zoomableDrawer.setController(controllerBuilder.build());
        }
    }

    private BaseControllerListener<ImageInfo> getDraweeControllerListener(@NonNull final ZoomableDraweeView drawee) {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                binding.progress.setVisibility(GONE);
                binding.retryBt.setVisibility(GONE);
                binding.progressBar.setVisibility(GONE);
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                drawee.update(imageInfo.getWidth(), imageInfo.getHeight());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                binding.progress.setVisibility(VISIBLE);
                binding.retryBt.setVisibility(VISIBLE);
                binding.progressBar.setVisibility(GONE);
                super.onFailure(id, throwable);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                binding.progress.setVisibility(VISIBLE);
                binding.retryBt.setVisibility(GONE);
                binding.progressBar.setVisibility(VISIBLE);
                super.onSubmit(id, callerContext);
            }
        };
    }

    public void invalidateView() {
        if (!TextUtils.isEmpty(url)) {
            ImagePipelineFactory imagePipelineFactory = Fresco.getImagePipelineFactory();
            // TODO: error: cannot access Task
            // imagePipelineFactory.getMainBufferedDiskCache().remove(new SimpleCacheKey(url));
            imagePipelineFactory.getSmallImageFileCache().remove(new SimpleCacheKey(url));
            imagePipelineFactory.getImagePipeline().clearCaches();
            imagePipelineFactory.getImagePipeline().clearDiskCaches();
            imagePipelineFactory.getImagePipeline().clearMemoryCaches();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.clearMemoryCaches();
            imagePipeline.clearDiskCaches();
            imagePipeline.clearCaches();

        }
        if (binding != null) {
            if (binding.zoomableDrawer != null) {
                if (binding.zoomableDrawer.getController() != null) {
                    binding.zoomableDrawer.getController().onDetach();
                }
                binding.zoomableDrawer.setController(null);
                binding.zoomableDrawer.setImageDrawable(null);
            }
        }
    }
}
