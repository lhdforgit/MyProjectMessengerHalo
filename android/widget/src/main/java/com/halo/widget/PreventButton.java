/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

public class PreventButton extends MaterialButton {

    public PreventButton(Context context) {
        super(context);
        init(context, null);
    }

    public PreventButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreventButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PreventButton, 0, 0);
            boolean isPrevent = attributes.getBoolean(R.styleable.PreventButton_isPrevent, false);
            if (isPrevent) {
                disableButton();
            } else {
                enableButton();
            }
            attributes.recycle();
        }
    }

    public void enableButton() {
        setEnabled(true);
        setSelected(true);
        setActivated(true);
        setAlpha(1f);
    }

    public void disableButton() {
        setEnabled(false);
        setSelected(false);
        setActivated(true);
        setAlpha(0.7f);
    }
}
