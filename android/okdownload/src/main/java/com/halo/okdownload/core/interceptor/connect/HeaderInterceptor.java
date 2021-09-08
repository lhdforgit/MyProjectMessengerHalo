/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.interceptor.connect;

import androidx.annotation.NonNull;

import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.OkDownload;
import com.halo.okdownload.core.Util;
import com.halo.okdownload.core.breakpoint.BlockInfo;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.connection.DownloadConnection;
import com.halo.okdownload.core.download.DownloadChain;
import com.halo.okdownload.core.download.DownloadStrategy;
import com.halo.okdownload.core.exception.InterruptException;
import com.halo.okdownload.core.interceptor.Interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.halo.okdownload.core.Util.CONTENT_LENGTH;
import static com.halo.okdownload.core.Util.CONTENT_RANGE;
import static com.halo.okdownload.core.Util.IF_MATCH;
import static com.halo.okdownload.core.Util.RANGE;
import static com.halo.okdownload.core.Util.USER_AGENT;

public class HeaderInterceptor implements Interceptor.Connect {
    private static final String TAG = "HeaderInterceptor";

    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        final BreakpointInfo info = chain.getInfo();
        final DownloadConnection connection = chain.getConnectionOrCreate();
        final DownloadTask task = chain.getTask();

        // add user customize header
        final Map<String, List<String>> userHeader = task.getHeaderMapFields();
        if (userHeader != null) Util.addUserRequestHeaderField(userHeader, connection);
        if (userHeader == null || !userHeader.containsKey(USER_AGENT)) {
            Util.addDefaultUserAgent(connection);
        }

        // add range header
        final int blockIndex = chain.getBlockIndex();
        final BlockInfo blockInfo = info.getBlock(blockIndex);
        if (blockInfo == null) {
            throw new IOException("No block-info found on " + blockIndex);
        }

        String range = "bytes=" + blockInfo.getRangeLeft() + "-";
        range += blockInfo.getRangeRight();

        connection.addHeader(RANGE, range);
        Util.d(TAG, "AssembleHeaderRange (" + task.getId() + ") block(" + blockIndex + ") "
                + "downloadFrom(" + blockInfo.getRangeLeft() + ") currentOffset("
                + blockInfo.getCurrentOffset() + ")");

        // add etag if exist
        final String etag = info.getEtag();
        if (!Util.isEmpty(etag)) {
            connection.addHeader(IF_MATCH, etag);
        }

        if (chain.getCache().isInterrupt()) {
            throw InterruptException.HAHALOLO;
        }

        OkDownload.with().callbackDispatcher().dispatch()
                .connectStart(task, blockIndex, connection.getRequestProperties());

        DownloadConnection.Connected connected = chain.processConnect();

        if (chain.getCache().isInterrupt()) {
            throw InterruptException.HAHALOLO;
        }

        Map<String, List<String>> responseHeaderFields = connected.getResponseHeaderFields();
        if (responseHeaderFields == null) responseHeaderFields = new HashMap<>();

        OkDownload.with().callbackDispatcher().dispatch().connectEnd(task, blockIndex,
                connected.getResponseCode(), responseHeaderFields);

        // if precondition failed.
        final DownloadStrategy strategy = OkDownload.with().downloadStrategy();
        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck =
                strategy.resumeAvailableResponseCheck(connected, blockIndex, info);
        responseCheck.inspect();

        final long contentLength;
        final String contentLengthField = connected.getResponseHeaderField(CONTENT_LENGTH);
        if (contentLengthField == null || contentLengthField.length() == 0) {
            final String contentRangeField = connected.getResponseHeaderField(CONTENT_RANGE);
            contentLength = Util.parseContentLengthFromContentRange(contentRangeField);
        } else {
            contentLength = Util.parseContentLength(contentLengthField);
        }

        chain.setResponseContentLength(contentLength);
        return connected;
    }
}
