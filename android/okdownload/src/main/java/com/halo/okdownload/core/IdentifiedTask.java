/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public abstract class IdentifiedTask {

    public static final String EMPTY_URL = "";
    public static final File EMPTY_FILE = new File("");

    public abstract int getId();

    @NonNull
    public abstract String getUrl();

    @NonNull
    protected abstract File getProvidedPathFile();

    @NonNull
    public abstract File getParentFile();

    @Nullable
    public abstract String getFilename();

    public boolean compareIgnoreId(IdentifiedTask another) {
        if (!getUrl().equals(another.getUrl())) return false;

        if (getUrl().equals(EMPTY_URL) || getParentFile().equals(EMPTY_FILE)) return false;

        if (getProvidedPathFile().equals(another.getProvidedPathFile())) return true;

        if (!getParentFile().equals(another.getParentFile())) return false;

        // cover the case of filename is provided by response.
        final String filename = getFilename();
        final String anotherFilename = another.getFilename();
        return anotherFilename != null && filename != null && anotherFilename.equals(filename);
    }
}