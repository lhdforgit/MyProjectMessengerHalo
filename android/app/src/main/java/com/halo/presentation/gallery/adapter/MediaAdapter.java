/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.adapter;

import android.database.Cursor;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.model.SelectedItemCollection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/13/18
 */
public class MediaAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    public interface Listener {

        void onMediaClick(Album album, Item item, int adapterPosition);

        void cameraClickHandler();
    }

    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;

    private final SelectedItemCollection mSelectedCollection;
    private Listener listener;

    public MediaAdapter(SelectedItemCollection selectedCollection, @NonNull Listener listener) {
        super(null);
        mSelectedCollection = selectedCollection;
        this.listener = checkNotNull(listener, "Listener can not be null");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @LayoutRes int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CAPTURE:
                return CameraViewHolder.createHolder(viewGroup, () -> {
                    if (listener != null) {
                        listener.cameraClickHandler();
                    }
                });
            case VIEW_TYPE_MEDIA:
                return MediaViewHolder.createHolder(viewGroup, mSelectedCollection,
                        new MediaViewHolder.Listener() {
                            @Override
                            public void checkStateUpdate() {
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onMediaClick(Album album, Item item, int adapterPosition) {
                                if (listener != null) {
                                    listener.onMediaClick(album, item, adapterPosition);
                                }
                            }
                        });
            default:
                throw new IllegalArgumentException("unknown view type: " + viewType);
        }
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof MediaViewHolder) {
            ((MediaViewHolder) holder).bind(Item.valueOf(cursor));
        }
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return Item.valueOf(cursor).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
    }
}
