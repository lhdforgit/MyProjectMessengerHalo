/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

import com.google.android.material.textfield.TextInputEditText;

/**
 * @author Bui Son
 * Create by buison on 11/07/2019
 */
public class HaloInputEditText extends TextInputEditText {

    private boolean enableGif = false;

    public HaloInputEditText(Context context) {
        super(context);
    }

    public HaloInputEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(attrs);
    }

    public HaloInputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributeSet(attrs);
    }

    private void initAttributeSet(AttributeSet attrs) {
        try {
            if (attrs != null) {
                TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HaloInputEditText, 0, 0);
                enableGif = ta.getBoolean(R.styleable.HaloInputEditText_enable_Media, false);
                ta.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        if (ic == null) {
            return null;
        }
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/jpg", "image/png", "image/jpeg", "image/gif"});
        InputConnectionCompat.OnCommitContentListener callback = (inputContentInfo, flags, opts) -> {
            // read and display inputContentInfo asynchronously
            if (BuildCompat.isAtLeastNMR1() && (flags &
                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    inputContentInfo.requestPermission();
                } catch (Exception e) {
                    return false; // return false if failed
                }
            }
            // read and display inputContentInfo asynchronously.
            // call inputContentInfo.releasePermission() as needed.
            if (enableGif
                    && inputListener != null
                    && inputContentInfo != null) {
                inputListener.onSelect(inputContentInfo);
            } else {
                Toast.makeText(getContext(), R.string.keybroad_not_supported_for_gif, Toast.LENGTH_SHORT).show();
            }
            // return true if succeeded
            return true;
        };
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    private InputSelectedListener inputListener;

    /**
     * Call back input with media type
     *
     * @param inputListener {@link InputSelectedListener}
     */
    public void addInputSelectedListener(@NonNull InputSelectedListener inputListener) {
        this.inputListener = inputListener;
    }

    public void removeInputSelectedListener() {
        this.inputListener = null;
    }

    /**
     * Input connection listener
     */
    public interface InputSelectedListener {
        void onSelect(InputContentInfoCompat infoCompat);
    }
}
