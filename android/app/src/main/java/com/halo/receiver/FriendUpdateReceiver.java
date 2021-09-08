/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;

public class FriendUpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_FRIEND_UPDATE = "UpdateStatusFriendReceiver_ACTION_FRIEND_UPDATE";
    public static final String ACTION_ALL_FRIEND_UPDATE = "UpdateStatusFriendReceiver_ACTION_ALL_FRIEND_UPDATE";
    public static final String DATA_FRIEND_UPDATE = "UpdateStatusFriendReceiver_DATA_FRIEND_UPDATE";
    public static final String COUNT_FRIEND_CHANGED = "UpdateStatusFriendReceiver_COUNT_FRIEND_CHANGED";
    private FriendReceiverListener listener;

    public enum Type {
        WAIT, SEND, FRIEND
    }

    public FriendUpdateReceiver(FriendReceiverListener friendListener) {
        this.listener = friendListener;
    }

    @NonNull
    public static FriendUpdateReceiver registerReceiver(@NonNull Context context, FriendReceiverListener friendListener) {
        FriendUpdateReceiver friendUpdateReceiver = new FriendUpdateReceiver(friendListener);
        IntentFilter intent1 = new IntentFilter(ACTION_FRIEND_UPDATE);
        context.registerReceiver(friendUpdateReceiver, intent1);

        IntentFilter intent2 = new IntentFilter(ACTION_ALL_FRIEND_UPDATE);
        context.registerReceiver(friendUpdateReceiver, intent2);

        IntentFilter intent3 = new IntentFilter(COUNT_FRIEND_CHANGED);
        context.registerReceiver(friendUpdateReceiver, intent3);
        return friendUpdateReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && context != null && listener != null) {
            if (TextUtils.equals(ACTION_FRIEND_UPDATE, intent.getAction())) {
                String id = intent.getStringExtra(DATA_FRIEND_UPDATE);
                if (!TextUtils.isEmpty(id)) listener.onChange(id);
            } else if (TextUtils.equals(ACTION_ALL_FRIEND_UPDATE, intent.getAction())) {
                listener.onUpdated();
            } else if (TextUtils.equals(COUNT_FRIEND_CHANGED, intent.getAction())) {
                Type type = (Type) intent.getSerializableExtra(COUNT_FRIEND_CHANGED);
                if (type!=null ){
                    listener.countFriendChanged(type);
                }
            }
        }
    }

    public interface FriendReceiverListener {
        default void onChange(@NonNull String idFriend) {
        }

        default void onUpdated() {
        }

        default void countFriendChanged(Type count) {
        }

    }
}