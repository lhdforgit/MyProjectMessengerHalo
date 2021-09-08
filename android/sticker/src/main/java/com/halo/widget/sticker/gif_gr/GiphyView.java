/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.gif_gr;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.halo.widget.HaloLinearLayoutManager;
import com.halo.widget.model.GifModel;
import com.halo.widget.repository.sticker.StickerRepository;
import com.halo.widget.sticker.R;
import com.halo.widget.sticker.gif_gr.adapter.GiphyHorizontalAdapter;

import java.util.ArrayList;
import java.util.List;

public class GiphyView extends FrameLayout {

    private View root;
    private RecyclerView recyclerView;
    private GiphyHorizontalAdapter giphyAdapter;
    private RequestManager requestManager;
    private EditText inputSearch;
    private View noResult;
    private View loading;
    private View closeGiphy;
    private RecyclerView.OnItemTouchListener onItemTouchListener;

    public void setOnItemTouchListener(RecyclerView.OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public EditText getInputSearch() {
        return inputSearch;
    }

    public GiphyView(Context context, RequestManager requestManager) {
        super(context);
        this.requestManager = requestManager;
        initView();
    }

    public GiphyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GiphyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.layout_giphy_view, null);
        inputSearch = root.findViewById(R.id.input_search);
        recyclerView = root.findViewById(R.id.gif_rec);
        noResult = root.findViewById(R.id.no_result_tv);
        loading = root.findViewById(R.id.loading_tv);
        closeGiphy = root.findViewById(R.id.btn_close_giphy);
        closeGiphy.setOnClickListener(v -> {
            String content  = inputSearch.getText()!=null ? inputSearch.getText().toString() : "";
            if (!TextUtils.isEmpty(content)){
                inputSearch.setText("");
            }else  if (listener!= null){
                listener.onCloseGiphyView();
            }
        });

        inputSearch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Strings.log("getViewTreeObserver "+ inputSearch.isFocused() );
            }
        });

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

    private GiphyViewListener listener;

    public void setListener(GiphyViewListener listener) {
        this.listener = listener;
    }

    private void initRec() {
        giphyAdapter = new GiphyHorizontalAdapter(requestManager);
        giphyAdapter.setGiphyListener(media -> {
            if (listener != null) {
                listener.onClickGifItem(media);
            }
        });

        recyclerView.setLayoutManager(new HaloLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(giphyAdapter);
        if (onItemTouchListener != null) {
            recyclerView.addOnItemTouchListener(onItemTouchListener);
        }
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

        if (listener!=null){
            StickerRepository stickerRepository = listener.stickerRepository();
            LifecycleOwner lifecycle = listener.lifecycleOwner();
            String token = listener.token();
            if (stickerRepository!=null && lifecycle!=null && token!=null){
                stickerRepository.gifs(token, query).observe(lifecycle, new Observer<List<GifModel>>() {
                    @Override
                    public void onChanged(List<GifModel> gifModels) {
                        loading.setVisibility(VISIBLE);
                        if(gifModels!=null){
                            loading.setVisibility(GONE);
                            noResult.setVisibility(gifModels.size()==0 ? VISIBLE : GONE);
                            giphyAdapter.updateList(gifModels);
                        }else {
                            //loading
                        }
                    }
                });
            }
        }
//        GPHApi client = new GPHApiClient(getContext().getString(R.string.key_giphy));
//        loading.setVisibility(VISIBLE);
//
//        if (query != null && !query.isEmpty()) {
//            client.search(query, MediaType.gif, null, null, null, null, null, (result, e) -> {
//                loading.setVisibility(GONE);
//                List<Media> list = result != null && result.getData() != null ? result.getData() : new ArrayList<>();
//                giphyAdapter.updateList(list);
//                noResult.setVisibility(list != null && !list.isEmpty() ? GONE : VISIBLE);
//            });
//        } else {
//            client.trending(MediaType.gif, null, null, null, (result, e) -> {
//                loading.setVisibility(GONE);
//                List<Media> list = result != null && result.getData() != null ? result.getData() : new ArrayList<>();
//                giphyAdapter.updateList(list);
//                noResult.setVisibility(list != null && !list.isEmpty() ? GONE : VISIBLE);
//            });
//        }
    }


    public interface GiphyViewListener {
        void onClickGifItem(GifModel media);
        void onCloseGiphyView();

        @Nullable
        StickerRepository stickerRepository();
        @Nullable
        LifecycleOwner lifecycleOwner();
        @Nullable
        String token();
    }
}
