/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.emoji.widget.EmojiTextViewHelper;

import com.google.android.material.textview.MaterialTextView;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/22/18.
 * <p>
 * Need config in Application
 * private void configEmoji() {
 * final EmojiCompat.Config config;
 * if (USE_BUNDLED_EMOJI) {
 * // Use the bundled font for EmojiCompat
 * config = new BundledEmojiCompatConfig(getApplicationContext());
 * } else {
 * // Use a downloadable font for EmojiCompat
 * final FontRequest fontRequest = new FontRequest(
 * "com.google.android.gms.fonts",
 * "com.google.android.gms",
 * "Noto Color Emoji Compat",
 * R.array.com_google_android_gms_fonts_certs);
 * config = new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest)
 * .setReplaceAll(true)
 * .registerInitCallback(new EmojiCompat.InitCallback() {
 * @Override public void onInitialized() {
 * }
 * @Override public void onFailed(@Nullable Throwable throwable) {
 * }
 * });
 * }
 * EmojiCompat.init(config);
 * }
 */
public class HaloEmojiTextView extends MaterialTextView {

    private EmojiTextViewHelper mEmojiTextViewHelper;

    /**
     * Prevent calling {@link #init()} multiple times in case super() constructors
     * call other constructors.
     */
    private boolean mInitialized;

    public HaloEmojiTextView(Context context) {
        super(context);
        init();
    }

    public HaloEmojiTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HaloEmojiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!mInitialized) {
            mInitialized = true;
            getEmojiTextViewHelper().updateTransformationMethod();
        }
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(getEmojiTextViewHelper().getFilters(filters));
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
        getEmojiTextViewHelper().setAllCaps(allCaps);
    }

    private EmojiTextViewHelper getEmojiTextViewHelper() {
        if (mEmojiTextViewHelper == null) {
            mEmojiTextViewHelper = new EmojiTextViewHelper(this);
        }
        return mEmojiTextViewHelper;
    }
}
