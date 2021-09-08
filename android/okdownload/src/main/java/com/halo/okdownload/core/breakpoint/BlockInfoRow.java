/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

import android.database.Cursor;

import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.CONTENT_LENGTH;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.CURRENT_OFFSET;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.HOST_ID;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.START_OFFSET;

public class BlockInfoRow {
    private final int breakpointId;

    private final long startOffset;
    private final long contentLength;
    private final long currentOffset;

    public BlockInfoRow(Cursor cursor) {
        this.breakpointId = cursor.getInt(cursor.getColumnIndex(HOST_ID));
        this.startOffset = cursor.getInt(cursor.getColumnIndex(START_OFFSET));
        this.contentLength = cursor.getInt(cursor.getColumnIndex(CONTENT_LENGTH));
        this.currentOffset = cursor.getInt(cursor.getColumnIndex(CURRENT_OFFSET));
    }

    public int getBreakpointId() {
        return breakpointId;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public BlockInfo toInfo() {
        return new BlockInfo(startOffset, contentLength, currentOffset);
    }
}
