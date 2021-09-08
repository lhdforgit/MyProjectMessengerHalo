/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.constant;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.halo.constant.ChatHost.SERVER_CHAT_HOST_LIVE;
import static com.halo.constant.ChatHost.SERVER_CHAT_HOST_TEST;
import static com.halo.constant.ChatHost.SERVER_CHAT_URI_LIVE;
import static com.halo.constant.ChatHost.SERVER_CHAT_URI_TEST;

import static com.halo.constant.ChatHost.HALOME_URI_LIVE;
import static com.halo.constant.ChatHost.HALOME_URI_TEST;

/**
 * @author ngannd
 * Create by ngannd on 18/12/2018
 */
@StringDef({SERVER_CHAT_URI_LIVE, SERVER_CHAT_URI_TEST, SERVER_CHAT_HOST_LIVE, SERVER_CHAT_HOST_TEST, HALOME_URI_LIVE, HALOME_URI_TEST})
@Retention(RetentionPolicy.SOURCE)
public @interface ChatHost {

    String SERVER_CHAT_URI_LIVE = "ssl://vmq.hlapis.com:8883";
    String SERVER_CHAT_URI_TEST = "ssl://sb.hlapis.com:8883";
    String SERVER_CHAT_URI_SB = "ssl://sb.hlapis.com:8883";

    String SERVER_CHAT_HOST_LIVE = "https://hlapis.com";
    String SERVER_CHAT_HOST_TEST = "https://sb.hlapis.com";
    String SERVER_CHAT_HOST_SB = "https://sb.hlapis.com";


    String HALOME_URI_LIVE = "https://msgr-gw.hlapis.com";
    String HALOME_URI_TEST = "https://msgr-gw-dev.hlapis.com";
    String HALOME_URI_SB = "https://msgr-gw-sb.hlapis.com";

    String HALOME_SOCKET_LIVE = "wss://msgr-gw.hlapis.com/v2/gateway";
    String HALOME_SOCKET_TEST = "wss://msgr-gw-dev.hlapis.com/v2/gateway";
    String HALOME_SOCKET_SB = "wss://msgr-gw-sb.hlapis.com/v2/gateway";

}
