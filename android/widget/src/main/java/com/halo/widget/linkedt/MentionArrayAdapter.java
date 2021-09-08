/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.linkedt;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.widget.R;

/**
 * Default adapter for displaying mention in {@link LinkAutoCompleteTextView}.
 * Note that this adapter is completely optional, any adapter extending
 * {@link android.widget.ArrayAdapter} can be attached to {@link LinkAutoCompleteTextView}.
 */
public class MentionArrayAdapter<T extends Mentionable> extends LinkArrayAdapter<T> {
    private final int defaultAvatar;

    public MentionArrayAdapter(@NonNull Context context) {
        this(context, R.drawable.circle_holder);
    }

    public MentionArrayAdapter(@NonNull Context context, @DrawableRes int defaultAvatar) {
        super(context, R.layout.socialview_layout_mention, R.id.socialview_mention_username);
        this.defaultAvatar = defaultAvatar;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.socialview_layout_mention, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final T item = getItem(position);
        if (item != null) {
            holder.usernameView.setText(item.getUsername());

            final CharSequence displayname = item.getDisplayname();
            if (!TextUtils.isEmpty(displayname)) {
                holder.displaynameView.setText(displayname);
                holder.displaynameView.setVisibility(View.VISIBLE);
            } else {
                holder.displaynameView.setVisibility(View.GONE);
            }

            final Object avatar = item.getAvatar();
        }
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView avatarView;
        private final ProgressBar loadingView;
        private final TextView usernameView;
        private final TextView displaynameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.socialview_mention_avatar);
            loadingView = itemView.findViewById(R.id.socialview_mention_loading);
            usernameView = itemView.findViewById(R.id.socialview_mention_username);
            displaynameView = itemView.findViewById(R.id.socialview_mention_displayname);
        }
    }
}