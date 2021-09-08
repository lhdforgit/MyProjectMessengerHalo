/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ngannd
 * Create by ngannd on 31/12/2018
 */
public abstract class HaloHeaderFooterAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private List<T> data;

    protected boolean withHeader;
    protected boolean withFooter;

    public HaloHeaderFooterAdapter(List<T> data, boolean withHeader, boolean withFooter) {
        this.data = data;
        this.withHeader = withHeader;
        this.withFooter = withFooter;
    }

    public HaloHeaderFooterAdapter(boolean withHeader, boolean withFooter) {
        this.data = new ArrayList<>();
        this.withHeader = withHeader;
        this.withFooter = withFooter;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (this.data != null) {
            this.data.clear();
            notifyDataSetChanged();
        }
    }

    //region Get View
    protected abstract RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent);

    protected abstract RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent);

    protected abstract RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent);
    //endregion

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ITEM) {
            return getItemView(inflater, parent);
        } else if (viewType == TYPE_HEADER) {
            return getHeaderView(inflater, parent);
        } else if (viewType == TYPE_FOOTER) {
            return getFooterView(inflater, parent);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + " +
                "make sure your using types correctly");
    }

    @Override
    public int getItemCount() {
        int itemCount = getRealItemCount();
        if (withHeader) itemCount++;
        if (withFooter) itemCount++;
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (withHeader && isPositionHeader(position)) return TYPE_HEADER;
        if (withFooter && isPositionFooter(position)) return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    protected int getRealItemCount() {
        return data.size();
    }

    protected T getItem(int position) {
        return withHeader ? data.get(position - 1) : data.get(position);
    }

    protected List<T> getData() {
        return data;
    }
}
