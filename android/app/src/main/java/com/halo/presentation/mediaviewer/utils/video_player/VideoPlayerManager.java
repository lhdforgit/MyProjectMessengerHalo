/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer.utils.video_player;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.halo.R;
import com.halo.presentation.MessApplication;
import com.halo.presentation.mediaviewer.view.VideoView;

import java.util.HashMap;

public class VideoPlayerManager {
    private static VideoPlayerManager playerManager;
    private static final boolean enableCache = false;

    private HashMap<String, Long> cacheUri;
    private HashMap<String, MediaSource> cacheSource;
    private String curentUri;

    public static VideoPlayerManager getInstance() {
        if (playerManager == null) {
            playerManager = new VideoPlayerManager();
        }
        return playerManager;
    }

    private DataSource.Factory dataSourceFactory;

    private SimpleExoPlayer player;

    private DefaultTrackSelector trackSelector;

    private VideoPlayerManager() {
        if (dataSourceFactory == null) {
            this.dataSourceFactory = new DefaultDataSourceFactory(
                    MessApplication.getInstance(),
                    Util.getUserAgent(MessApplication.getInstance(),
                    MessApplication.getInstance().getString(R.string.hahalolo_app_name)));
        }
    }

    public void init(@NonNull Context context,
                     @NonNull VideoView playerView,
                     String uri) {
        // Create a default track selector.
        if (playerView == null) return;

        if (player != null) {
            pause();
        }

        this.curentUri = uri;

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        MediaSource mediaSource;
        if (enableCache) {
            if (getCacheSource().get(uri) != null) {
                mediaSource = getCacheSource().get(uri);
            } else {
                mediaSource = buildMediaSource(Uri.parse(uri));
                getCacheSource().put(uri, mediaSource);
            }
        } else {
            mediaSource = buildMediaSource(Uri.parse(uri));
        }

        playerView.setTrackSelector(trackSelector);

        playerView.setPlayer(player);

        player.prepare(mediaSource);

        long seekposition = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            seekposition = getCacheUri().getOrDefault(uri, 0l);
        } else {
            seekposition = getCacheUri().get(uri) == null ? 0 : getCacheUri().get(uri);
        }

        if (seekposition == player.getDuration()) {
            seekposition = 0;
        }

        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.seekTo(seekposition);
        player.setPlayWhenReady(true);
    }


    public void resume(Context context, @NonNull VideoView playerView) {
        if (curentUri != null && !curentUri.isEmpty()) {
            init(context, playerView, curentUri);
        }
    }

    public void pause() {
        if (player != null) {
            if (curentUri != null) {
                getCacheUri().put(curentUri, player.getCurrentPosition());
            }
            player.release();
            player = null;
        }
    }

    public void release() {

        //TODO release player
        if (player != null) {
            player.release();
            player = null;
        }
    }

    //    @Override
    public int[] getSupportedTypes() {
        // IMA does not support Smooth Streaming ads.
        return new int[]{C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
    }

    // Internal methods.

    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }


    @NonNull
    private HashMap<String, Long> getCacheUri() {
        if (cacheUri == null) {
            cacheUri = new HashMap<>();
        }
        return cacheUri;
    }

    @NonNull
    private HashMap<String, MediaSource> getCacheSource() {
        if (cacheSource == null) {
            cacheSource = new HashMap<>();
        }
        return cacheSource;
    }


}
