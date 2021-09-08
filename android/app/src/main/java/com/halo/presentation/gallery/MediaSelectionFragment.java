/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.halo.R;
import com.halo.databinding.GalleryFrBinding;
import com.halo.presentation.gallery.adapter.MediaAdapter;
import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.entities.SelectionSpec;
import com.halo.presentation.gallery.model.AlbumMediaCollection;
import com.halo.presentation.gallery.model.SelectedItemCollection;
import com.halo.presentation.gallery.utils.UIUtils;
import com.halo.presentation.gallery.widget.MediaGridInset;
import com.halo.widget.HaloGridLayoutManager;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/14/18
 */
public class MediaSelectionFragment extends Fragment {

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }

    public static final String EXTRA_ALBUM = "extra_album";

    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private MediaAdapter mAdapter;
    private SelectionProvider mSelectionProvider;

    private MediaAdapter.Listener adapterListener;

    GalleryFrBinding binding;

    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) context;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (context instanceof MediaAdapter.Listener) {
            adapterListener = (MediaAdapter.Listener) context;
        } else {
            throw new IllegalStateException("Context must implement MediaAdapter.Listener.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.gallery_fr, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && adapterListener != null && mSelectionProvider != null) {
            Album album = getArguments().getParcelable(EXTRA_ALBUM);
            mAdapter = new MediaAdapter(mSelectionProvider.provideSelectedItemCollection(), adapterListener);
            binding.mediaRec.setHasFixedSize(true);

            int spanCount;
            SelectionSpec selectionSpec = SelectionSpec.getInstance();
            if (selectionSpec.gridExpectedSize > 0 && getContext() != null) {
                spanCount = UIUtils.spanCount(getContext(), selectionSpec.gridExpectedSize);
            } else {
                spanCount = selectionSpec.spanCount;
            }
            binding.mediaRec.setLayoutManager(new HaloGridLayoutManager(getContext(), spanCount));

            int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
            binding.mediaRec.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
            binding.mediaRec.setAdapter(mAdapter);
            if (getActivity() != null && album != null) {
                mAlbumMediaCollection.onCreate(getActivity(), new AlbumMediaCollection.AlbumMediaCallbacks() {
                    @Override
                    public void onAlbumMediaLoad(Cursor cursor) {
                        mAdapter.swapCursor(cursor);
                    }

                    @Override
                    public void onAlbumMediaReset() {
                        mAdapter.swapCursor(null);
                    }
                });
                mAlbumMediaCollection.load(album, selectionSpec.capture);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
        adapterListener = null;
    }
}
