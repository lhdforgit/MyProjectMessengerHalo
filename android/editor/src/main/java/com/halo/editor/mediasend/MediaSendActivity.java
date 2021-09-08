/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.mediasend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.util.Supplier;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.halo.common.utils.RxJavaUtil;
import com.halo.di.ActivityScoped;
import com.halo.editor.R;
import com.halo.editor.camera.Camera1Fragment;
import com.halo.editor.camera.CameraFragment;
import com.halo.editor.camera.CameraXFragment;
import com.halo.editor.camera.CameraXUtil;
import com.halo.editor.databinding.MediasendActivityBinding;
import com.halo.editor.imageeditor.model.EditorModel;
import com.halo.editor.mediapreview.MediaRailAdapter;
import com.halo.editor.mediasend.picker.MediaPickerFolderFragment;
import com.halo.editor.mediasend.picker.MediaPickerItemFragment;
import com.halo.editor.mediasend.send.MediaSendFragment;
import com.halo.editor.permissions.Permissions;
import com.halo.editor.providers.BlobProvider;
import com.halo.editor.scribbles.ImageEditorFragment;
import com.halo.editor.util.Function3;
import com.halo.editor.util.IOFunction;
import com.halo.editor.util.MediaUtil;
import com.halo.editor.util.ServiceUtil;
import com.halo.editor.util.SimpleProgressDialog;
import com.halo.editor.util.SimpleTask;
import com.halo.editor.util.Util;
import com.halo.editor.util.VideoUtil;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

/**
 * Encompasses the entire flow of sending media, starting from the selection process to the actual
 * captioning and editing of the content.
 * <p>
 * This activity is intended to be launched via {@link #startActivityForResult(Intent, int)}.
 * It will return the {@link Media} that the user decided to send.
 */
