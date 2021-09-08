/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.swipe.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

import com.hahalolo.swipe.SwipeLayout;
import com.hahalolo.swipe.implments.SwipeItemMangerImpl;
import com.hahalolo.swipe.interfaces.SwipeAdapterInterface;
import com.hahalolo.swipe.interfaces.SwipeItemMangerInterface;
import com.hahalolo.swipe.util.Attributes;

import java.util.List;

public abstract class CursorSwipeAdapter extends CursorAdapter implements SwipeItemMangerInterface, SwipeAdapterInterface {

    private SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

    protected CursorSwipeAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    protected CursorSwipeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        mItemManger.bind(v, position);
        return v;
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }
}
