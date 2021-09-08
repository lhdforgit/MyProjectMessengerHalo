/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.halo.common.permission.RxPermissions;

import java.io.File;

import io.reactivex.disposables.Disposable;

public class HaloFileUtils {

    public static Uri addFileToGallery(@NonNull Context context, @NonNull Download download) {
        try {
            if (isPermission(context)
                    && !TextUtils.isEmpty(download.getLocalPath())
                    && !TextUtils.isEmpty(download.getKey())) {
                if (ThumbImageUtils.isImage(download.getLocalPath()) || ThumbImageUtils.isGif(download.getLocalPath())) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + FilenameUtils.getExtension(download.getLocalPath()));
                    values.put(MediaStore.MediaColumns.DATA, download.getLocalPath());
                    return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else if (ThumbImageUtils.isVideo(download.getLocalPath())) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/" + FilenameUtils.getExtension(download.getLocalPath()));
                    values.put(MediaStore.MediaColumns.DATA, download.getLocalPath());
                    return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, download.getKey());
                    values.put(MediaStore.Files.FileColumns.PARENT, download.getKey());
                    values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Files.FileColumns.DATA, download.getLocalPath());
                    Uri result;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        result = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                    } else {
                        result = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                    }
                    return result;
                }
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public static Uri addImageToGallery(final String filePath, final Context context) {
        try {
            if (isPermission(context)) {
                if (!TextUtils.isEmpty(filePath)) {
                    if (ThumbImageUtils.isImage(filePath) || ThumbImageUtils.isGif(filePath)) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                        }
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + FilenameUtils.getExtension(filePath));
                        values.put(MediaStore.MediaColumns.DATA, filePath);
                        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    } else if (ThumbImageUtils.isVideo(filePath)) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
                        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                        }
                        values.put(MediaStore.Video.Media.MIME_TYPE, "video/" + FilenameUtils.getExtension(filePath));
                        values.put(MediaStore.MediaColumns.DATA, filePath);
                        return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis());
                        values.put(MediaStore.Files.FileColumns.DATE_MODIFIED, System.currentTimeMillis());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Files.FileColumns.DATE_TAKEN, System.currentTimeMillis());
                        }
                        values.put(MediaStore.Files.FileColumns.DATA, filePath);
                        Uri result;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            result = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                        } else {
                            result = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                        }
                        return result;
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    private static final String currentFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator + "Hahalolo";

    private static boolean isPermission(Context context) {
        return context != null
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static Local getLocalFile(@NonNull String id, @NonNull Context context) {
        try {
            if (isPermission(context)) {
                Cursor cursor;
                String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "=?";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    cursor = context.getContentResolver().query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                            null,
                            selection,
                            new String[]{id},
                            null);
                } else {
                    cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                            null,
                            selection,
                            new String[]{id},
                            null);
                }
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String dataLocal = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                            String idLocal = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                            long dateLocal = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                            File file = new File(dataLocal);
                            if (file.exists()) {
                                cursor.close();
                                return new Local(idLocal, dateLocal, dataLocal, FilenameUtils.getName(dataLocal));
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return null;
                }
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public static boolean isFileDownLoaded(String id,
                                           String fileName,
                                           Context context) {

        if (fileName != null && !fileName.isEmpty()) {
            String path = currentFolder + File.separator + fileName;
            if (context != null
                    && !ThumbImageUtils.isGif(fileName)
                    && !ThumbImageUtils.isImage(fileName)
                    && !ThumbImageUtils.isVideo(fileName)) {
                return getLocalFile(id, context) != null;
            }
            return new File(path).exists();
        }
        return false;
    }

    public static boolean isDownLoaded(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            String path = currentFolder + File.separator + fileName;
            return new File(path).exists();
        }
        return false;
    }

    public static String getLocalMedia(String url) {
        return Environment.DIRECTORY_DOWNLOADS +
                File.separator + "Hahalolo" +
                File.separator + FilenameUtils.getName(url);
    }

    public static String getPathLocalMedia(String url) {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() +
                File.separator + "Hahalolo" +
                File.separator + FilenameUtils.getName(url);
    }


    public static void clearFile(String fileName) {
        File file = new File(
                currentFolder
                        + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    public static File createFolderDownload() {
        return new File(currentFolder + File.separator);
    }

    public static void openFolderDownload(Context context, String fileName, String typeFile) {
        try {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FilenameUtils.getExtension(fileName));
            openFile(context, currentFolder + File.separator + fileName);
        } catch (Exception ignored) {

        }
    }

    private static String createFileName(String fileName) {
        int count = 0;
        File file = new File(currentFolder + File.separator + fileName);
        while (file.exists()) {
            count++;
            file = new File(currentFolder
                    + File.separator
                    + FilenameUtils.getName(fileName).substring(0, fileName.lastIndexOf("."))
                    + (count == 0 ? "" : "_" + count)
                    + "."
                    + FilenameUtils.getExtension(fileName));
        }
        return FilenameUtils.getName(fileName).substring(0, fileName.lastIndexOf("."))
                + (count == 0 ? "" : "_" + count)
                + "."
                + FilenameUtils.getExtension(fileName);
    }

    private static void openFile(Context context, String url) {

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.contains(".doc") || url.contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {

        }
    }

    public static File createFile(String fileName){
        try {
            String url = currentFolder + File.separator + fileName;
            return new File(url);
        }catch (Exception ignored) {
        }
        return null;
    }

    public static Uri createUriFromFileName(Context context, String fileName) {
        try {
            File file = createFile(fileName);
            Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            return uriForFile;
        } catch (Exception ignored) {
        }
        return null;
    }

    //befor download and add to device
    public static class Download {
        private String key;
        private String name;
        private String localPath;
        private long size;
        private String pathDownload;

        public static Download create(Context context,
                                      String key,
                                      String name,
                                      long size,
                                      String pathDownload) {

            Local local = getLocalFile(key, context);
            String newName;
            String localPath;
            if (local == null) {
                //new download file
                newName = createFileName(name);
            } else {
                //download replace old file
                newName = local.getName();
            }
            localPath = getPathLocalMedia(newName);
            return new Download(key,
                    newName,
                    localPath,
                    size,
                    pathDownload);
        }

        private Download(String key,
                         String name,
                         String localPath,
                         long size,
                         String pathDownload) {
            this.key = key;
            this.name = name;
            this.localPath = localPath;
            this.size = size;
            this.pathDownload = pathDownload;
        }

        public String getLocalPath() {
            return localPath;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public String getPathDownload() {
            return pathDownload;
        }
    }

    //after added to device
    public static class Local {
        private String id;
        private long time;
        private String data;
        private String name;

        Local(String id, long time, String data, String name) {
            this.id = id;
            this.time = time;
            this.data = data;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    public interface PerListener {
        void onGranted();
        void onDeny();
    }

    public static void externalPermision(@NonNull Activity activity,
                                         @NonNull PerListener perListener) {

        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Do nothing
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            perListener.onGranted();
                        } else {
                            perListener.onDeny();
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
}
