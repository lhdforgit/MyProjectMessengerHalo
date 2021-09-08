/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.RequestManager;
import com.giphy.sdk.core.models.Media;
import com.google.android.material.tabs.TabLayout;
import com.halo.data.entities.mongo.tet.GifCard;
import com.halo.widget.felling.model.StickerEntity;
import com.halo.widget.repository.sticker.StickerRepository;
import com.halo.widget.sticker.R;

import org.jetbrains.annotations.NotNull;

public class StickerGroupView extends RelativeLayout {

    private View root;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private StickerGroupPager adapter;
    private RequestManager requestManager;


    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public StickerGroupView(Context context) {
        super(context);
        initView();
    }

    public StickerGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StickerGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.layout_sticker_group, null);
        tabLayout = root.findViewById(R.id.sticker_tab);
        viewPager = root.findViewById(R.id.sticker_pager);
        this.addView(root);
    }

    private void initPager() {
        adapter = new StickerGroupPager(requestManager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        if (listener != null) {
            adapter.setListener(listener);
        }
    }

    StickerGroupListener listener;

    public void setListener(StickerGroupListener listener) {
        this.listener = listener;
        if (adapter != null) {
            adapter.setListener(listener);
        }
    }

    public interface StickerGroupListener {
        void onClickStickerItem(StickerEntity stickerEntity);
        default void onClickGifItem(Media media) {
        }
        void onClickGifCardItem(@NotNull GifCard gifCard);

        StickerRepository stickerRepository() ;
        LifecycleOwner lifecycleOwner() ;
        @Nullable default String token(){
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initPager();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }
}
