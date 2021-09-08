/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.halo.widget.reactions.TenNumber.LEVER_1;
import static com.halo.widget.reactions.TenNumber.LEVER_2;
import static com.halo.widget.reactions.TenNumber.LEVER_3;
import static com.halo.widget.reactions.TenNumber.LEVER_4;
import static com.halo.widget.reactions.TenNumber.LEVER_5;


/**
 * @author ndn
 * Created by ndn
 * Created on 8/24/18
 */
@IntDef({LEVER_1, LEVER_2, LEVER_3, LEVER_4, LEVER_5})
@Retention(RetentionPolicy.SOURCE)
public @interface TenNumber {
    int LEVER_1 = 1;
    int LEVER_2 = 2;
    int LEVER_3 = 3;
    int LEVER_4 = 4;
    int LEVER_5 = 5;
}
