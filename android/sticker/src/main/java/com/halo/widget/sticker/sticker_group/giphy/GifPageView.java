/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group.giphy;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.halo.widget.HaloGridLayoutManager;
import com.halo.widget.sticker.R;
import com.halo.widget.sticker.sticker_group.StickerGroupView;
import com.halo.widget.sticker.sticker_group.adapter.GiphyAdapter;

import java.util.ArrayList;
import java.util.List;

public class GifPageView extends FrameLayout {

    private View root;
    private RecyclerView recyclerView;
    private GiphyAdapter giphyAdapter;
    private RequestManager requestManager;
    private EditText inputSearch;
    private View noResult;
    private View loading;

    public GifPageView(Context context, RequestManager requestManager) {
        super(context);
        this.requestManager = requestManager;
        initView();
    }

    public GifPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GifPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.layout_gif_page, null);
        inputSearch = root.findViewById(R.id.input_search);
        recyclerView = root.findViewById(R.id.gif_rec);
        noResult = root.findViewById(R.id.no_result_tv);
        loading = root.findViewById(R.id.loading_tv);
        this.addView(root);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initRec();
    }

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    private void initRec() {
        giphyAdapter = new GiphyAdapter(requestManager);
        giphyAdapter.setGiphyListener(media -> {
            if (listener != null) {
                listener.onClickGifItem(media);
            }
        });


        recyclerView.setLayoutManager(new HaloGridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(giphyAdapter);
        loadGifList("");
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadGifList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadGifList(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void loadGifList(String query) {

        GPHApi client = new GPHApiClient(getContext().getString(R.string.key_giphy));
        loading.setVisibility(VISIBLE);

        if (query != null && !query.isEmpty()) {
            client.search(query, MediaType.gif, null, null, null, null, null, (result, e) -> {
                loading.setVisibility(GONE);
                List<Media> list = result != null && result.getData() != null ? result.getData() : new ArrayList<>();
                giphyAdapter.updateList(list);
                noResult.setVisibility(list != null && !list.isEmpty() ? GONE : VISIBLE);
            });
        } else {
            client.trending(MediaType.gif, null, null, null, (result, e) -> {
                loading.setVisibility(GONE);
                List<Media> list = result != null && result.getData() != null ? result.getData() : new ArrayList<>();
                giphyAdapter.updateList(list);
                noResult.setVisibility(list != null && !list.isEmpty() ? GONE : VISIBLE);
            });
        }
    }

    private StickerGroupView.StickerGroupListener listener;

    public void setListener(StickerGroupView.StickerGroupListener listener) {
        this.listener = listener;
    }
}
