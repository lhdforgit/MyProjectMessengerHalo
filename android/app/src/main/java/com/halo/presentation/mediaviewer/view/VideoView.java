/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hahalolo.player.core.Binder;
import com.hahalolo.player.core.Playback;
import com.halo.R;
import com.halo.presentation.MessApplication;
import com.halo.widget.anim.ViewAnim;

public class VideoView extends FrameLayout implements View.OnClickListener {

    private View root;
    private PlayerView playerView;
    private LinearLayout popupRender;
    private View btnRender;
    private boolean fullScreen = true;
    private ImageView zoomVideoBt;
    private View playViewGr;
    private DefaultTrackSelector trackSelector;
    private View layoutSeekbar;

    private Playback playback;
    private Binder.Options binder = new Binder.Options();

    public VideoView(@NonNull Context context) {
        super(context);
        initView();
    }

    public VideoView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setTrackSelector(DefaultTrackSelector trackSelector) {
        this.trackSelector = trackSelector;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.item_video_player_view, null);
        playerView = root.findViewById(R.id.player_view);
        playViewGr = root.findViewById(R.id.play_video_gr);
        layoutSeekbar = root.findViewById(R.id.layout_seekbar);
        btnRender = root.findViewById(R.id.player_render);
        popupRender = root.findViewById(R.id.video_render_gr);
        zoomVideoBt = root.findViewById(R.id.zoom_video_bt);
        btnRender.setOnClickListener(this);
        zoomVideoBt.setOnClickListener(this);

        addView(root);
        playerView.setOnTouchListener((v, event) -> {
            if (popupRender != null && popupRender.getVisibility() == VISIBLE) {
                ViewAnim.scaleZoom(popupRender, 1f, 1f, false);
            }
            return false;
        });

        checkFullScreen(getResources().getConfiguration());
        updateViewScreen(true);
        playerView.hideController();
    }

    private void checkFullScreen(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && !fullScreen) {
        } else {
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        super.dispatchVisibilityChanged(changedView, visibility);
        updateViewScreen(fullScreen);
    }

    public void setPlayer(Player player) {
        playerView.setPlayer(player);
        DefaultTrackNameProvider trackNameProvider = new DefaultTrackNameProvider(getResources());

        if (popupRender != null) {
            popupRender.removeAllViews();

        }
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (popupRender == null) return;
                if (playbackState == Player.STATE_READY) {
                    popupRender.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    for (int i = 0; i < player.getCurrentTrackSelections().length; i++) {
                        TrackSelection trackSelection = player.getCurrentTrackSelections().get(i);
                        if (trackSelection != null && player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                            int select = trackSelection.getSelectedIndex();

                            DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                            DefaultTrackSelector.SelectionOverride override = parameters.getSelectionOverride(select, player.getCurrentTrackGroups());

                            for (int j = 0; j < trackSelection.length(); j++) {
                                Format format = trackSelection.getFormat(j);

                                View view = inflater.inflate(R.layout.item_video_render_popup, popupRender, false);
                                TextView textView = view.findViewById(R.id.content_tv);
                                textView.setTextColor(select == j ? Color.GREEN : Color.BLACK);
                                textView.setText(trackNameProvider.getTrackName(format));
                                int finalJ = j;
                                boolean isDisable = j == select;

                                view.setOnClickListener(v -> ViewAnim.scaleZoom(popupRender, 1f, 1f, false));
                                popupRender.addView(view);
                            }
                        }
                    }
                }
            }
        });

    }

    public Player getPlayer() {
        return playerView.getPlayer();
    }

    public void setControllerAutoShow(boolean show) {
        playerView.setControllerAutoShow(show);
    }

    public void setControllerHideOnTouch(boolean hideOnTouch) {
        playerView.setControllerHideOnTouch(hideOnTouch);
    }

    public void setControllerVisibilityListener(PlayerControlView.VisibilityListener listener) {
        playerView.setControllerVisibilityListener(listener);
    }

    public boolean isControllerVisible() {
        return playerView.isControllerVisible();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_render:
                if (popupRender != null) {
                    ViewAnim.scaleZoom(popupRender, 1f, 1f, popupRender.getVisibility() == GONE);
                }
                break;
            case R.id.zoom_video_bt:
                updateViewScreen(!fullScreen);
                if (fullScreenListener != null) {
                    fullScreenListener.onScreenChange(fullScreen);
                }
                break;
        }
    }

    @SuppressLint("ResourceType")
    private void updateViewScreen(boolean full) {
        if (full) {
            fullScreen = true;
            zoomVideoBt.setImageLevel(2);
            playerView.hideController();
            playViewGr.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        } else {
            fullScreen = false;
            zoomVideoBt.setImageLevel(1);
            playViewGr.getLayoutParams().height = MessApplication.getInstance().WIDTH_SCREEN;
        }
        playerView.invalidate();
        playViewGr.invalidate();
        checkFullScreen(getResources().getConfiguration());
        invalidate();
    }

    private FullScreenListener fullScreenListener;

    public void setFullScreenListener(FullScreenListener fullScreenListener) {
        this.fullScreenListener = fullScreenListener;
    }

    public void invalidateView() {
        playback = null;
    }

    public View getControlView() {
        return layoutSeekbar;
    }

    public void hideController() {
        if (playerView!=null){
            playerView.hideController();
        }
    }

    public void showController() {
        if (playerView!=null) playerView.showController();
    }

    public interface FullScreenListener {

        void onScreenChange(boolean full);

    }
}
