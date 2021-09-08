/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.gallery;

/**
 * @author ngannd
 * Create by ngannd on 11/12/2018
 */
public interface MediaListenerV2 {

    boolean isSelected(long rowId);

    boolean onChecked(MediaStoreData data, boolean checked);

    boolean isMaxItem();

}
