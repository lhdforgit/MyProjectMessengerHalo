/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadTask;

import java.util.HashMap;

public class KeyToIdMap {

    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    @NonNull
    private final HashMap<String, Integer> keyToIdMap;
    @NonNull
    private final SparseArray<String> idToKeyMap;

    KeyToIdMap() {
        this(new HashMap<String, Integer>(), new SparseArray<String>());
    }

    KeyToIdMap(@NonNull HashMap<String, Integer> keyToIdMap,
               @NonNull SparseArray<String> idToKeyMap) {
        this.keyToIdMap = keyToIdMap;
        this.idToKeyMap = idToKeyMap;
    }

    @Nullable
    public Integer get(@NonNull DownloadTask task) {
        final Integer candidate = keyToIdMap.get(generateKey(task));
        if (candidate != null) return candidate;
        return null;
    }

    public void remove(int id) {
        final String key = idToKeyMap.get(id);
        if (key != null) {
            keyToIdMap.remove(key);
            idToKeyMap.remove(id);
        }
    }

    public void add(@NonNull DownloadTask task, int id) {
        final String key = generateKey(task);
        keyToIdMap.put(key, id);
        idToKeyMap.put(id, key);
    }

    String generateKey(@NonNull DownloadTask task) {
        return task.getUrl() + task.getUri() + task.getFilename();
    }
}
