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
public enum ReactionEmotions {

    LIKE(ReactionNumber.HAHA, R.drawable.ic_emoji_like_small, R.drawable.ic_emoji_like_social),
    LOVE(ReactionNumber.LOVE, R.drawable.ic_emoji_love_small, R.drawable.ic_emoji_love_social),
    HAHA(ReactionNumber.LOLO, R.drawable.ic_emoji_haha_small, R.drawable.ic_emoji_haha_social),
    WOW(ReactionNumber.WOW, R.drawable.ic_emoji_wow_small, R.drawable.ic_emoji_wow_social),
    SORRY(ReactionNumber.SAD, R.drawable.ic_emoji_sad_small, R.drawable.ic_emoji_sad_social),
    ANGRY(ReactionNumber.ANGRY, R.drawable.ic_emoji_angry_small, R.drawable.ic_emoji_angry_social),
    DISLIKE(ReactionNumber.HAHA, R.drawable.ic_emoji_dislike_small, R.drawable.ic_emoji_dislike_social);

    @ReactionNumber
    private int value;
    @DrawableRes
    private int resDrawable;
    @DrawableRes
    private int resDrawableSocial;

    ReactionEmotions(@ReactionNumber int value, @DrawableRes int resDrawable, @DrawableRes int resDrawableSocial) {
        this.value = value;
        this.resDrawable = resDrawable;
        this.resDrawableSocial = resDrawableSocial;
    }

    @ReactionNumber
    public int getValue() {
        return value;
    }

    @DrawableRes
    public int getResDrawable() {
        return resDrawable;
    }

    @DrawableRes
    public int getResDrawableSocial() {
        return resDrawableSocial;
    }

    @Nullable
    public static ReactionEmotions reactionOf(@ReactionNumber int value) {
        for (ReactionEmotions reaction : values()) {
            if (reaction.value == value) {
                return reaction;
            }
        }
        return null;
    }
}