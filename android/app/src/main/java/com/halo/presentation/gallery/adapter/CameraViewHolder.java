/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.halo.R;
import com.halo.databinding.GalleryCameraItemBinding;

import javax.annotation.Nonnull;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/13/18
 */
public class CameraViewHolder extends RecyclerView.ViewHolder {

    public interface CameraListener {
        void cameraClickHandler();
    }

    private GalleryCameraItemBinding binding;

    private CameraViewHolder(@NonNull GalleryCameraItemBinding binding, @Nonnull CameraListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.setListener(listener);
    }

    public void bind() {
        binding.executePendingBindings();
    }

    public static CameraViewHolder createHolder(@Nonnull ViewGroup parent, @NonNull CameraListener listener) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GalleryCameraItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.gallery_camera_item, parent, false);
        return new CameraViewHolder(binding, listener);
    }
}
