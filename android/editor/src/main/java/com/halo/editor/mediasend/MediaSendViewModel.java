/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.mediasend;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.halo.constant.HaloConfig;
import com.halo.editor.providers.BlobProvider;
import com.halo.editor.util.MediaConstraints;
import com.halo.editor.util.MediaUtil;
import com.halo.editor.util.SingleLiveEvent;
import com.halo.editor.util.Util;
import com.halo.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Manages the observable datasets available in {@link MediaSendActivity}.
 */
public class MediaSendViewModel extends ViewModel {

    private static final String TAG = MediaSendViewModel.class.getSimpleName();

    private final Application application;
    private final MediaRepository repository;
    private final MutableLiveData<List<Media>> selectedMedia;
    private final MutableLiveData<List<Media>> bucketMedia;
    private final MutableLiveData<Optional<Media>> mostRecentMedia;
    private final MutableLiveData<Integer> position;
    private final MutableLiveData<String> bucketId;
    private final MutableLiveData<List<MediaFolder>> folders;
    private final MutableLiveData<HudState> hudState;
    private final SingleLiveEvent<Error> error;
    private final SingleLiveEvent<Event> event;
    private final Map<Uri, Object> savedDrawState;
    private MediaConstraints mediaConstraints;

    private Page page;
    private Optional<Media> lastCameraCapture;

    private boolean hudVisible;
    private ButtonState buttonState;
    private RailState railState;
    private ViewOnceState viewOnceState;

    @Inject
    MediaSendViewModel(@NonNull MediaRepository repository,
                       @NonNull Application application) {
        this.application = application;
        this.repository = repository;
        this.selectedMedia = new MutableLiveData<>();
        this.bucketMedia = new MutableLiveData<>();
        this.mostRecentMedia = new MutableLiveData<>();
        this.position = new MutableLiveData<>();
        this.bucketId = new MutableLiveData<>();
        this.folders = new MutableLiveData<>();
        this.hudState = new MutableLiveData<>();
        this.error = new SingleLiveEvent<>();
        this.event = new SingleLiveEvent<>();
        this.savedDrawState = new HashMap<>();
        this.lastCameraCapture = Optional.absent();
        this.buttonState = ButtonState.GONE;
        this.railState = RailState.GONE;
        this.viewOnceState = ViewOnceState.GONE;
        this.page = Page.UNKNOWN;
        mediaConstraints = MediaConstraints.getPushMediaConstraints();

        this.position.setValue(-1);
    }

    /**
     * Khi người dùng thực hiện hành động thay đổi media
     *
     * @param context  contex
     * @param newMedia media mà người dùng thay đổi
     */
    public void onSelectedMediaChanged(@NonNull Context context, @NonNull List<Media> newMedia) {
        if (!newMedia.isEmpty()) {
            selectedMedia.setValue(newMedia);
        }

        repository.getPopulatedMedia(context, newMedia, populatedMedia -> Util.runOnMain(() -> {
            if (!populatedMedia.isEmpty()) {
                List<Media> filteredMedia = getFilteredMedia(context, populatedMedia, mediaConstraints);

                if (filteredMedia.size() != newMedia.size()) {
                    error.setValue(Error.ITEM_TOO_LARGE);
                }

                if (filteredMedia.size() > HaloConfig.MEDIA_MAX_SELECT) {
                    filteredMedia = filteredMedia.subList(0, HaloConfig.MEDIA_MAX_SELECT);
                    error.setValue(Error.TOO_MANY_ITEMS);
                }

                if (filteredMedia.size() > 0) {
                    String computedId = Stream.of(filteredMedia)
                            .skip(1)
                            .reduce(filteredMedia.get(0).getBucketId().or(Media.ALL_MEDIA_BUCKET_ID), (id, m) -> {
                                if (Util.equals(id, m.getBucketId().or(Media.ALL_MEDIA_BUCKET_ID))) {
                                    return id;
                                } else {
                                    return Media.ALL_MEDIA_BUCKET_ID;
                                }
                            });
                    bucketId.setValue(computedId);
                } else {
                    bucketId.setValue(Media.ALL_MEDIA_BUCKET_ID);
                }

                if (page == Page.EDITOR && filteredMedia.isEmpty()) {
                    error.postValue(Error.NO_ITEMS);
                } else if (filteredMedia.isEmpty()) {
                    hudVisible = false;
                    selectedMedia.setValue(filteredMedia);
                    hudState.setValue(buildHudState());
                } else {
                    hudVisible = true;
                    selectedMedia.setValue(filteredMedia);
                    hudState.setValue(buildHudState());
                }
            }
        }));
    }

