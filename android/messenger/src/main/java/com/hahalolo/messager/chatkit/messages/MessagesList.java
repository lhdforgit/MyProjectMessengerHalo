/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.messages;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hahalolo.messager.chatkit.adapter.ChatMessagePagedAdapter;
import com.hahalolo.messager.presentation.message.adapter.ChatMessageAdapter;
import com.halo.widget.HaloLinearLayoutManager;
import com.halo.widget.sticky_header.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.NotNull;


public class MessagesList extends RecyclerView {
    private MessagesListStyle messagesListStyle;

    public MessagesList(Context context) {
        super(context);
    }

    public MessagesList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
    }

    public MessagesList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs);
    }


    public HaloLinearLayoutManager setAdapterPagedV4(ChatMessagePagedAdapter adapter){
        HaloLinearLayoutManager layoutManager = new HaloLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, true );
        setLayoutManager(layoutManager);
        adapter.setStyle(messagesListStyle);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        this.addItemDecoration(headersDecor);
        super.setAdapter(adapter);
        return layoutManager;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        throw new IllegalArgumentException("You can't set adapter to MessagesList. Use #setAdapter(MessagesListAdapter) instead.");
    }

    @SuppressWarnings("ResourceType")
    private void parseStyle(Context context, AttributeSet attrs) {
        messagesListStyle = MessagesListStyle.parse(context, attrs);
    }


}
