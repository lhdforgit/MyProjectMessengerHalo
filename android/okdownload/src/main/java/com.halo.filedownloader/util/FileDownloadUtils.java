/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.util;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.halo.filedownloader.DownloadTaskAdapter;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.Util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The utils for FileDownloader.
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class FileDownloadUtils {

    private static String defaultSaveRootPath;
    private static final String TAG = "FileDownloadUtils";

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(defaultSaveRootPath)) {
            return defaultSaveRootPath;
        }

        if (FileDownloadHelper.getAppContext().getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            //noinspection ConstantConditions
            return FileDownloadHelper.getAppContext().getExternalCacheDir().getAbsolutePath();
        }
    }

    /**
     * The path is used as the default directory in the case of the task without set path.
     *
     * @param path default root path for save download file.
     * @see com.halo.filedownloader.BaseDownloadTask#setPath(String, boolean)
     */
    public static void setDefaultSaveRootPath(final String path) {
        defaultSaveRootPath = path;
    }

    public static String getDefaultSaveFilePath(final String url) {
        return generateFilePath(getDefaultSaveRootPath(), generateFileName(url));
    }

    public static String generateFileName(final String url) {
        return md5(url);
    }

    public static String generateFilePath(String directory, String filename) {
        if (filename == null) {
            throw new IllegalStateException("can't generate real path, the file name is null");
        }

        if (directory == null) {
            throw new IllegalStateException("can't generate real path, the directory is null");
        }

        return String.format("%s%s%s", directory, File.separator, filename);
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append(0);
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    @Nullable
    public static DownloadTaskAdapter findDownloadTaskAdapter(DownloadTask downloadTask) {
        if (downloadTask == null) {
            Util.w(TAG, "download task is null when find DownloadTaskAdapter");
            return null;
        }
        final Object o = downloadTask.getTag(DownloadTaskAdapter.KEY_TASK_ADAPTER);
        if (o == null) {
            Util.w(TAG, "no tag with DownloadTaskAdapter.KEY_TASK_ADAPTER");
            return null;
        }
        if (o instanceof DownloadTaskAdapter) {
            return (DownloadTaskAdapter) o;
        }
        Util.w(TAG, "download task's tag is not DownloadTaskAdapter");
        return null;
    }
}
