/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.common.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipbroadUtils {

    public static void copyText(@NonNull Context context, @NonNull String s) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", s);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }

            Toast.makeText(context,
                    context.getString(URLUtil.isValidUrl(s) ? R.string.copied_link_notify : R.string.copied_text),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getClipboardText(final Context context) {
        final ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            final CharSequence clipboardText = clipData.getItemAt(0).getText();
            if (clipboardText != null) {
                return clipboardText.toString();
            }
        }
        return null;
    }

    public static void setClipboardText(final Context context,
                                        @Nullable final String label,
                                        @Nullable final String text) {
        try {
            final ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clipData = ClipData.newPlainText(label, text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clipData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClipboardText(final Context context,
                                        @Nullable final String label,
                                        @Nullable final String text,
                                        boolean notify) {
        setClipboardText(context, label, text);
        if (notify) {
            Toast.makeText(context,
                    String.format(context.getText(R.string.clipboard_copy).toString(), label),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
