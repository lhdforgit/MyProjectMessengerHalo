/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.exception;

import java.io.IOException;

/**
 * Throw this exception only if the {@link com.halo.okdownload.DownloadTask#isWifiRequired} is
 * {@code true} but the current network type is not Wifi.
 */
public class NetworkPolicyException extends IOException {
    public NetworkPolicyException() {
        super("Only allows downloading this task on the wifi network type!");
    }
}
