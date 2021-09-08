/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.halo.R;
import com.halo.databinding.GalleryActBinding;
import com.halo.presentation.gallery.adapter.AlbumAdapter;
import com.halo.presentation.gallery.adapter.MediaAdapter;
import com.halo.presentation.gallery.entities.Album;
import com.halo.presentation.gallery.entities.IncapableCause;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.entities.SelectionSpec;
import com.halo.presentation.gallery.model.AlbumCollection;
import com.halo.presentation.gallery.model.SelectedItemCollection;
import com.halo.presentation.gallery.utils.MediaStoreCompat;
import com.halo.presentation.gallery.utils.MimeType;
import com.halo.presentation.gallery.utils.PathUtils;
import com.halo.presentation.gallery.widget.AlbumsSpinner;

import java.util.ArrayList;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public class MediaAct extends AppCompatActivity implements MediaSelectionFragment.SelectionProvider, MediaAdapter.Listener {

    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
    public static final String EXTRA_RESULT_SELECTION_ITEM = "extra_result_selection_item";

    private static final int REQUEST_CODE_CAPTURE = 24;

    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);

    private AlbumsSpinner mAlbumsSpinner;
    private AlbumAdapter mAlbumAdapter;

    GalleryActBinding binding;

    public static Intent getIntent(Context context) {
        return new Intent(context, MediaAct.class);
    }

    /**
     * {@link AppCompatActivity#onCreate(Bundle)})
     * programmatically set theme before {@link super#onCreate(Bundle)}}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SelectionSpec mSpec = SelectionSpec.getInstance();
        setTheme(mSpec.themeId);
        super.onCreate(savedInstanceState);
        if (!mSpec.hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        binding = DataBindingUtil.setContentView(this, R.layout.gallery_act);

        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        initActionBar();

        mSelectedCollection.onCreate(savedInstanceState);
        if (mSpec.defaultItemSelection != null && !mSpec.defaultItemSelection.isEmpty()) {
            mSelectedCollection.setDefaultSelection(mSpec.defaultItemSelection);
        }

        mAlbumAdapter = new AlbumAdapter(this, null, false);
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mAlbumCollection.setStateCurrentSelection(position);
                mAlbumAdapter.getCursor().moveToPosition(position);
                Album album = Album.valueOf(mAlbumAdapter.getCursor());
                if (album.isAll() && SelectionSpec.getInstance().capture) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAlbumsSpinner.setSelectedTextView(binding.selectAlbumsTv);
        mAlbumsSpinner.setPopupAnchorView(binding.toolBar);

        mAlbumsSpinner.setAdapter(mAlbumAdapter);
        mAlbumCollection.onCreate(this, new AlbumCollection.AlbumCallbacks() {
            @Override
            public void onAlbumLoad(Cursor cursor) {
                mAlbumAdapter.swapCursor(cursor);
                // select default album.
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    if (!isFinishing()) {
                        if (cursor != null && !cursor.isClosed() && mAlbumsSpinner != null) {
                            cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                            mAlbumsSpinner.setSelection(MediaAct.this, mAlbumCollection.getCurrentSelection());
                            Album album = Album.valueOf(cursor);
                            if (album.isAll() && SelectionSpec.getInstance().capture) {
                                album.addCaptureCount();
                            }
                            onAlbumSelected(album);
                        }
                    }
                });
            }

            @Override
            public void onAlbumReset() {
                mAlbumAdapter.swapCursor(null);
            }
        });
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();

        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            if (mSpec.captureStrategy == null) {
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            }
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
            if (mSpec.captureNow) {
                cameraClickHandler();
            }
        }
    }

    private void initActionBar() {
        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.media_done_item) {
            Intent result = new Intent();

            // Return result list uri when click done
            ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);

            // Return result list path string when click done
            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);

            // Return result list item when click done
            ArrayList<Item> items = (ArrayList<Item>) mSelectedCollection.asList();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION_ITEM, items);

            setResult(RESULT_OK, result);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_CAPTURE) {
            // Just pass the data back to previous calling Activity.
            Uri contentUri = mMediaStoreCompat.getCurrentPhotoUri();
            String path = mMediaStoreCompat.getCurrentPhotoPath();
            Item item = new Item(Item.ITEM_ID_CAPTURE, MimeType.PNG.toString(), -1, -1);
            item.uri = contentUri;
            item.path = path;

            ArrayList<Uri> selected = new ArrayList<>();
            selected.add(contentUri);

            ArrayList<String> selectedPath = new ArrayList<>();
            selectedPath.add(path);

            ArrayList<Item> selectedItems = new ArrayList<>();
            selectedItems.add(item);

            Intent result = new Intent();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPath);
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION_ITEM, selectedItems);

            setResult(RESULT_OK, result);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                MediaAct.this.revokeUriPermission(contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            finish();
        }
    }

    /**
     * When {@link AlbumsSpinner} selected item
     *
     * @param album {@link Album} of spinner item
     */
    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            // TODO: Show empty album and hide media selection
        } else {
            // TODO: Hide empty album and show media selection
            binding.container.setVisibility(View.VISIBLE);
            MediaSelectionFragment fragment = MediaSelectionFragment.newInstance(album);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        // Return result item when user click media
        if (item != null) {
            if (assertAddSelection(this, item)) {
                Intent result = new Intent();

                // Return result uri when user select media
                ArrayList<Uri> selected = new ArrayList<>();
                selected.add(item.uri);
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);

                // Return result path when user select media
                ArrayList<String> selectedPaths = new ArrayList<>();
                selectedPaths.add(PathUtils.getPath(MediaAct.this, item.uri));
                result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);

                // Return result path when user select media
                ArrayList<Item> selectedItems = new ArrayList<>();
                selectedItems.add(item);
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION_ITEM, selectedItems);

                setResult(RESULT_OK, result);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void cameraClickHandler() {
        if (assertAddSelectionCapture(this)) {
            if (mMediaStoreCompat != null) {
                mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
            }
        }
    }

    /**
     * Filter choose media
     *
     * @param context {@link Context}
     * @param item    {@link Item} item media
     * @return true if success filter
     */
    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    private boolean assertAddSelectionCapture(Context context) {
        IncapableCause cause = mSelectedCollection.isAcceptableCapture();
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }
}
