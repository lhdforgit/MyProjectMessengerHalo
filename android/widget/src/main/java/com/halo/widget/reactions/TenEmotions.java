/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.halo.widget.R;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/22/18
 */
public enum TenEmotions {
    LEVER_1(TenNumber.LEVER_1, R.drawable.ic_ten_level_1),
    LEVER_2(TenNumber.LEVER_2, R.drawable.ic_ten_level_2),
    LEVER_3(TenNumber.LEVER_3, R.drawable.ic_ten_level_3),
    LEVER_4(TenNumber.LEVER_4, R.drawable.ic_ten_level_4),
    LEVER_5(TenNumber.LEVER_5, R.drawable.ic_ten_level_5);

    @TenNumber
    private int value;
    @DrawableRes
    private int resDrawable;

    TenEmotions(@TenNumber int value, @DrawableRes int resDrawable) {
        this.value = value;
        this.resDrawable = resDrawable;
    }

    @TenNumber
    public int getValue() {
        return value;
    }

    @DrawableRes
    public int getResDrawable() {
        return resDrawable;
    }

    @Nullable
    public static TenEmotions reactionOf(@TenNumber int value) {
        for (TenEmotions reaction : values()) {
            if (reaction.value == value) {
                return reaction;
            }
        }
        return null;
    }
}