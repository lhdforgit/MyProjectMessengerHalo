/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.gif_gr.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.giphy.sdk.core.models.Media;
import com.halo.widget.model.GifModel;
import com.halo.widget.sticker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GiphyHorizontalAdapter extends RecyclerView.Adapter<GiphyHorizontalAdapter.GiphyItemHolder> {
    private List<GifModel> mediaList = new ArrayList<>();
    private RequestManager requestManager;
    private int width;
    private int height;

    public GiphyHorizontalAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }
//
//    public void updateList(List<Media> newList) {
//        GiphyCallBack diffCallback = new GiphyCallBack(this.mediaList, newList);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
//        mediaList.clear();
//        mediaList.addAll(newList);
//        diffResult.dispatchUpdatesTo(this);
//    }

    public void updateList(List<GifModel> newList) {
        GiphyCallBack diffCallback = new GiphyCallBack(this.mediaList, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        mediaList.clear();
        mediaList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public GiphyItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (width != viewGroup.getWidth() && viewGroup.getWidth() != 0) {
            width = viewGroup.getWidth();
        }
        if (height != viewGroup.getHeight() && viewGroup.getHeight() != 0) {
            height = viewGroup.getHeight();
        }
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(
                R.layout.layout_giphy_horizontal_item,
                viewGroup, false);
        return new GiphyItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GiphyItemHolder giphyItemHolder, int i) {
        giphyItemHolder.onBind(mediaList.get(i));
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class GiphyItemHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private View avLoading;

        public GiphyItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_giphy);
            avLoading = itemView.findViewById(R.id.av_loading);
        }

        void onBind(GifModel media) {
            int width = 0;
            if (height > 0) {
                width = height / 4 * 6;
                imageView.getLayoutParams().width = width;
            }
            String link = media.getGif()!=null? media.getGif().getPreview() : "";

            avLoading.setVisibility(View.VISIBLE);
            requestManager.asGif()
//                    .load(TextUtils.isEmpty(link)?String.format(itemView.getContext().getString(R.string.media_giphy_url), media.getId()):link)
                    .load(link)
                    .apply(new RequestOptions()
                            .override(width, height)
                            .priority(Priority.NORMAL)
                            .centerCrop())
                    .apply(RequestOptions.placeholderOf(ContextCompat.getDrawable(imageView.getContext(), com.halo.widget.R.drawable.holder_rect)))
                    .apply(RequestOptions.errorOf(ContextCompat.getDrawable(imageView.getContext(), R.drawable.holder_rect)))
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            avLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            avLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
            itemView.setOnClickListener(v -> {
                if (giphyListener != null) {
                    giphyListener.itemOnClick(media);
                }
            });

        }
    }

    public class GiphyCallBack extends DiffUtil.Callback {

        private List<GifModel> oldList;
        private List<GifModel> newList;

        public GiphyCallBack(List<GifModel> oldList, List<GifModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return TextUtils.equals(oldList.get(i).getId(), newList.get(i1).getId());
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            return TextUtils.equals(oldList.get(i).getId(), newList.get(i1).getId());
        }

    }

    private GiphyListener giphyListener;

    public void setGiphyListener(GiphyListener giphyListener) {
        this.giphyListener = giphyListener;
    }

    public interface GiphyListener {
        void itemOnClick(GifModel media);

    }
}
