/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils.media;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.halo.R;
import com.halo.common.permission.RxPermissions;
import com.halo.common.utils.FilenameUtils;
import com.halo.common.utils.ThumbImageUtils;
import com.halo.data.entities.media.MediaEntity;
import com.halo.okdownload.DownloadTask;
import com.halo.presentation.MessApplication;
import com.halo.presentation.utils.glide.GlideApp;
import com.halo.presentation.utils.upload.DownloadMediaUtils;
import com.halo.widget.imageviewer.ImageViewer;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MediaViewerUtils {

    static ImageViewer viewer;

    public static ImageViewer build(Activity activity,
                                    List<MediaEntity> entities,
                                    int positionStart,
                                    ImageView target) {
        return build(activity, entities, positionStart, target, null);
    }

    public static ImageViewer buildUri(Activity activity,
                                    List<Uri> entities,
                                    int positionStart,
                                    ImageView target) {
        return buildUri(activity, entities, positionStart, target, null);
    }

    private static void loadImageDefault(@NonNull ImageView imageView,
                                         RequestListener<Drawable> listener,
                                         MediaEntity image) {
        String path = ThumbImageUtils.thumb(
                MessApplication.getInstance().WIDTH_SCREEN > ThumbImageUtils.Size.MEDIA_WIDTH_1080 ?
                        ThumbImageUtils.Size.MEDIA_WIDTH_1920 :
                        ThumbImageUtils.Size.MEDIA_WIDTH_1080,
                image.getPath(),
                ThumbImageUtils.TypeSize._AUTO);

        GlideApp.with(imageView.getContext())
                .load(path)
                .apply(RequestOptions.fitCenterTransform())
                .addListener(listener)
                .into(imageView);
    }


    private static void loadImageDefaultUri(@NonNull ImageView imageView,
                                         RequestListener<Drawable> listener,
                                         Uri image) {
        GlideApp.with(imageView.getContext())
                .load(image)
                .apply(RequestOptions.fitCenterTransform())
                .addListener(listener)
                .into(imageView);
    }

    public static ImageViewer build(@NonNull Activity activity,
                                    @NonNull List<MediaEntity> entities,
                                    int positionStart,
                                    ImageView target,
                                    ImageViewerListener listener) {

        if (positionStart >= entities.size()) {
            positionStart = 0;
        }

        viewer = new ImageViewer.Builder<>(activity, entities, (imageView, listenerDrawable,  image) -> {
            if (listener != null) {
                listener.loadPosterImage(imageView,listenerDrawable,  image);
            } else {
                loadImageDefault(imageView, listenerDrawable, image);
            }
        }).withStartPosition(positionStart)
                .withTransitionFrom(target)
                .withImageChangeListener(position -> {
                    if (listener != null) {
                        ImageView imageView = listener.onImageChange(position);
                        if (imageView != null && viewer != null) {
                            viewer.updateTransitionImage(imageView);
                        }
                    }
                })
                .withDownloadListener(o -> {
                    if (o != null && o instanceof MediaEntity) {
                        if (listener != null) {
                            listener.onDownload(activity, (MediaEntity) o);
                        } else {
                            onDownloadDefault(activity, (MediaEntity) o);
                        }
                    }
                })
                .show();

        return viewer;
    }

    public static ImageViewer buildUri(@NonNull Activity activity,
                                    @NonNull List<Uri> entities,
                                    int positionStart,
                                    ImageView target,
                                    UriImageViewerListener listener) {

        if (positionStart >= entities.size()) {
            positionStart = 0;
        }

        viewer = new ImageViewer.Builder<>(activity, entities, (imageView, listenerDrawable,  image) -> {
            if (listener != null) {
                listener.loadPosterImage(imageView,listenerDrawable,  image);
            } else {
                loadImageDefaultUri(imageView, listenerDrawable, image);
            }
        }).withStartPosition(positionStart)
                .withTransitionFrom(target)
                .withImageChangeListener(position -> {
                    if (listener != null) {
                        ImageView imageView = listener.onImageChange(position);
                        if (imageView != null && viewer != null) {
                            viewer.updateTransitionImage(imageView);
                        }
                    }
                })
                .withDownloadListener(o -> {

                })
                .show();
        return viewer;
    }

    public static ImageViewer build(@NonNull Activity activity,
                                    @NonNull List<MediaEntity> entities,
                                    List<View> imageViewList,
                                    int positionStart) {
        return build(activity, entities, imageViewList, positionStart, null);
    }

    public static ImageViewer buildUri(@NonNull Activity activity,
                                    @NonNull List<Uri> entities,
                                    List<View> imageViewList,
                                    int positionStart) {
        return buildUri(activity, entities, imageViewList, positionStart, null);
    }

    public static ImageViewer build(@NonNull Activity activity,
                                    @NonNull List<MediaEntity> entities,
                                    List<View> imageViewList,
                                    int positionStart,
                                    ImageViewerListener listener) {
        if (positionStart >= entities.size()) {
            positionStart = 0;
        }
        ImageView target = null;
        if (imageViewList != null
                && !imageViewList.isEmpty()
                && imageViewList.size() > positionStart
                && imageViewList.get(positionStart) instanceof ImageView) {
            target = (ImageView) imageViewList.get(positionStart);
        }

        viewer = new ImageViewer.Builder<>(activity, entities, (imageView, listenerDrawable, image) -> {
            if (listener != null) {
                listener.loadPosterImage(imageView, listenerDrawable, image);
            } else {
                loadImageDefault(imageView,listenerDrawable,  image);
            }
        }).withStartPosition(positionStart)
                .withTransitionFrom(target)
                .withImageChangeListener(position -> {
                    if (imageViewList != null
                            && !imageViewList.isEmpty()
                            && imageViewList.size() > position
                            && imageViewList.get(position) instanceof ImageView
                            && viewer != null) {
                        viewer.updateTransitionImage((ImageView) imageViewList.get(position));
                    }
                })
                .withDownloadListener(o -> {
                    if (o != null && o instanceof MediaEntity) {
                        if (listener != null) {
                            listener.onDownload(activity, (MediaEntity) o);
                        } else {
                            onDownloadDefault(activity, (MediaEntity) o);
                        }
                    }
                }).show();
        return viewer;
    }

    public static ImageViewer buildUri(@NonNull Activity activity,
                                    @NonNull List<Uri> entities,
                                    List<View> imageViewList,
                                    int positionStart,
                                    UriImageViewerListener listener) {
        if (positionStart >= entities.size()) {
            positionStart = 0;
        }
        ImageView target = null;
        if (imageViewList != null
                && !imageViewList.isEmpty()
                && imageViewList.size() > positionStart
                && imageViewList.get(positionStart) instanceof ImageView) {
            target = (ImageView) imageViewList.get(positionStart);
        }

        viewer = new ImageViewer.Builder<>(activity, entities, (imageView, listenerDrawable, image) -> {
            if (listener != null) {
                listener.loadPosterImage(imageView, listenerDrawable, image);
            } else {
                loadImageDefaultUri(imageView,listenerDrawable,  image);
            }
        }).withStartPosition(positionStart)
                .withTransitionFrom(target)
                .withImageChangeListener(position -> {
                    if (imageViewList != null
                            && !imageViewList.isEmpty()
                            && imageViewList.size() > position
                            && imageViewList.get(position) instanceof ImageView
                            && viewer != null) {
                        viewer.updateTransitionImage((ImageView) imageViewList.get(position));
                    }
                })
                .withDownloadListener(o -> {

                }).show();
        return viewer;
    }

    public interface ImageViewerListener {
        default void loadPosterImage(ImageView imageView, RequestListener<Drawable> listener,  MediaEntity image) {
            loadImageDefault(imageView, listener, image);
        }

        default ImageView onImageChange(int position) {
            return null;
        }

        default void onDownload(@NonNull Activity activity, @NonNull MediaEntity mediaEntity) {
            onDownloadDefault(activity, mediaEntity);
        }
    }

    public interface UriImageViewerListener {
        default void loadPosterImage(ImageView imageView, RequestListener<Drawable> listener,  Uri image) {
            loadImageDefaultUri(imageView, listener, image);
        }

        default ImageView onImageChange(int position) {
            return null;
        }
    }

    static void onDownloadDefault(Activity activity, MediaEntity mediaEntity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Do nothing
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            if (mediaEntity != null) {
                                onDownloadMedia(activity, mediaEntity);
                            } else {
                                Toast.makeText(activity, R.string.media_viewer_media_error, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(activity, R.string.editor_permission_media_request_denied, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Do nothing
                    }

                    @Override
                    public void onComplete() {
                        // Do nothing
                    }
                });
    }

    private static void onDownloadMedia(Activity activity, MediaEntity mediaEntity) {
        if (mediaEntity.getPath() == null) return;
        DownloadMediaUtils.downloadUrl(new DownloadMediaUtils.DownloadUrlListener() {
            @NonNull
            @Override
            public String getPath() {
                return mediaEntity.getPath();
            }

            @Override
            public long getSize() {
                long size = 0;
                try {
                    size = Long.parseLong(mediaEntity.getSize());
                } catch (Exception e) {

                }
                return size;
            }

            @NonNull
            @Override
            public String getName() {
                return FilenameUtils.getName(mediaEntity.getPath());
            }

            @Override
            public void onStart(DownloadTask task) {
            }

            @Override
            public void onProgress(DownloadTask task, long progress) {
            }

            @Override
            public void onComplete(DownloadTask task, String localMedia) {
                Toast.makeText(activity, activity.getString(R.string.media_viewer_save_to) + localMedia, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
