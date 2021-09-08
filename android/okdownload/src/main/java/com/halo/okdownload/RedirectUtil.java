/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload;

import androidx.annotation.NonNull;

import com.halo.okdownload.core.connection.DownloadConnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class RedirectUtil {

    /**
     * How many redirects and auth challenges should we attempt? Chrome follows 21 redirects;
     * Firefox, curl, and wget follow 20; Safari follows 16; and HTTP/1.0 recommends 5.
     */
    public static final int MAX_REDIRECT_TIMES = 10;

    /**
     * The target resource resides temporarily under a different URI and the user agent MUST NOT
     * change the request method if it performs an automatic redirection to that URI.
     */
    static final int HTTP_TEMPORARY_REDIRECT = 307;
    /**
     * The target resource has been assigned a new permanent URI and any future references to this
     * resource ought to use one of the enclosed URIs.
     */
    static final int HTTP_PERMANENT_REDIRECT = 308;


    public static boolean isRedirect(int code) {
        return code == HttpURLConnection.HTTP_MOVED_PERM
                || code == HttpURLConnection.HTTP_MOVED_TEMP
                || code == HttpURLConnection.HTTP_SEE_OTHER
                || code == HttpURLConnection.HTTP_MULT_CHOICE
                || code == HTTP_TEMPORARY_REDIRECT
                || code == HTTP_PERMANENT_REDIRECT;
    }

    @NonNull
    public static String getRedirectedUrl(DownloadConnection.Connected connected, int responseCode)
            throws IOException {
        String url = connected.getResponseHeaderField("Location");
        if (url == null) {
            throw new ProtocolException(
                    "Response code is " + responseCode + " but can't find Location field");

        }
        return url;
    }

}
