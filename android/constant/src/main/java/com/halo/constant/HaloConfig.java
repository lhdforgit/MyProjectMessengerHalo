/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.constant;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.halo.constant.ChatHost.HALOME_SOCKET_LIVE;
import static com.halo.constant.ChatHost.HALOME_SOCKET_SB;
import static com.halo.constant.ChatHost.HALOME_SOCKET_TEST;
import static com.halo.constant.ChatHost.HALOME_URI_LIVE;
import static com.halo.constant.ChatHost.HALOME_URI_SB;
import static com.halo.constant.ChatHost.HALOME_URI_TEST;
import static com.halo.constant.HaloConfig.Dev.LIVE;
import static com.halo.constant.HaloConfig.Dev.SB;
import static com.halo.constant.HaloConfig.Dev.TEST;

/**
 * @author ngannd
 * Create by ngannd on 18/12/2018
 */
public final class HaloConfig {

    @IntDef({TEST, LIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Dev {
        int TEST = 1;
        int LIVE = 2;
        int SB = 3;
    }

    public static int dev = SB;
    public static final int DATABASE_VERSION = 1;

    public static final int MESSENGER_DATABASE_VERSION = 1;
    public static final int MESSENGER_DATABASE_VERSION_OLD = -1;

    public static final int SEARCH_DATABASE_VERSION = 1;

    public static final String MESS_HOST = dev== TEST ? HALOME_URI_TEST: (dev==SB? HALOME_URI_SB: HALOME_URI_LIVE);

    public static final String SOCKET_HOST = dev== TEST ? HALOME_SOCKET_TEST: (dev==SB? HALOME_SOCKET_SB: HALOME_SOCKET_LIVE) ;

    @SuppressLint("HardwareIds")
    public static String userAgent() {
        return Build.MODEL + "/" + Build.MANUFACTURER + "/";
    }

    public static String langForHeaderApp = "vi";

    public static final String DEVICE = "device";
    public static final String DEVICE_ID = "device-id";

    // Help
    public static final String HELP = "https://help.hahalolo.com/hc/vi";
    public static final String HELP_EN = "https://help.hahalolo.com/hc/en-us";

    // chat mqtt
    public static final String SERVER_CHAT_URI =
            dev == TEST ? ChatHost.SERVER_CHAT_URI_TEST : ChatHost.SERVER_CHAT_URI_LIVE;
    public static final String SERVER_CHAT_HOST_URI =
            dev == TEST ? ChatHost.SERVER_CHAT_HOST_TEST : ChatHost.SERVER_CHAT_HOST_LIVE;

    public static final String LANGUAGE_DEFAULT = "vi";
    public static final String ISO_DEFAULT = "VN";
    public static final String MONGO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ES_DATE_FORMAT = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            ? "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" : "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TOKEN_N_ACTIVE = "n-active";

    public static final int MEDIA_MAX_SIZE_BYTE = 314572800;
    public static final int MEDIA_MAX_SELECT = 20;

    // Load Items
    public static final int PRELOAD_AHEAD_ITEMS = 2;
    public static final int DEFAULT_NETWORK_PAGE_SIZE = 10;

    public static final String NOTIFICATION_APP_ID = "com.hahalolo.android.halome";
}