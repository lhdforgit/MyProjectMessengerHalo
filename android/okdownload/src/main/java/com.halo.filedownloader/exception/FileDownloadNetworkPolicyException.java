/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.exception;

import com.halo.filedownloader.BaseDownloadTask;

/**
 * Throw this exception, If you have set {@code true} to {@link BaseDownloadTask#setWifiRequired}
 * when starting downloading with the network type isn't wifi or in downloading state the network
 * type change to non-Wifi type.
 */

public class FileDownloadNetworkPolicyException extends FileDownloadGiveUpRetryException {
    public FileDownloadNetworkPolicyException() {
        super("Only allows downloading this task on the wifi network type");
    }
}