/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/1/18
 * https://stackoverflow.com/questions/14981307/how-to-set-multiple-spans-on-a-textviews-text-on-the-same-partial-text
 * Usage:
 * SpanBuilderUtil ssb = new SpanBuilderUtil();
 * ssb.appendWithSpace("Hello");
 * ssb.append("StackOverflow",new ForegroundColorSpan(Color.RED),new RelativeSizeSpan(1.5));
 * textView.setText(ssb.build());
 */
public class SpanBuilderUtil {

    private static class SpanSection {
        private final String text;
        private final int startIndex;
        private final CharacterStyle[] styles;

        private SpanSection(String text, int startIndex, CharacterStyle... styles) {
            this.styles = styles;
            this.text = text;
            this.startIndex = startIndex;
        }

        private void apply(SpannableStringBuilder spanStringBuilder) {
            if (spanStringBuilder == null) {
                return;
            }
            for (CharacterStyle style : styles) {
                spanStringBuilder.setSpan(style, startIndex, startIndex + text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private List<SpanSection> spanSections;
    private StringBuilder stringBuilder;

    public SpanBuilderUtil() {
        stringBuilder = new StringBuilder();
        spanSections = new ArrayList<>();
    }

    public SpanBuilderUtil append(String text, CharacterStyle... styles) {
        if (styles != null && styles.length > 0 && text != null) {
            spanSections.add(new SpanSection(text, stringBuilder.length(), styles));
        }
        stringBuilder.append(text != null ? text : "");
        return this;
    }

    public SpanBuilderUtil appendWithSpace(String text, CharacterStyle... styles) {
        return append(text != null ? text.concat(" ") : " ", styles);
    }

    public SpanBuilderUtil appendWithLineBreak(String text, CharacterStyle... styles) {
        return append(text != null ? text.concat("\n") : "\n", styles);
    }

    public SpannableStringBuilder build() {
        SpannableStringBuilder ssb = new SpannableStringBuilder(stringBuilder.toString());
        for (SpanSection section : spanSections) {
            section.apply(ssb);
        }
        return ssb;
    }

    public SpanBuilderUtil clear() {
        if (stringBuilder != null) {
            stringBuilder.setLength(0);
        }
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
