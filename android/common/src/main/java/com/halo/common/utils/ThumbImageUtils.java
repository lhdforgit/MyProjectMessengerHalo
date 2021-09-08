/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;

import static android.util.DisplayMetrics.DENSITY_XXHIGH;
import static com.halo.common.utils.ThumbImageUtils.Quality.HIGH;
import static com.halo.common.utils.ThumbImageUtils.Quality.LOW;
import static com.halo.common.utils.ThumbImageUtils.Size.AVATAR_LARGE;
import static com.halo.common.utils.ThumbImageUtils.Size.AVATAR_NORMAL;
import static com.halo.common.utils.ThumbImageUtils.Size.AVATAR_SMALL;
import static com.halo.common.utils.ThumbImageUtils.Size.AVATAR_WALL;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_THUMB_LOADING;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_1080;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_1920;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_360;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_480;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_640;
import static com.halo.common.utils.ThumbImageUtils.Size.MEDIA_WIDTH_720;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._16_9;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._1_1;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._1_2;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._2_1;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._3_2;
import static com.halo.common.utils.ThumbImageUtils.TypeSize._AUTO;

/**
 * @author ngannd
 * Create by ngannd on 21/12/2018
 */
final public class ThumbImageUtils {


    @IntDef({AVATAR_SMALL, AVATAR_NORMAL, AVATAR_LARGE, AVATAR_WALL, MEDIA_WIDTH_720, MEDIA_WIDTH_1080, MEDIA_WIDTH_1920,
            MEDIA_THUMB_LOADING, MEDIA_WIDTH_640, MEDIA_WIDTH_360, MEDIA_WIDTH_480})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Size {
        int AVATAR_SMALL = 96;
        int AVATAR_NORMAL = 128;
        int AVATAR_LARGE = 192;
        int AVATAR_WALL = 224;

        int MEDIA_THUMB_LOADING = 64;

        int MEDIA_WIDTH_360 = 360;
        int MEDIA_WIDTH_480 = 480;
        int MEDIA_WIDTH_640 = 640;
        int MEDIA_WIDTH_720 = 720;
        int MEDIA_WIDTH_1080 = 1080;
        int MEDIA_WIDTH_1920 = 1920;
    }

