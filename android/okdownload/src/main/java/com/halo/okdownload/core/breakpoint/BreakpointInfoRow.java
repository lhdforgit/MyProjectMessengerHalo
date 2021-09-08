/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

import android.database.Cursor;

import java.io.File;

import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.CHUNKED;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.ETAG;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.FILENAME;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.ID;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.PARENT_PATH;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.TASK_ONLY_PARENT_PATH;
import static com.halo.okdownload.core.breakpoint.BreakpointSQLiteKey.URL;

public class BreakpointInfoRow {
    private final int id;
    private final String url;
    private final String etag;
    private final String parentPath;
    private final String filename;
    private final boolean taskOnlyProvidedParentPath;
    private final boolean chunked;

    public BreakpointInfoRow(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(ID));
        this.url = cursor.getString(cursor.getColumnIndex(URL));
        this.etag = cursor.getString(cursor.getColumnIndex(ETAG));
        this.parentPath = cursor.getString(cursor.getColumnIndex(PARENT_PATH));
        this.filename = cursor.getString(cursor.getColumnIndex(FILENAME));
        this.taskOnlyProvidedParentPath = cursor.getInt(
                cursor.getColumnIndex(TASK_ONLY_PARENT_PATH)) == 1;
        this.chunked = cursor.getInt(cursor.getColumnIndex(CHUNKED)) == 1;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getEtag() {
        return etag;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isTaskOnlyProvidedParentPath() {
        return taskOnlyProvidedParentPath;
    }

    public boolean isChunked() {
        return chunked;
    }

    public BreakpointInfo toInfo() {
        final BreakpointInfo info = new BreakpointInfo(id, url, new File(parentPath), filename,
                taskOnlyProvidedParentPath);
        info.setEtag(etag);
        info.setChunked(chunked);
        return info;
    }
}
