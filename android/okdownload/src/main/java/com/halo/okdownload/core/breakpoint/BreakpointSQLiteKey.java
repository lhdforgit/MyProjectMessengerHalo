/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

public interface BreakpointSQLiteKey {
    String ID = "id";
    String URL = "url";
    String ETAG = "etag";

    String PARENT_PATH = "parent_path";
    String FILENAME = "filename";
    String TASK_ONLY_PARENT_PATH = "task_only_parent_path";
    String CHUNKED = "chunked";

    String HOST_ID = "breakpoint_id";
    String BLOCK_INDEX = "block_index";
    String START_OFFSET = "start_offset";
    String CONTENT_LENGTH = "content_length";
    String CURRENT_OFFSET = "current_offset";
}
