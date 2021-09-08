/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.databinding.DataBindingUtil;

import com.halo.R;
import com.halo.databinding.GalleryAlbumItemBinding;
import com.halo.presentation.gallery.entities.Album;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/14/18
 */
public class AlbumAdapter extends CursorAdapter {

    private GalleryAlbumItemBinding binding;

    public AlbumAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AlbumAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.gallery_album_item, viewGroup, false);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        binding.setAlbum(album);
        binding.executePendingBindings();
    }
}