@ActivityScoped
public class MediaSendActivity
        extends PassphraseRequiredActionBarActivity
        implements MediaPickerFolderFragment.Controller,
        MediaPickerItemFragment.Controller,
        CameraFragment.Controller,
        ImageEditorFragment.Controller,
        MediaRailAdapter.RailItemListener {

    public static final String EXTRA_RESULT = "result";

    private static final String KEY_MEDIA = "media";
    private static final String KEY_IS_CAMERA = "is_camera";
    private static final String KEY_IS_GALLERY = "is_gallery";

    private static final String TAG_FOLDER_PICKER = "folder_picker";
    private static final String TAG_ITEM_PICKER = "item_picker";
    private static final String TAG_SEND = "send";
    private static final String TAG_CAMERA = "camera";

    @Inject
    ViewModelProvider.Factory factory;

    MediaSendViewModel viewModel;
    MediasendActivityBinding binding;

    @Inject
    Lazy<Camera1Fragment> camera1FragmentLazy;
    @Inject
    Lazy<CameraXFragment> cameraxFragmentLazy;
    @Inject
    Lazy<MediaSendFragment> mediaSendFragmentLazy;
    @Inject
    Lazy<MediaPickerFolderFragment> mediaPickerFolderFragmentLazy;

    private MediaRailAdapter mediaRailAdapter;
    private AlertDialog progressDialog;

    /**
     * Get an intent to launch the media send flow starting with the picker.
     */
    public static Intent buildGalleryIntent(@NonNull Context context) {
        return new Intent(context, MediaSendActivity.class);
    }

    public static Intent buildGalleryIntentWithMedia(@NonNull Context context,
                                                     @NonNull List<Media> media) {
        Intent intent = buildGalleryIntent(context);
        intent.putExtra(KEY_IS_GALLERY, true);
        intent.putParcelableArrayListExtra(KEY_MEDIA, new ArrayList<>(media));
        return intent;
    }

    /**
     * Get an intent to launch the media send flow starting with the picker.
     */
    public static Intent buildCameraIntent(@NonNull Context context) {
        Intent intent = buildGalleryIntent(context);
        intent.putExtra(KEY_IS_CAMERA, true);
        return intent;
    }

    /**
     * Get an intent to launch the media send flow with a specific list of media. Will jump right to
     * the editor screen.
     */
    public static Intent buildEditorIntent(@NonNull Context context,
                                           @NonNull List<Media> media) {
        Intent intent = buildGalleryIntent(context);
        intent.putParcelableArrayListExtra(KEY_MEDIA, new ArrayList<>(media));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean ready) {
        binding = DataBindingUtil.setContentView(this, R.layout.mediasend_activity);
        setResult(RESULT_CANCELED);

        if (savedInstanceState != null) {
            return;
        }

        viewModel = new ViewModelProvider(this, factory).get(MediaSendViewModel.class);

        List<Media> media = getIntent().getParcelableArrayListExtra(KEY_MEDIA);
        boolean isCamera = getIntent().getBooleanExtra(KEY_IS_CAMERA, false);

        if (isCamera) {
            Fragment fragment;
            if (CameraXUtil.isSupported()) {
                fragment = cameraxFragmentLazy.get();
            } else {
                fragment = camera1FragmentLazy.get();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mediasend_fragment_container, fragment, TAG_CAMERA)
                    .commit();

        } else if (!Util.isEmpty(media)) {
            viewModel.onSelectedMediaChanged(this, media);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mediasend_fragment_container, mediaSendFragmentLazy.get(), TAG_SEND)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mediasend_fragment_container, mediaPickerFolderFragmentLazy.get(), TAG_FOLDER_PICKER)
                    .commit();
        }

        binding.mediasendSendButton.setOnClickListener(v -> onSendClicked());

        binding.mediasendSendButton.setOnLongClickListener(v -> true);

        mediaRailAdapter = new MediaRailAdapter(Glide.with(this), this, true);
        binding.mediasendMediaRail.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.mediasendMediaRail.setAdapter(mediaRailAdapter);

        binding.mediasendCountButton.getRoot().setOnClickListener(v -> navigateToMediaSend());

        initViewModel();

        boolean isGallery = getIntent().getBooleanExtra(KEY_IS_GALLERY, false);
        navigatePickerMedia(isGallery);
    }

    private void navigatePickerMedia(boolean isGallery) {
        RxJavaUtil.delayInMainThread(isGallery, 200, TimeUnit.MILLISECONDS, isGallery1 -> {
            if (isGallery1) {
                MediaPickerFolderFragment folderFragment = MediaPickerFolderFragment.newInstance();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mediasend_fragment_container, folderFragment, TAG_FOLDER_PICKER)
                        .addToBackStack(null)
                        .commit();

                if (!TextUtils.isEmpty(viewModel.getBucketId().getValue())) {
                    MediaPickerItemFragment itemFragment =
                            MediaPickerItemFragment.newInstance(viewModel.getBucketId().getValue(),
                                    "", viewModel.getMaxSelection(), false);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mediasend_fragment_container, itemFragment, TAG_ITEM_PICKER)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /*---MediaPickerFolderFragment.Controller---*/

    @Override
    public void onFolderSelected(@NonNull MediaFolder folder) {
        viewModel.onFolderSelected(Strings.nullToEmpty(folder.getBucketId()));

        MediaPickerItemFragment fragment = MediaPickerItemFragment.newInstance(
                folder.getBucketId(), folder.getTitle(), viewModel.getMaxSelection(), false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mediasend_fragment_container, fragment, TAG_ITEM_PICKER)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCameraSelected() {
        navigateToCamera();
    }

    /*---MediaPickerItemFragment.Controller---*/

    @Override
    public void onMediaSelected(@NonNull Media media) {
        viewModel.onSingleMediaSelected(this, media);
        navigateToMediaSend();
    }

    /*---ImageEditorFragment.Controller---*/

    @Override
    public void onTouchEventsNeeded(boolean needed) {
        MediaSendFragment fragment = (MediaSendFragment) getSupportFragmentManager().findFragmentByTag(TAG_SEND);
        if (fragment != null) {
            fragment.onTouchEventsNeeded(needed);
        }
    }

    @Override
    public void onRequestFullScreen(boolean fullScreen, boolean hideKeyboard) {
        binding.mediasendSendAndRail.setVisibility(fullScreen ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDoneEditing() {

    }

    /*---RailItemListener---*/

    @Override
    public void onRailItemClicked(int distanceFromActive) {
        if (getMediaSendFragment() != null) {
            viewModel.onPageChanged(getMediaSendFragment().getCurrentImagePosition() + distanceFromActive);
        }
    }

    @Override
    public void onRailItemDeleteClicked(int distanceFromActive) {
        if (getMediaSendFragment() != null) {
            viewModel.onMediaItemRemoved(getMediaSendFragment().getCurrentImagePosition() + distanceFromActive);
        }
    }

    /*Send Media Button Click Action*/

    private void onSendClicked() {
        MediaSendFragment fragment = getMediaSendFragment();

        if (fragment == null) {
            throw new AssertionError("No editor fragment available!");
        }

        viewModel.onSendClicked(buildModelsToTransform(fragment))
                .observe(this, this::setActivityResultAndFinish);
    }

    private static Map<Media, MediaTransform> buildModelsToTransform(@NonNull MediaSendFragment fragment) {
        List<Media> mediaList = fragment.getAllMedia();
        Map<Uri, Object> savedState = fragment.getSavedState();
        Map<Media, MediaTransform> modelsToRender = new HashMap<>();

        for (Media media : mediaList) {
            Object state = savedState.get(media.getUri());
            if (state instanceof ImageEditorFragment.Data) {
                EditorModel model = ((ImageEditorFragment.Data) state).readModel();
                if (model != null && model.isChanged()) {
                    modelsToRender.put(media, new ImageEditorModelRenderMediaTransform(model, null));
                }
            }

            /*
            TODO: Edit Video
               if (state instanceof MediaSendVideoFragment.Data) {
                   modelsToRender.put(media, new VideoTrimTransform());
               }
            */
        }

        return modelsToRender;
    }

    private void onAddMediaClicked(@NonNull String bucketId) {
        MediaPickerFolderFragment folderFragment = MediaPickerFolderFragment.newInstance();
        MediaPickerItemFragment itemFragment = MediaPickerItemFragment.newInstance(bucketId, "", viewModel.getMaxSelection(), false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mediasend_fragment_container, folderFragment, TAG_FOLDER_PICKER)
                .addToBackStack(null)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mediasend_fragment_container, itemFragment, TAG_ITEM_PICKER)
                .addToBackStack(null)
                .commit();
    }

    private void onNoMediaAvailable() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void initViewModel() {
        viewModel.getHudState().observe(this, state -> {
            if (state == null) return;

            binding.mediasendSendAndRail.setVisibility(state.isHudVisible() ? View.VISIBLE : View.GONE);

            int captionBackground;

            if (state.getRailState() == MediaSendViewModel.RailState.VIEWABLE) {
                captionBackground = R.color.core_grey_90;
            } else if (state.getViewOnceState() == MediaSendViewModel.ViewOnceState.ENABLED) {
                captionBackground = 0;
            } else {
                captionBackground = R.color.transparent_black_40;
            }

            binding.mediasendSendAndRail.setBackgroundResource(captionBackground);

            switch (state.getButtonState()) {
                case SEND:
                    binding.mediasendSendButtonBkg.setVisibility(View.VISIBLE);
                    binding.mediasendCountButton.getRoot().setVisibility(View.GONE);
                    break;
                case COUNT:
                    binding.mediasendSendButtonBkg.setVisibility(View.GONE);
                    binding.mediasendCountButton.getRoot().setVisibility(View.VISIBLE);
                    binding.mediasendCountButton.setCount(String.valueOf(state.getSelectionCount()));
                    break;
                case GONE:
                    binding.mediasendSendButtonBkg.setVisibility(View.GONE);
                    binding.mediasendCountButton.getRoot().setVisibility(View.GONE);
                    break;
            }

            switch (state.getRailState()) {
                case INTERACTIVE:
                    binding.mediasendMediaRail.setVisibility(View.VISIBLE);
                    mediaRailAdapter.setEditable(true);
                    mediaRailAdapter.setInteractive(true);
                    break;
                case VIEWABLE:
                    binding.mediasendMediaRail.setVisibility(View.VISIBLE);
                    mediaRailAdapter.setEditable(false);
                    mediaRailAdapter.setInteractive(false);
                    break;
                case GONE:
                    binding.mediasendMediaRail.setVisibility(View.GONE);
                    break;
            }
        });

        viewModel.getSelectedMedia().observe(this, medias ->
                mediaRailAdapter.setMedia(medias));

        viewModel.getPosition().observe(this, position -> {
            if (position == null || position < 0) return;
            mediaRailAdapter.setActivePosition(position);
            binding.mediasendMediaRail.smoothScrollToPosition(position);
        });

        viewModel.getBucketId().observe(this, bucketId -> {
            if (bucketId == null) return;
            mediaRailAdapter.setAddButtonListener(() -> onAddMediaClicked(bucketId));
        });

        viewModel.getError().observe(this, error -> {
            if (error == null) return;

            switch (error) {
                case NO_ITEMS:
                    onNoMediaAvailable();
                    break;
                case ITEM_TOO_LARGE:
                    Toast.makeText(this, R.string.MediaSendActivity_an_item_was_removed_because_it_exceeded_the_size_limit, Toast.LENGTH_LONG).show();
                    break;
                case TOO_MANY_ITEMS:
                    int maxSelection = viewModel.getMaxSelection();
                    Toast.makeText(this, getResources().getQuantityString(R.plurals.MediaSendActivity_cant_share_more_than_n_items, maxSelection, maxSelection), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getEvents().observe(this, event -> {
            switch (event) {
                case SHOW_RENDER_PROGRESS:
                    progressDialog = SimpleProgressDialog.show(new ContextThemeWrapper(
                            MediaSendActivity.this, R.style.TextSecure_MediaSendProgressDialog));
                    break;
                case HIDE_RENDER_PROGRESS:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
            }
        });
    }

    private void navigateToMediaSend() {
        MediaSendFragment fragment = MediaSendFragment.newInstance();
        String backstackTag = null;

        if (getSupportFragmentManager().findFragmentByTag(TAG_SEND) != null) {
            getSupportFragmentManager().popBackStack(TAG_SEND, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            backstackTag = TAG_SEND;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mediasend_fragment_container, fragment, TAG_SEND)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(backstackTag)
                .commit();
    }

    private void navigateToCamera() {
        Permissions.with(this)
                .request(Manifest.permission.CAMERA)
                .ifNecessary()
                .withRationaleDialog(getString(R.string.ConversationActivity_to_capture_photos_and_video_allow_hahalolo_access_to_the_camera), R.drawable.ic_photo_camera)
                .withPermanentDenialDialog(getString(R.string.ConversationActivity_hahalolo_needs_the_camera_permission_to_take_photos_or_video))
                .onAllGranted(() -> {
                    Fragment fragment = getOrCreateCameraFragment();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.mediasend_fragment_container, fragment, TAG_CAMERA)
                            .addToBackStack(null)
                            .commit();
                })
                .onAnyDenied(() -> Toast.makeText(MediaSendActivity.this, R.string.ConversationActivity_hahalolo_needs_camera_permissions_to_take_photos_or_video, Toast.LENGTH_LONG).show())
                .execute();
    }

    private Fragment getOrCreateCameraFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CAMERA);
        return fragment != null ? fragment : CameraFragment.newInstance();
    }

    private @Nullable
    MediaSendFragment getMediaSendFragment() {
        return (MediaSendFragment) getSupportFragmentManager().findFragmentByTag(TAG_SEND);
    }

    private void setActivityResultAndFinish(@NonNull MediaSendActivityResult result) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.stationary, R.anim.camera_slide_to_bottom);
    }

    /*Camera Listener*/

    @Override
    public void onCameraError() {
        Toast.makeText(this, R.string.MediaSendActivity_camera_unavailable, Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onVideoCaptureError() {
        Vibrator vibrator = ServiceUtil.getVibrator(this);
        vibrator.vibrate(50);
    }

    @Override
    public void onImageCaptured(@NonNull byte[] data, int width, int height) {
        Timber.i("Camera image captured.");
        onMediaCaptured(() -> data,
                ignored -> (long) data.length,
                (blobProvider, bytes, ignored) -> blobProvider.forData(bytes),
                MediaUtil.IMAGE_JPEG,
                width,
                height);
    }

    @Override
    public void onVideoCaptured(@NonNull FileDescriptor fd) {
        Timber.i("Camera video captured.");
        onMediaCaptured(() -> new FileInputStream(fd),
                fin -> fin.getChannel().size(),
                BlobProvider::forData,
                VideoUtil.RECORDED_VIDEO_CONTENT_TYPE,
                0,
                0);
    }

    @Override
    public int getDisplayRotation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void onCameraCountButtonClicked() {
        navigateToMediaSend();
    }

    @Override
    public void onGalleryClicked() {
        MediaPickerFolderFragment folderFragment = MediaPickerFolderFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mediasend_fragment_container, folderFragment, TAG_FOLDER_PICKER)
                .setCustomAnimations(R.anim.slide_from_bottom, R.anim.stationary, R.anim.slide_to_bottom, R.anim.stationary)
                .addToBackStack(null)
                .commit();
    }

    private <T> void onMediaCaptured(Supplier<T> dataSupplier,
                                     IOFunction<T, Long> getLength,
                                     Function3<BlobProvider, T, Long, BlobProvider.BlobBuilder> createBlobBuilder,
                                     String mimeType,
                                     int width,
                                     int height) {
        SimpleTask.run(getLifecycle(), () -> {
            try {
                T data = dataSupplier.get();
                long length = getLength.apply(data);

                Uri uri = createBlobBuilder.apply(BlobProvider.getInstance(), data, length)
                        .withMimeType(mimeType)
                        .createForSingleSessionOnDisk(this);

                return new Media(
                        "",
                        uri,
                        mimeType,
                        System.currentTimeMillis(),
                        width,
                        height,
                        length,
                        0,
                        Optional.of(Media.ALL_MEDIA_BUCKET_ID),
                        "",
                        false
                );
            } catch (IOException e) {
                return null;
            }
        }, media -> {
            if (media == null) {
                onNoMediaAvailable();
                return;
            }
            viewModel.onMediaCaptured(media);
            navigateToMediaSend();
        });
    }
}
