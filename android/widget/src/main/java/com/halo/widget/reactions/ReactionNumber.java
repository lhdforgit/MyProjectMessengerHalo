/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.halo.widget.reactions.ReactionNumber.ANGRY;
import static com.halo.widget.reactions.ReactionNumber.HAHA;
import static com.halo.widget.reactions.ReactionNumber.LOLO;
import static com.halo.widget.reactions.ReactionNumber.LOVE;
import static com.halo.widget.reactions.ReactionNumber.SAD;
import static com.halo.widget.reactions.ReactionNumber.UNHAHA;
import static com.halo.widget.reactions.ReactionNumber.WOW;


/**
 * @author ndn
 * Created by ndn
 * Created on 8/24/18
 */
@IntDef({UNHAHA, HAHA, LOVE, LOLO, WOW, SAD, ANGRY})
@Retention(RetentionPolicy.SOURCE)
public @interface ReactionNumber {
    int UNHAHA = 0;
    int HAHA = 1;
    int LOVE = 2;
    int LOLO = 3;
    int WOW = 4;
    int SAD = 5;
    int ANGRY = 6;
}
