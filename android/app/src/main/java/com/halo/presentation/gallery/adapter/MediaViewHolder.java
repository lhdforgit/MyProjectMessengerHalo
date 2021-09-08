/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.halo.R;
import com.halo.databinding.GalleryItemBinding;
import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.entities.IncapableCause;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.entities.SelectionSpec;
import com.halo.presentation.gallery.model.SelectedItemCollection;
import com.halo.presentation.gallery.widget.CheckView;
import com.halo.presentation.gallery.widget.MediaView;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/13/18
 */
public class MediaViewHolder extends RecyclerView.ViewHolder implements MediaView.Listener {

    public interface Listener {

        void checkStateUpdate();

        void onMediaClick(Album album, Item item, int adapterPosition);
    }

    private GalleryItemBinding binding;

    private final SelectedItemCollection mSelectedCollection;
    private SelectionSpec mSelectionSpec;
    private Listener listener;

    private MediaViewHolder(@NonNull GalleryItemBinding binding, @NonNull SelectedItemCollection selectedCollection, @NonNull Listener listener) {
        super(binding.getRoot());
        this.binding = checkNotNull(binding, "Binding can not be null");
        mSelectionSpec = SelectionSpec.getInstance();
        this.mSelectedCollection = checkNotNull(selectedCollection, "SelectedItemCollection can not be null");
        this.listener = checkNotNull(listener, "Listener can not be null");
    }

    public void registerListener(Listener listener) {
        this.listener = checkNotNull(listener, "Listener can not be null");
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public void bind(@NonNull Item item) {
        checkNotNull(item, "Item can not be null");
        binding.mediaView.preBindMedia(new MediaView.PreBindInfo(mSelectionSpec.countable, this));
        binding.mediaView.bindMedia(item);
        binding.mediaView.setListener(this);
        setCheckStatus(item);
        binding.executePendingBindings();
    }

    private void setCheckStatus(Item item) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                binding.mediaView.setCheckEnabled(true);
                binding.mediaView.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    binding.mediaView.setCheckEnabled(false);
                    binding.mediaView.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    binding.mediaView.setCheckEnabled(true);
                    binding.mediaView.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                binding.mediaView.setCheckEnabled(true);
                binding.mediaView.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    binding.mediaView.setCheckEnabled(false);
                    binding.mediaView.setChecked(false);
                } else {
                    binding.mediaView.setCheckEnabled(true);
                    binding.mediaView.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder) {
        // TODO: Khi click vao hinh anh, thi van xem nhu checked
        //if (listener != null) {
        //    listener.onMediaClick(null, item, holder.getAdapterPosition());
        //}
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    /**
     * Notify to adapter when image checked
     */
    private void notifyCheckStateChanged() {
        if (listener != null) {
            listener.checkStateUpdate();
        }
    }

    public static MediaViewHolder createHolder(@NonNull ViewGroup parent, @NonNull SelectedItemCollection selectedCollection, @NonNull Listener listener) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GalleryItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.gallery_item, parent, false);
        return new MediaViewHolder(binding, selectedCollection, listener);
    }
}
