/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.gallery.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.exifinterface.media.ExifInterface;

import com.halo.R;
import com.halo.presentation.gallery.entities.IncapableCause;
import com.halo.presentation.gallery.entities.Item;
import com.halo.presentation.gallery.entities.SelectionSpec;
import com.halo.presentation.gallery.filter.Filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author ndn
 * Created by ndn
 * Created on 7/11/18
 */
public final class PhotoMetadataUtils {
    private static final String TAG = PhotoMetadataUtils.class.getSimpleName();
    private static final int MAX_WIDTH = 1600;
    private static final String SCHEME_CONTENT = "content";

    private PhotoMetadataUtils() {
        throw new AssertionError("oops! the utility class is about to be instantiated...");
    }

    public static int getPixelsCount(ContentResolver resolver, Uri uri) {
        Point size = getBitmapBound(resolver, uri);
        return size.x * size.y;
    }

    public static Point getBitmapSize(Uri uri, Activity activity) {
        ContentResolver resolver = activity.getContentResolver();
        Point imageSize = getBitmapBound(resolver, uri);
        int w = imageSize.x;
        int h = imageSize.y;
        if (PhotoMetadataUtils.shouldRotate(resolver, uri)) {
            w = imageSize.y;
            h = imageSize.x;
        }
        if (h == 0) return new Point(MAX_WIDTH, MAX_WIDTH);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float screenWidth = (float) metrics.widthPixels;
        float screenHeight = (float) metrics.heightPixels;
        float widthScale = screenWidth / w;
        float heightScale = screenHeight / h;
        if (widthScale > heightScale) {
            return new Point((int) (w * widthScale), (int) (h * heightScale));
        }
        return new Point((int) (w * widthScale), (int) (h * heightScale));
    }

    private Point getDropboxIMGSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new Point(imageWidth, imageHeight);
    }

    public static Point getBitmapBound(ContentResolver resolver, Uri uri) {
        InputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (TextUtils.isEmpty(uri.getPath())){
                File file;
                is = new FileInputStream(new File(uri.toString()));
            }else {
                is = resolver.openInputStream(uri);
            }
            BitmapFactory.decodeStream(is, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return new Point(width, height);
        } catch (FileNotFoundException e) {
            return new Point(-1, -1);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPath(ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        }

        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                }
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return uri.getPath();
    }

    public static IncapableCause isAcceptable(Context context, Item item) {
        if (!isSelectableType(context, item)) {
            return new IncapableCause(context.getString(R.string.error_file_type));
        }

        if (SelectionSpec.getInstance().filters != null) {
            for (Filter filter : SelectionSpec.getInstance().filters) {
                IncapableCause incapableCause = filter.filter(context, item);
                if (incapableCause != null) {
                    return incapableCause;
                }
            }
        }
        return null;
    }

    private static boolean isSelectableType(Context context, Item item) {
        if (context == null) {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        for (MimeType type : SelectionSpec.getInstance().mimeTypeSet) {
            if (type.checkType(resolver, item.getContentUri())) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldRotate(ContentResolver resolver, Uri uri) {
        ExifInterface exif;
        try {
            exif = ExifInterfaceCompat.newInstance(getPath(resolver, uri));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        return orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270;
    }

    public static float getSizeInMB(long sizeInBytes) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("0.0");
        String result = df.format((float) sizeInBytes / 1024 / 1024);
        result = result.replaceAll(",", "."); // in some case , 0.0 will be 0,0
        return Float.valueOf(result);
    }

    /**
     * Get the file's content URI
     * then query the server app to get the file's display name
     * and size.
     *
     * @param uri {@link Uri}
     */
    public static long getFileSize(Uri uri, Context context) {
        try {
            if (uri != null && context != null) {
                Cursor returnCursor =
                        context.getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null) {
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    /*
                     * Get the column indexes of the data in the Cursor,
                     * move to the first row in the Cursor, get the data
                     */
                    returnCursor.moveToFirst();
                    long size = returnCursor.getLong(sizeIndex);
                    returnCursor.close();
                    return size;
                }
            }
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Get the file's content URI
     * then query the server app to get the file's display name
     * and size.
     *
     * @param uri {@link Uri}
     */
    public static String getFileName(Uri uri, Context context) {
        try {
            if (uri != null && context != null) {
                Cursor returnCursor =
                        context.getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null) {
                    int fileNameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    /*
                     * Get the column indexes of the data in the Cursor,
                     * move to the first row in the Cursor, get the data
                     */
                    returnCursor.moveToFirst();
                    String fileName = returnCursor.getString(fileNameIndex);
                    returnCursor.close();
                    return fileName;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