    /**
     * Nếu trường hợp không cho chọn nhiều media, khi người dùng click chọn 1 media
     *
     * @param context context
     * @param media   Media mà người dùng đã chọn
     */
    void onSingleMediaSelected(@NonNull Context context, @NonNull Media media) {
        selectedMedia.setValue(Collections.singletonList(media));

        repository.getPopulatedMedia(context,
                Collections.singletonList(media),
                populatedMedia -> Util.runOnMain(() -> {
                    List<Media> filteredMedia = getFilteredMedia(context, populatedMedia, mediaConstraints);

                    if (filteredMedia.isEmpty()) {
                        error.setValue(Error.ITEM_TOO_LARGE);
                        bucketId.setValue(Media.ALL_MEDIA_BUCKET_ID);
                    } else {
                        bucketId.setValue(filteredMedia.get(0).getBucketId().or(Media.ALL_MEDIA_BUCKET_ID));
                    }

                    selectedMedia.setValue(filteredMedia);
                }));
    }

    public void onMultiSelectStarted() {
        hudVisible = true;
        buttonState = ButtonState.COUNT;
        railState = RailState.VIEWABLE;
        viewOnceState = ViewOnceState.GONE;

        hudState.setValue(buildHudState());
    }

    public void onImageEditorStarted() {
        page = Page.EDITOR;
        hudVisible = true;
        buttonState = ButtonState.SEND;

        if (viewOnceState == ViewOnceState.GONE && viewOnceSupported()) {
            viewOnceState = ViewOnceState.DISABLED;
        } else if (!viewOnceSupported()) {
            viewOnceState = ViewOnceState.GONE;
        }

        railState = viewOnceState != ViewOnceState.ENABLED ? RailState.INTERACTIVE : RailState.GONE;

        hudState.setValue(buildHudState());
    }

    public void onCameraStarted() {
        Page previous = page;

        page = Page.CAMERA;
        hudVisible = false;
        viewOnceState = ViewOnceState.GONE;
        buttonState = ButtonState.COUNT;

        List<Media> selected = getSelectedMediaOrDefault();

        if (previous == Page.EDITOR
                && lastCameraCapture.isPresent()
                && selected.contains(lastCameraCapture.get()) && selected.size() == 1) {
            selected.remove(lastCameraCapture.get());
            selectedMedia.setValue(selected);
        }

        hudState.setValue(buildHudState());
    }

    public void onItemPickerStarted() {
        page = Page.ITEM_PICKER;
        hudVisible = true;
        buttonState = ButtonState.COUNT;
        viewOnceState = ViewOnceState.GONE;
        railState = getSelectedMediaOrDefault().isEmpty() ? RailState.GONE : RailState.VIEWABLE;

        lastCameraCapture = Optional.absent();

        hudState.setValue(buildHudState());
    }

    public void onFolderPickerStarted() {
        page = Page.FOLDER_PICKER;
        hudVisible = true;
        buttonState = ButtonState.COUNT;
        viewOnceState = ViewOnceState.GONE;
        railState = getSelectedMediaOrDefault().isEmpty() ? RailState.GONE : RailState.VIEWABLE;

        lastCameraCapture = Optional.absent();

        hudState.setValue(buildHudState());
    }

    void onFolderSelected(@NonNull String bucketId) {
        this.bucketId.setValue(bucketId);
        bucketMedia.setValue(Collections.emptyList());
    }

