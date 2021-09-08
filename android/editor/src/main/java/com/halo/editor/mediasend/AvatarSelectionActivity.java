package com.halo.editor.mediasend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.common.base.Optional;
import com.hahalolo.playcore.split.DaggerSplitActivity;
import com.halo.di.ActivityScoped;
import com.halo.editor.R;
import com.halo.editor.camera.Camera1Fragment;
import com.halo.editor.camera.CameraFragment;
import com.halo.editor.camera.CameraXFragment;
import com.halo.editor.camera.CameraXUtil;
import com.halo.editor.imageeditor.model.EditorModel;
import com.halo.editor.mediasend.picker.MediaPickerFolderFragment;
import com.halo.editor.mediasend.picker.MediaPickerItemFragment;
import com.halo.editor.providers.BlobProvider;
import com.halo.editor.scribbles.ImageEditorFragment;
import com.halo.editor.util.MediaUtil;
import java.io.FileDescriptor;
import java.util.Collections;
import javax.inject.Inject;
import dagger.Lazy;

@ActivityScoped
public class AvatarSelectionActivity extends DaggerSplitActivity
        implements CameraFragment.Controller, ImageEditorFragment.Controller,
        MediaPickerFolderFragment.Controller, MediaPickerItemFragment.Controller {

    private static final Point AVATAR_DIMENSIONS = new Point(1024, 1024);

    private static final String IMAGE_CAPTURE = "IMAGE_CAPTURE";
    private static final String IMAGE_EDITOR = "IMAGE_EDITOR";
    private static final String ARG_GALLERY = "ARG_GALLERY";

    public static final String ARG_IMAGE_URI = "ARG_IMAGE_URI";

    public static final String EXTRA_MEDIA = "avatar.media";
    public static final String EXTRA_IMAGE_URI = "avatar.media.uri";

    private Media currentMedia;

    @Inject
    ViewModelProvider.Factory factory;

    MediaSendViewModel viewModel;

    @Inject
    Lazy<Camera1Fragment> camera1FragmentLazy;
    @Inject
    Lazy<CameraXFragment> cameraxFragmentLazy;

    public static Intent getIntentForCameraCapture(@NonNull Context context) {
        return new Intent(context, AvatarSelectionActivity.class);
    }

    public static Intent getIntentForGallery(@NonNull Context context) {
        Intent intent = getIntentForCameraCapture(context);

        intent.putExtra(ARG_GALLERY, true);

        return intent;
    }

    public static Intent getIntentForUriResult(@NonNull Context context) {
        Intent intent = getIntentForCameraCapture(context);
        intent.putExtra(ARG_GALLERY, true);
        intent.putExtra(ARG_IMAGE_URI, true);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_selection_activity);

        viewModel = new ViewModelProvider(this, factory).get(MediaSendViewModel.class);

        if (isGalleryFirst()) {
            onGalleryClicked();
        } else {
            onCameraSelected();
        }
    }

    @Override
    public void onCameraError() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onImageCaptured(@NonNull byte[] data, int width, int height) {
        Uri blobUri = BlobProvider.getInstance()
                .forData(data)
                .withMimeType(MediaUtil.IMAGE_JPEG)
                .createForSingleSessionInMemory();

        onMediaSelected(new Media(
                "",
                blobUri,
                MediaUtil.IMAGE_JPEG,
                System.currentTimeMillis(),
                width,
                height,
                data.length,
                0,
                Optional.of(Media.ALL_MEDIA_BUCKET_ID),
                "",
                false));
    }

    @Override
    public void onVideoCaptured(@NonNull FileDescriptor fd) {
        throw new UnsupportedOperationException("Cannot set profile as video");
    }

    @Override
    public void onVideoCaptureError() {
        throw new AssertionError("This should never happen");
    }

    @Override
    public void onGalleryClicked() {
        if (isGalleryFirst() && popToRoot()) {
            return;
        }

        MediaPickerFolderFragment fragment = MediaPickerFolderFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (isCameraFirst()) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public int getDisplayRotation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void onCameraCountButtonClicked() {
        throw new UnsupportedOperationException("Cannot select more than one photo");
    }

    @Override
    public void onTouchEventsNeeded(boolean needed) {
    }

    @Override
    public void onRequestFullScreen(boolean fullScreen, boolean hideKeyboard) {
    }

    @Override
    public void onFolderSelected(@NonNull MediaFolder folder) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, MediaPickerItemFragment.newInstance(
                        folder.getBucketId(), folder.getTitle(),
                        1, false))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMediaSelected(@NonNull Media media) {
        currentMedia = media;
        if (media.getUri() != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            ImageEditorFragment.newInstanceForAvatar(media.getUri()), IMAGE_EDITOR)
                    .addToBackStack(IMAGE_EDITOR)
                    .commit();
        }
    }

    @Override
    public void onCameraSelected() {
        if (isCameraFirst() && popToRoot()) {
            return;
        }

        Fragment fragment;
        if (CameraXUtil.isSupported()) {
            fragment = cameraxFragmentLazy.get();
        } else {
            fragment = camera1FragmentLazy.get();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, IMAGE_CAPTURE);

        if (isGalleryFirst()) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onDoneEditing() {
        if (isFileUriResult()) {
            handleReturnFilePath();
        } else {
            handleSave();
        }
    }

    public boolean popToRoot() {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 0) {
            return false;
        }

        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStack();
        }

        return true;
    }

    private boolean isGalleryFirst() {
        return getIntent().getBooleanExtra(ARG_GALLERY, false);
    }

    private boolean isCameraFirst() {
        return !isGalleryFirst();
    }

    private boolean isFileUriResult() {
        return getIntent().getBooleanExtra(ARG_IMAGE_URI, false);
    }

    private void handleSave() {
        ImageEditorFragment fragment = (ImageEditorFragment) getSupportFragmentManager().findFragmentByTag(IMAGE_EDITOR);
        if (fragment == null) {
            throw new AssertionError();
        }

        ImageEditorFragment.Data data = (ImageEditorFragment.Data) fragment.saveState();

        EditorModel model = data.readModel();
        if (model == null) {
            throw new AssertionError();
        }

        MediaRepository.transformMedia(this,
                Collections.singletonList(currentMedia),
                Collections.singletonMap(currentMedia, new ImageEditorModelRenderMediaTransform(model, AVATAR_DIMENSIONS)),
                output -> {
                    Media transformed = output.get(currentMedia);

                    Intent result = new Intent();
                    result.putExtra(EXTRA_MEDIA, transformed);
                    setResult(RESULT_OK, result);
                    finish();
                });
    }

    private void handleReturnFilePath() {
        Uri filePath = null;
        if (currentMedia != null && currentMedia.getUri() != null) {
            filePath = currentMedia.getUri();
        }
        Intent result = new Intent();
        result.putExtra(EXTRA_IMAGE_URI, filePath);
        setResult(RESULT_OK, result);
        finish();
    }
}
