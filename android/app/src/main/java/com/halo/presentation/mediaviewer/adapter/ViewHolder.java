/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
public abstract class ViewHolder {

    private static final String STATE = ViewHolder.class.getSimpleName();

    public final View itemView;

    boolean mIsAttached;

    int mPosition;

    public ViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView should not be null");
        }
        this.itemView = itemView;
    }

    public void attach(ViewGroup parent, int position) {
        mIsAttached = true;
        mPosition = position;
        parent.addView(itemView);
    }

    public void detach(ViewGroup parent) {
        parent.removeView(itemView);
        mIsAttached = false;
    }

    void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            SparseArray<Parcelable> ss = bundle.containsKey(STATE) ? bundle.getSparseParcelableArray(STATE) : null;
            if (ss != null) {
                itemView.restoreHierarchyState(ss);
            }
        }
    }

    Parcelable onSaveInstanceState() {
        SparseArray<Parcelable> state = new SparseArray<>();
        itemView.saveHierarchyState(state);
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray(STATE, state);
        return bundle;
    }


}