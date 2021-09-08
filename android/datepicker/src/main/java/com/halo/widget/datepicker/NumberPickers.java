/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.datepicker;

import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by ndn
 */

public class NumberPickers {

    //inefficient way of obtaining EditText from inside NumberPicker - not too bad here as View
    //hierarchy is very small -
    public static EditText findEditText(NumberPicker np) {
        for (int i = 0; i < np.getChildCount(); i++) {
            if (np.getChildAt(i) instanceof EditText) {
                return (EditText) np.getChildAt(i);
            }
        }
        return null;
    }
}
