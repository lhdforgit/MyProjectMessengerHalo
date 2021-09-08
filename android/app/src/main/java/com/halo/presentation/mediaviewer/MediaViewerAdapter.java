/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hahalolo.player.exoplayer.HaloPlayer;
import com.halo.common.utils.ThumbImageUtils;
import com.halo.data.entities.media.MediaEntity;
import com.halo.data.entities.media.TypeMedia;
import com.halo.presentation.mediaviewer.adapter.RecyclingPagerAdapter;
import com.halo.presentation.mediaviewer.adapter.ViewHolder;
import com.halo.presentation.mediaviewer.drawee.OnScaleChangeListener;
import com.halo.presentation.mediaviewer.view.HaloGifView;
import com.halo.presentation.mediaviewer.view.HaloZoomDrawerView;
import com.halo.presentation.mediaviewer.view.VideoPlayerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.halo.presentation.mediaviewer.MediaViewerAdapter.IDView.GIF;
import static com.halo.presentation.mediaviewer.MediaViewerAdapter.IDView.IMAGE;
import static com.halo.presentation.mediaviewer.MediaViewerAdapter.IDView.VIDEO;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
public class MediaViewerAdapter extends RecyclingPagerAdapter<ViewHolder> {

    private Context context;
    private List<MediaEntity> dataSet;
    private HashSet<ViewHolder> holders;
    private ImageRequestBuilder imageRequestBuilder;
    private GenericDraweeHierarchyBuilder hierarchyBuilder;
    private boolean isZoomingAllowed;
    private MediaViewerListener listener;
    private RequestManager requestManager;

    private HaloPlayer player;

    public void setListener(MediaViewerListener listener) {
        this.listener = listener;
    }

    MediaViewerAdapter(Context context,
                       List<MediaEntity> dataSet,
                       ImageRequestBuilder imageRequestBuilder,
                       GenericDraweeHierarchyBuilder hierarchyBuilder,
                       RequestManager requestManager,
                       boolean isZoomingAllowed,
                       HaloPlayer player) {
        this.context = context;
        this.dataSet = dataSet == null ? new ArrayList<>() : dataSet;
        this.holders = new HashSet<>();
        this.imageRequestBuilder = imageRequestBuilder;
        this.hierarchyBuilder = hierarchyBuilder;
        this.isZoomingAllowed = isZoomingAllowed;
        this.requestManager = requestManager;
        this.player = player;
    }

    public void setDataSet(List<MediaEntity> dataSet) {
        this.dataSet = dataSet == null ? new ArrayList<>() : dataSet;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup parent, int position, @NonNull Object object) {
        if (object instanceof MediaViewHolder) {
            ((MediaViewHolder) object).unbind();
        }
        super.destroyItem(parent, position, object);
    }

    @Override
    public int getItemViewType(int position) {
        MediaEntity data = null ;
        if (dataSet != null && position >= 0 && position < dataSet.size()) {
            data = dataSet.get(position);
        }
        if (data!=null && data.getType()!=null){
            switch (data.getType()) {
                case TypeMedia.IMG:
                    if (ThumbImageUtils.isGif(getUrl(position))) {
                        return IDView.GIF;
                    }
                    return IDView.IMAGE;
                case TypeMedia.VID:
                    return IDView.VIDEO;
            }
        }
        String url = getUrl(position);
        if (!TextUtils.isEmpty(url)) {
            if (ThumbImageUtils.isImage(url)) {
                return IDView.IMAGE;
            } else if (ThumbImageUtils.isGif(getUrl(position))) {
                return IDView.GIF;
            } else if (ThumbImageUtils.isVideo(url)) {
                return IDView.VIDEO;
            }
        }
        return IDView.IMAGE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        switch (viewType) {
            case IDView.IMAGE:
                HaloZoomDrawerView drawer = new HaloZoomDrawerView(context);
                drawer.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                drawer.setZoomingAllowed(isZoomingAllowed);
                drawer.setImageRequestBuilder(imageRequestBuilder);
                viewHolder = new MediaViewHolder(drawer);
                break;

            case IDView.VIDEO:
                VideoPlayerView playerView = new VideoPlayerView(context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                playerView.setLayoutParams(params);
                viewHolder = new MediaViewHolder(playerView);
                break;
            case IDView.GIF:
                HaloGifView imageGif = new HaloGifView(context);
                imageGif.setRequestManager(requestManager);
                imageGif.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                viewHolder = new MediaViewHolder(imageGif);
                break;
        }

        holders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof MediaViewHolder) {
            ((MediaViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private String getUrl(int index) {
        return dataSet.get(index).getPath();
    }

    @IntDef({IMAGE, VIDEO, GIF})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IDView {
        int IMAGE = 1;
        int VIDEO = 2;
        int GIF = 3;
    }

    class MediaViewHolder extends ViewHolder implements OnScaleChangeListener {

        private HaloZoomDrawerView drawer;
        private boolean isScaled;
        private VideoPlayerView playerView;
        private HaloGifView imageGif;

        MediaViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof HaloZoomDrawerView) {
                this.drawer = (HaloZoomDrawerView) itemView;
            } else if (itemView instanceof HaloGifView) {
                this.imageGif = (HaloGifView) itemView;
            } else if (itemView instanceof VideoPlayerView) {
                this.playerView = (VideoPlayerView) itemView;
            }
        }

        void bind(int position) {
            MediaEntity mediaEntity = dataSet.get(position);
            if (drawer != null) {
                tryToSetHierarchy();
                String path;
                path = ThumbImageUtils.thumb(
                        ThumbImageUtils.Size.MEDIA_WIDTH_1080 ,
                        mediaEntity.getPath(),
                        ThumbImageUtils.TypeSize._AUTO);
                setController(path);
                drawer.setOnScaleChangeListener(this);

            } else if (imageGif != null) {
                imageGif.setRequestManager(requestManager);
                imageGif.loadPath(getUrl(position));
            } else if (playerView != null) {
                playerView.bindVideo(mediaEntity.path, player);
            }
        }

        @Override
        public void onScaleChange(float scaleFactor, float focusX, float focusY) {
            isScaled = drawer.isScale();
            if (listener != null) {
                listener.showViewOption(!isScaled, !isScaled);
            }
        }

        void resetScale() {
            drawer.setScale(1.0f, true);
        }

        private void tryToSetHierarchy() {
            if (hierarchyBuilder != null) {
                hierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                drawer.setHierarchy(hierarchyBuilder.build());
            }
        }

        private void setController(String url) {
            drawer.setController(url);
        }

        void unbind() {
            if (drawer != null) {
                drawer.invalidateView();
            }
            if (playerView != null) {
                playerView.unbind();
            }
            if (imageGif != null) {
                imageGif.invalidateView();
            }
        }
    }
}
