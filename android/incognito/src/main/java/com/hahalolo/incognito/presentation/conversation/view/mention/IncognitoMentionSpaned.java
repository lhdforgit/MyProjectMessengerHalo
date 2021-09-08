/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.conversation.view.mention;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import static android.graphics.Typeface.BOLD;

class IncognitoMentionSpaned extends SpannableString {
    private static final int COLOR_TAG_EDIT = 0xFF4C5264;
    private static final int COLOR_TAG_CLICK = 0xFF4C5264;
    private static final int COLOR_TAG_BACKGROUND_EDIT = 0xAAe5e5e5;
    private static final int COLOR_TAG_BACKGROUND_CLICK = 0xAAE0E9EC;
    private static final int STYLE_TAG_EDIT = BOLD;
    private static final int STYLE_TAG_CLICK = BOLD;


    IncognitoMentionSpaned(IncognitoMentionEntity spanEntity) {
        super(spanEntity.getContent());
        MentionColorSpan colorSpan = new MentionColorSpan(spanEntity, COLOR_TAG_EDIT);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(COLOR_TAG_BACKGROUND_EDIT);
        setSpan(colorSpan, 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(backgroundColorSpan, 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(new StyleSpan(STYLE_TAG_EDIT), 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    IncognitoMentionSpaned(IncognitoMentionEntity spanEntity, TagingClickSpan tagingClickSpan) {
        super(spanEntity.getContent());
        MentionColorSpan colorSpan = new MentionColorSpan(spanEntity, COLOR_TAG_EDIT);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(COLOR_TAG_BACKGROUND_CLICK);
        setSpan(colorSpan, 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(backgroundColorSpan, 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(new StyleSpan(STYLE_TAG_CLICK), 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (tagingClickSpan != null) {
            setSpan(tagingClickSpan, 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    class MentionColorSpan extends ForegroundColorSpan {
        private IncognitoMentionEntity spanEntity;

        MentionColorSpan(IncognitoMentionEntity spanEntity, int color) {
            super(color);
            this.spanEntity = spanEntity;
        }

        IncognitoMentionEntity getSpanEntity() {
            return spanEntity;
        }
    }

    abstract static class TagingClickSpan extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(COLOR_TAG_CLICK);
            ds.setUnderlineText(false);
        }
    }
}