    public void onPageChanged(int position) {
        if (position < 0 || position >= getSelectedMediaOrDefault().size()) {
            Timber.tag(TAG).w("Tried to move to an out-of-bounds item. Size: "
                    + getSelectedMediaOrDefault().size() + ", position: " + position);
            return;
        }

        this.position.setValue(position);
    }

    void onMediaItemRemoved(int position) {
        if (position < 0 || position >= getSelectedMediaOrDefault().size()) {
            Timber.tag(TAG).w("Tried to remove an out-of-bounds item. Size: "
                    + getSelectedMediaOrDefault().size() + ", position: " + position);
            return;
        }

        Media removed = getSelectedMediaOrDefault().remove(position);

        if (removed != null
                && removed.getUri() != null
                && BlobProvider.isAuthority(removed.getUri())) {
            BlobProvider.getInstance().delete(application, removed.getUri());
        }

        if (page == Page.EDITOR && getSelectedMediaOrDefault().isEmpty()) {
            error.setValue(Error.NO_ITEMS);
        } else {
            selectedMedia.setValue(selectedMedia.getValue());
        }

        if (getSelectedMediaOrDefault().size() > 0) {
            this.position.setValue(Math.min(position, getSelectedMediaOrDefault().size() - 1));
        }

        if (getSelectedMediaOrDefault().size() == 1) {
            viewOnceState = viewOnceSupported() ? ViewOnceState.DISABLED : ViewOnceState.GONE;
        }

        hudState.setValue(buildHudState());
    }

    public void onMediaCaptured(@NonNull Media media) {
        lastCameraCapture = Optional.of(media);

        List<Media> selected = selectedMedia.getValue();

        if (selected == null) {
            selected = new LinkedList<>();
        }

        if (selected.size() >= HaloConfig.MEDIA_MAX_SELECT) {
            error.setValue(Error.TOO_MANY_ITEMS);
            return;
        }

        selected.add(media);
        selectedMedia.setValue(selected);
        position.setValue(selected.size() - 1);
        bucketId.setValue(Media.ALL_MEDIA_BUCKET_ID);
    }

    public void saveDrawState(@NonNull Map<Uri, Object> state) {
        savedDrawState.clear();
        savedDrawState.putAll(state);
    }

    @NonNull
    LiveData<MediaSendActivityResult> onSendClicked(Map<Media, MediaTransform> modelsToTransform) {
        MutableLiveData<MediaSendActivityResult> result = new MutableLiveData<>();
        Runnable dialogRunnable = () -> event.postValue(Event.SHOW_RENDER_PROGRESS);

        List<Media> initialMedia = getSelectedMediaOrDefault();

        Preconditions.checkState(initialMedia.size() > 0, "No media to send!");

        Util.runOnMainDelayed(dialogRunnable, 250);

        MediaRepository.transformMedia(application, initialMedia, modelsToTransform, (oldToNew) -> {
            Util.cancelRunnableOnMain(dialogRunnable);

            List<Media> updatedMedia = new ArrayList<>(oldToNew.values());
            result.postValue(MediaSendActivityResult.createResult(updatedMedia));
            // Send data result to pre activity
        });

        return result;
    }

    @NonNull
    public Map<Uri, Object> getDrawState() {
        return savedDrawState;
    }

    @NonNull
    public LiveData<List<Media>> getSelectedMedia() {
        return selectedMedia;
    }

    @NonNull
    public LiveData<List<Media>> getMediaInBucket(@NonNull Context context, @NonNull String bucketId) {
        repository.getMediaInBucket(context, bucketId, bucketMedia::postValue);
        return bucketMedia;
    }

    @NonNull
    public LiveData<List<MediaFolder>> getFolders(@NonNull Context context) {
        repository.getFolders(context, folders::postValue);
        return folders;
    }

    public void onCameraControlsInitialized() {
        repository.getMostRecentItem(application, mostRecentMedia::postValue);
    }

    @NonNull
    public MediaConstraints getMediaConstraints() {
        return mediaConstraints;
    }

    @NonNull
    public LiveData<Optional<Media>> getMostRecentMediaItem() {
        return mostRecentMedia;
    }

