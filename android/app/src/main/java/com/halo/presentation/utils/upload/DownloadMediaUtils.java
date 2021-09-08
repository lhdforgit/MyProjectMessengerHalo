/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hahalolo.messager.presentation.message.CustomDownloadListener;
import com.halo.common.utils.HaloFileUtils;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.presentation.MessApplication;

import java.util.HashMap;
import java.util.UUID;

public class DownloadMediaUtils {
    private static HashMap<String, Long> cacheProgress = new HashMap<>();
    private static HashMap<String, Long> cacheSize = new HashMap<>();
    private static HashMap<String, String> cacheName = new HashMap<>();

    public static void downloadUrl(DownloadUrlListener downloadUrlListener) {
        if (downloadUrlListener != null) {
            String path = downloadUrlListener.getPath();
            long maxSize = downloadUrlListener.getSize();
            String name = downloadUrlListener.getName();

            DownloadTask task = new DownloadTask.Builder(path, HaloFileUtils.createFolderDownload())
                    .setFilename(name)
                    // the minimal interval millisecond for callback progress
                    .setMinIntervalMillisCallbackProcess(30)
                    // do re-download even if the task has already been completed in the past.
                    .setPassIfAlreadyCompleted(false)
                    .build();
            String key = UUID.randomUUID().toString();
            task.setTag(key);
            if (maxSize != 0) {
                cacheSize.put(key, maxSize);
            }
            cacheName.put(key, name);

            task.enqueue(new CustomDownloadListener() {
                @Override
                public void taskStart(@NonNull DownloadTask task) {
                    downloadUrlListener.onStart(task);
                }

                @Override
                public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
                    if (cacheProgress != null && task.getTag() != null) {
                        String key = String.valueOf(task.getTag());
                        Long progress = cacheProgress.get(key);
                        Long maxSize = cacheSize.get(key);
                        if (progress != null &&
                                maxSize != null) {
                            progress = progress + increaseBytes;
                            downloadUrlListener.onProgress(task, progress * 100 / maxSize);
                            cacheProgress.put(String.valueOf(task.getTag()), progress);
                        }
                    }
                }

                @Override
                public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                    String key = String.valueOf(task.getTag());
                    if (realCause == null) {
                        String fileName = HaloFileUtils.getLocalMedia(cacheName.get(key));
                        HaloFileUtils.addImageToGallery(HaloFileUtils.getPathLocalMedia(cacheName.get(key)), MessApplication.getInstance());
                        downloadUrlListener.onComplete(task, fileName);
                    } else {
                        downloadUrlListener.onError(realCause);
                    }
                    cacheProgress.remove(key);
                    cacheName.remove(key);
                    cacheSize.remove(key);
                }
            });

        }
    }

    public interface DownloadUrlListener {

        @NonNull
        String getPath();

        long getSize();

        @NonNull
        String getName();

        default void onStart(DownloadTask task) {
        }

        default void onProgress(DownloadTask task, long progress) {
        }


        void onComplete(DownloadTask task, String localMedia);

        default void onError(Exception realCause) {
        }
    }
}