    @StringDef({LOW, HIGH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Quality {
        String LOW = "low";
        String HIGH = "high";
    }

    @IntDef({_16_9, _1_1, _2_1, _1_2, _3_2, _AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeSize {
        int _16_9 = 0;
        int _1_1 = 1;
        int _2_1 = 2;
        int _1_2 = 3;
        int _3_2 = 4;
        int _AUTO = 5;
    }

    public static int sizeWithScreenWidth() {
        return ScreenUtils.getScreenWidth() >= MEDIA_WIDTH_1920
                // Trường hợp màn hình có chiều rộng lớn hơn hoặc bằng 1920
                ? (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_1080 : MEDIA_WIDTH_720)
                : ScreenUtils.getScreenWidth() >= MEDIA_WIDTH_1080
                // Trường hợp màn hình có chiều rộng lớn hơn hoặc bằng 1080
                ? (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_720 : MEDIA_WIDTH_640)
                // Trường hợp màn hình có chiều rộng nhỏ hơn 1080
                : (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_640 : MEDIA_WIDTH_480);
    }

    @Size
    public static int getSize(int size) {
        if (size >= MEDIA_WIDTH_1920) {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_1080 : MEDIA_WIDTH_720);
        } else if (size >= MEDIA_WIDTH_1080) {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_720 : MEDIA_WIDTH_640);
        } else if (size >= MEDIA_WIDTH_720) {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_640 : MEDIA_WIDTH_480);
        } else if (size >= MEDIA_WIDTH_640) {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_480 : MEDIA_WIDTH_360);
        } else if (size >= MEDIA_WIDTH_480) {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? MEDIA_WIDTH_360 : AVATAR_WALL);
        } else {
            return (ScreenUtils.getScreenDensityDpi() > DENSITY_XXHIGH ? AVATAR_WALL : AVATAR_LARGE);
        }
    }

    /**
     * Get thumb image url for view
     *
     * @param width    width view have set url image
     * @param url      url path of image
     * @param typeSize {@link TypeSize} type of size thumb
     * @return String thumb url
     */
    @Nullable
    public static String thumb(@Size int width, String url, @TypeSize int typeSize) {
        if (url != null) {
            // Trường hợp nếu là ảnh gif thì không thumb được nữa
            if (isGif(url)) {
                return url;
            } else if (url.toLowerCase().contains("hahalolo.com")) {
                String size = getSize(width, typeSize);
                String prefix = getPrefixThumb(url, size, HIGH);

                // get thumb of video
                String extension = FilenameUtils.getExtension(url);
                if (extension != null && isVideo(extension.toLowerCase())) {
                    extension = "jpg";
                }

                // Fix bug domain error
                return replaceDomain(prefix + "." + extension);
            } else {
                // Trường hợp crawler lấy link ngoài hahalolo, thì không thực hiện chức năng thumb được
                return url;
            }
        }
        return "";
    }

    @Nullable
    public static String thumb(@Size int width, String url) {
        return thumb(width, url, _AUTO);
    }

    /**
     * Get thumb image url for view, with size width full screen
     *
     * @param url      url path of image
     * @param typeSize {@link TypeSize} type of size thumb
     * @return String thumb url
     */
    @Nullable
    public static String thumb(String url, @TypeSize int typeSize) {
        return thumb(sizeWithScreenWidth(), url, typeSize);
    }

    @Nullable
    public static String thumb(String url) {
        return thumb(sizeWithScreenWidth(), url);
    }

    public static String thumbAvatar(String url) {
        return thumb(AVATAR_LARGE, url, _1_1);
    }

    /**
     * Add size, quality to url original
     *
     * @param url     original url
     * @param size    size of image
     * @param quality quality of image
     * @return url with size_quality
     */
    public static String getPrefixThumb(String url, String size, String quality) {
        return FilenameUtils.getPrefixExtension(replaceThumbUrl(url)) + "_" + size + "_" + quality;
    }

    /**
     * @param fullUrl url
     * @return url after replace
     */
    public static String replaceDomain(String fullUrl) {
        try {
            if (URLUtil.isValidUrl(fullUrl)) {
                URL url = new URL(fullUrl);
                String protocol = url.getProtocol();
                String host = url.getHost();
                String path = replacePathError(url.getPath());
                fullUrl = new URL(protocol, host, path).toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fullUrl;
    }

    /**
     * Fix bug replace //, /// to /
     *
     * @param path before replace
     * @return after replace
     */
    public static String replacePathError(String path) {
        return path != null ? path.replaceAll("(\\/\\//+)", "/") : "";
    }

    /**
     * Fix bug replace old link image with regex \_>*?\.
     * Delete path error _<size>_<quanlity>.jpg
     *
     * @param url url thumb error
     * @return url path
     */
    public static String replaceThumbUrl(String url) {
        return url != null ? url.replaceAll("\\_.*?\\.", ".") : "";
    }

    public static String getSize(int width, @TypeSize int typeSize) {
        try {
            String height = "auto";
            String size;
            switch (typeSize) {
                case _16_9:
                    height = String.valueOf(width * 9 / 16);
                    break;
                case _1_1:
                    height = String.valueOf(width);
                    break;
                case _2_1:
                    height = String.valueOf(width / 2);
                    break;
                case _1_2:
                    height = String.valueOf(width * 2);
                    break;
                case _3_2:
                    height = String.valueOf(width * 2 / 3);
                    break;
                case _AUTO:
                    height = "auto";
                    break;
            }
            size = width + "x" + height;
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getQuality() {
        try {
            return NetworkUtils.isConnectedFast() ? Quality.HIGH : Quality.LOW;
        } catch (Exception e) {
            e.printStackTrace();
            return Quality.LOW;
        }
    }

    public static boolean isGif(String url) {
        if (url == null) {
            return false;
        }
        if (!TextUtils.isEmpty(url) && url.contains(CHARACTER_EXTENSION)) {
            url = FilenameUtils.getExtension(url);
        }
        if (url == null) {
            return false;
        }
        return TextUtils.equals(url.toLowerCase(), "gif");
    }

    public static boolean urlIsImage(String url) {
        if (url == null) {
            return false;
        }
        if (!TextUtils.isEmpty(url) && url.contains(CHARACTER_EXTENSION)) {
            url = FilenameUtils.getExtension(url);
        }

        return isImage(url);
    }

    public static boolean isImage(String extension) {
        if (extension == null) {
            return false;
        }
        if (!TextUtils.isEmpty(extension) && extension.contains(CHARACTER_EXTENSION)) {
            extension = FilenameUtils.getExtension(extension);
        }
        extension = extension.toLowerCase();
        return TextUtils.equals(extension, "png")
                || TextUtils.equals(extension, "PNG")
                || TextUtils.equals(extension, "jpg")
                || TextUtils.equals(extension, "jpeg")
                || TextUtils.equals(extension, "JPG")
                || TextUtils.equals(extension, "JPEG")
                || TextUtils.equals(extension, "webp");
    }

    public static boolean urlIsVideo(String url) {
        if (url == null) {
            return false;
        }
        if (!TextUtils.isEmpty(url) && url.contains(CHARACTER_EXTENSION)) {
            url = FilenameUtils.getExtension(url);
        }
        return isVideo(url);
    }

    public static boolean isVideo(String extension) {
        if (extension == null) {
            return false;
        }
        if (!TextUtils.isEmpty(extension) && extension.contains(CHARACTER_EXTENSION)) {
            extension = FilenameUtils.getExtension(extension);
        }
        extension = extension.toLowerCase();
        return TextUtils.equals(extension, "webm")
                || TextUtils.equals(extension, "ogg")
                || TextUtils.equals(extension, "flv")
                || TextUtils.equals(extension, "3gp")
                || TextUtils.equals(extension, "mov")
                || TextUtils.equals(extension, "mpd")
                || TextUtils.equals(extension, "ism")
                || TextUtils.equals(extension, "m3u8")
                || TextUtils.equals(extension, "aac")
                || TextUtils.equals(extension, "mkv")
                || TextUtils.equals(extension, "ogg")
                || TextUtils.equals(extension, "mp4");

    }

    public static final String CHARACTER_EXTENSION = ".";
}
