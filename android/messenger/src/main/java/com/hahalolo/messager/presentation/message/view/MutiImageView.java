/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.message.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.RequestManager;
import com.hahalolo.messenger.R;
import com.hahalolo.messenger.databinding.LayoutMutiliimage2Binding;
import com.hahalolo.messenger.databinding.LayoutMutiliimage3Binding;
import com.hahalolo.messenger.databinding.LayoutMutiliimage4Binding;
import com.hahalolo.messenger.databinding.LayoutMutiliimageBinding;
import com.halo.common.utils.ThumbImageUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.Nullable;

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
public class MutiImageView extends FrameLayout {
    private List<String> imageList = new ArrayList<>();

    private RequestManager requestManager;

    private int width;

    private int size;

    private ViewDataBinding binding;

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
        loadImage();
    }

    public MutiImageView(Context context) {
        super(context);
        initView();
    }

    public MutiImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public MutiImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadImage();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        size = MeasureSpec.getSize(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        if (binding != null && binding instanceof LayoutMutiliimage4Binding && width > 0) {
            ((LayoutMutiliimage4Binding) binding).countTv.setTextSize(width / 10);
        }
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        initLayout();
        loadImage();
    }

    public void setImage(String image){
        this.imageList = new ArrayList<>();
        this.imageList.add(image);
        initLayout();
        loadImage();
    }

    private void initLayout() {
        removeAllViews();
        binding = null;
        if (imageList != null && !imageList.isEmpty()) {
            switch (imageList.size()) {
                case 1:
                    binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                            R.layout.layout_mutiliimage,
                            this,
                            false);
                    loadImage((LayoutMutiliimageBinding) binding);
                    addView(binding.getRoot());
                    break;
                case 2:
                    binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                            R.layout.layout_mutiliimage_2,
                            this,
                            false);
                    loadImage((LayoutMutiliimage2Binding) binding);
                    addView(binding.getRoot());
                    break;
                case 3:
                    binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                            R.layout.layout_mutiliimage_3,
                            this,
                            false);
                    loadImage((LayoutMutiliimage3Binding) binding);
                    addView(binding.getRoot());
                    break;
                default:
                    binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                            R.layout.layout_mutiliimage_4,
                            this,
                            false);
                    loadImage((LayoutMutiliimage4Binding) binding);
                    addView(binding.getRoot());
                    break;
            }
        }
    }

    private void loadImage(LayoutMutiliimageBinding binding) {
        if (binding != null && requestManager != null && imageList != null && !imageList.isEmpty()) {
            requestManager.load(getThumb(imageList.get(0)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image1);
        }
    }

    private void loadImage(LayoutMutiliimage2Binding binding) {
        if (binding != null && requestManager != null && imageList != null && imageList.size() >= 2) {
            requestManager.load(getThumb(imageList.get(0)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image1);
            requestManager.load(getThumb(imageList.get(1)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image2);
        }
    }

    private void loadImage(LayoutMutiliimage3Binding binding) {
        if (binding != null && requestManager != null && imageList != null && imageList.size() >= 3) {
            requestManager.load(getThumb(imageList.get(0)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image1);
            requestManager.load(getThumb(imageList.get(1)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image2);
            requestManager.load(getThumb(imageList.get(2)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image3);
        }
    }

    private void loadImage(LayoutMutiliimage4Binding binding) {
        if (binding != null && requestManager != null && imageList != null && imageList.size() >= 4) {
            int size = imageList.size();
            requestManager.load(getThumb(imageList.get(0)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image1);
            requestManager.load(getThumb(imageList.get(1)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image2);
            requestManager.load(getThumb(imageList.get(2)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image3);
            requestManager.load(getThumb(imageList.get(3)))
                    .placeholder(R.drawable.img_holder)
                    .error(R.color.img_holder)
                    .into(binding.image4);
            binding.countTv.setVisibility(size > 4 ? View.VISIBLE : View.GONE);
            binding.countTv.setText(String.format("%s+", (int) (size - 4)));
        }
    }

    private void loadImage() {

    }

    private String getThumb(String content) {
        return ThumbImageUtils.thumb(ThumbImageUtils.Size.AVATAR_NORMAL,
                content,
                ThumbImageUtils.TypeSize._1_1);
    }

    private void init() {
        loadImage();
    }

    public void invalidateLayout(RequestManager requestManager) {

        if (binding != null) {
            if (binding instanceof LayoutMutiliimageBinding) {
                if (requestManager != null)
                    requestManager.clear(((LayoutMutiliimageBinding) binding).image1);
                ((LayoutMutiliimageBinding) binding).image1.setImageDrawable(null);
            } else if (binding instanceof LayoutMutiliimage2Binding) {
                if (requestManager != null) {
                    requestManager.clear(((LayoutMutiliimage2Binding) binding).image1);
                    requestManager.clear(((LayoutMutiliimage2Binding) binding).image2);
                }
                ((LayoutMutiliimage2Binding) binding).image1.setImageDrawable(null);
                ((LayoutMutiliimage2Binding) binding).image2.setImageDrawable(null);

            } else if (binding instanceof LayoutMutiliimage3Binding) {
                if (requestManager != null) {
                    requestManager.clear(((LayoutMutiliimage3Binding) binding).image1);
                    requestManager.clear(((LayoutMutiliimage3Binding) binding).image2);
                    requestManager.clear(((LayoutMutiliimage3Binding) binding).image3);
                }
                ((LayoutMutiliimage3Binding) binding).image1.setImageDrawable(null);
                ((LayoutMutiliimage3Binding) binding).image2.setImageDrawable(null);
                ((LayoutMutiliimage3Binding) binding).image3.setImageDrawable(null);

            } else if (binding instanceof LayoutMutiliimage4Binding) {
                if (requestManager != null) {
                    requestManager.clear(((LayoutMutiliimage4Binding) binding).image1);
                    requestManager.clear(((LayoutMutiliimage4Binding) binding).image2);
                    requestManager.clear(((LayoutMutiliimage4Binding) binding).image3);
                    requestManager.clear(((LayoutMutiliimage4Binding) binding).image4);
                }
                ((LayoutMutiliimage4Binding) binding).image1.setImageDrawable(null);
                ((LayoutMutiliimage4Binding) binding).image2.setImageDrawable(null);
                ((LayoutMutiliimage4Binding) binding).image3.setImageDrawable(null);
                ((LayoutMutiliimage4Binding) binding).image4.setImageDrawable(null);
            }
        }
    }
}
