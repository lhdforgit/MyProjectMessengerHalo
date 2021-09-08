/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.RequestManager;
import com.halo.widget.sticker.sticker_group.giphy.GifPageView;
import com.halo.widget.sticker.sticker_group.sticker.StickerPageView;

public class StickerGroupPager extends PagerAdapter {
    private RequestManager requestManager;
    private StickerGroupView.StickerGroupListener listener;

    public StickerGroupPager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void setListener(StickerGroupView.StickerGroupListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        switch (position) {
            case 0:
                view = new StickerPageView(container.getContext(), requestManager);
                ((StickerPageView) view).setListener(listener);

                break;
            case 1:
                view = new GifPageView(container.getContext(), requestManager);
                ((GifPageView) view).setListener(listener);
                break;
        }
        if (view != null) {
            container.addView(view);
            return view;
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "sticker";
            case 1:
                return "gif";
        }

        return super.getPageTitle(position);
    }
}
