/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.widget;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.halo.R;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.entities.SelectionSpec;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/12/18
 */
public class MediaView extends ConstraintLayout {

    private ImageView mThumbnail;
    private CheckView mCheckView;
    private AppCompatTextView mGifTag;
    private AppCompatTextView mVideoDuration;
    private ImageView mVideoTag;

    private Item mMedia;
    private PreBindInfo mPreBindInfo;
    private Listener mListener;

    public MediaView(Context context) {
        super(context);
        init(context);
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.gallery_view, this, true);

        mThumbnail = findViewById(R.id.media_thumbnail_iv);
        mCheckView = findViewById(R.id.check_view);
        mGifTag = findViewById(R.id.gif_tv);
        mVideoDuration = findViewById(R.id.video_duration_tv);
        mVideoTag = findViewById(R.id.play_video_iv);

        mThumbnail.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            }
        });
        mCheckView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            }
        });
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Item item) {
        mMedia = item;
        setGifTag();
        initCheckView();
        setImage();
        setVideoDuration();
    }

    public Item getMedia() {
        return mMedia;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mMedia.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifThumbnail(getContext(), mThumbnail, mMedia.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(), mThumbnail, mMedia.getContentUri());
        }
    }

    private void setVideoDuration() {
        if (mMedia.isVideo()) {
            mVideoDuration.setVisibility(VISIBLE);
            mVideoTag.setVisibility(VISIBLE);
            mVideoDuration.setText(DateUtils.formatElapsedTime(mMedia.duration / 1000));
        } else {
            mVideoDuration.setVisibility(GONE);
            mVideoTag.setVisibility(GONE);
        }
    }

    public void setListener(@NonNull Listener listener) {
        mListener = checkNotNull(listener);
    }

    public void removeListener() {
        mListener = null;
    }

    public interface Listener {

        void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder);
    }

    public static class PreBindInfo {
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(boolean checkViewCountable, RecyclerView.ViewHolder viewHolder) {
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }
}