    @NonNull
    public LiveData<Integer> getPosition() {
        return position;
    }

    @NonNull
    public LiveData<String> getBucketId() {
        return bucketId;
    }

    @NonNull
    public LiveData<Error> getError() {
        return error;
    }

    @NonNull
    public LiveData<Event> getEvents() {
        return event;
    }

    @NonNull
    public LiveData<HudState> getHudState() {
        return hudState;
    }

    int getMaxSelection() {
        return HaloConfig.MEDIA_MAX_SELECT;
    }

    private @NonNull
    List<Media> getSelectedMediaOrDefault() {
        return selectedMedia.getValue() == null ? Collections.emptyList()
                : selectedMedia.getValue();
    }

    private @NonNull
    List<Media> getFilteredMedia(@NonNull Context context,
                                 @NonNull List<Media> media,
                                 @NonNull MediaConstraints mediaConstraints) {
        return Stream.of(media).filter(m -> MediaUtil.isGif(m.getMimeType()) ||
                MediaUtil.isImageType(m.getMimeType()) ||
                MediaUtil.isVideoType(m.getMimeType()))
                .filter(m -> (MediaUtil.isImageType(m.getMimeType()) && !MediaUtil.isGif(m.getMimeType())) ||
                        (MediaUtil.isGif(m.getMimeType()) && m.getSize() < mediaConstraints.getGifMaxSize(context)) ||
                        (MediaUtil.isVideoType(m.getMimeType()) && m.getSize() < mediaConstraints.getUncompressedVideoMaxSize(context))).toList();

    }

    private HudState buildHudState() {
        List<Media> selectedMedia = getSelectedMediaOrDefault();
        int selectionCount = selectedMedia.size();
        ButtonState updatedButtonState = buttonState == ButtonState.COUNT && selectionCount == 0 ? ButtonState.GONE : buttonState;
        return new HudState(hudVisible, selectionCount, updatedButtonState, railState, viewOnceState);
    }

    private boolean viewOnceSupported() {
        return mediaSupportsRevealableMessage(getSelectedMediaOrDefault());
    }

    private boolean mediaSupportsRevealableMessage(@NonNull List<Media> media) {
        if (media.size() != 1) return false;
        return MediaUtil.isImageOrVideoType(media.get(0).getMimeType());
    }

    public enum Error {
        ITEM_TOO_LARGE, TOO_MANY_ITEMS, NO_ITEMS
    }

    public enum Event {
        SHOW_RENDER_PROGRESS, HIDE_RENDER_PROGRESS
    }

    public enum Page {
        CAMERA, ITEM_PICKER, FOLDER_PICKER, EDITOR, CONTACT_SELECT, UNKNOWN
    }

    public enum ButtonState {
        COUNT, SEND, GONE
    }

    public enum RailState {
        INTERACTIVE, VIEWABLE, GONE
    }

    public enum ViewOnceState {
        ENABLED, DISABLED, GONE
    }

    public static class HudState {

        private final boolean hudVisible;
        private final int selectionCount;
        private final ButtonState buttonState;
        private final RailState railState;
        private final ViewOnceState viewOnceState;

        public HudState(boolean hudVisible,
                        int selectionCount,
                        @NonNull ButtonState buttonState,
                        @NonNull RailState railState,
                        @NonNull ViewOnceState viewOnceState) {
            this.hudVisible = hudVisible;
            this.selectionCount = selectionCount;
            this.buttonState = buttonState;
            this.railState = railState;
            this.viewOnceState = viewOnceState;
        }

        public boolean isHudVisible() {
            return hudVisible;
        }

        public int getSelectionCount() {
            return selectionCount;
        }

        public @NonNull
        ButtonState getButtonState() {
            return buttonState;
        }

        public @NonNull
        RailState getRailState() {
            return hudVisible ? railState : RailState.GONE;
        }

        public @NonNull
        ViewOnceState getViewOnceState() {
            return hudVisible ? viewOnceState : ViewOnceState.GONE;
        }
    }
}
