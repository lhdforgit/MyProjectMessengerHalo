/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.mediasend.send

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util
import com.halo.constant.HaloConfig
import com.halo.editor.R
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.math.max

/**
 * @author ndn
 * Created by ndn
 * Created on 3/19/20
 * com.halo.editor.mediasend
 */
open class MediaSendVideoFragment : Fragment(),
    MediaSendPageFragment,
    PlayerControlView.VisibilityListener, PlaybackPreparer {
    override var uri: Uri? = null
    override val playbackControls: View? = null

    private var playerView: PlayerView? = null

    private var dataSourceFactory: DataSource.Factory? = null
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null

    private val cookieManager: CookieManager = CookieManager()
        get() {
            field.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
            return field
        }

    private var data =
        Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory = buildDataSourceFactory()
        if (CookieHandler.getDefault() !== cookieManager) {
            CookieHandler.setDefault(cookieManager)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mediasend_video_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.player_view)
        playerView!!.setControllerVisibilityListener(this)
        playerView!!.requestFocus()

        if (savedInstanceState != null) {
            data.startAutoPlay =
                savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            data.startWindow =
                savedInstanceState.getInt(KEY_WINDOW)
            data.startPosition =
                savedInstanceState.getLong(KEY_POSITION)
        } else {
            clearStartPosition()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            playerView?.onResume()
        }
    }


    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            playerView?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateStartPosition()
        outState.putBoolean(
            KEY_AUTO_PLAY,
            data.startAutoPlay
        )
        outState.putInt(
            KEY_WINDOW,
            data.startWindow
        )
        outState.putLong(
            KEY_POSITION,
            data.startPosition
        )
    }

    // Internal methods
    private fun initializePlayer() {
        context?.let { ctx ->
            if (player == null) {
                mediaSource = createTopLevelMediaSource(uri)
                if (mediaSource == null) {
                    return
                }
                player = SimpleExoPlayer.Builder(ctx).build()
                player?.addListener(PlayerEventListener())
                player?.playWhenReady = data.startAutoPlay
                playerView?.player = player
                playerView?.setPlaybackPreparer(this)
            }
            val haveStartPosition = data.startWindow != C.INDEX_UNSET
            if (haveStartPosition) {
                player!!.seekTo(data.startWindow, data.startPosition)
            }
            player!!.prepare(mediaSource!!, !haveStartPosition, false)
        }
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun preparePlayback() {
        player?.retry()
    }

    private fun createTopLevelMediaSource(uri: Uri?): MediaSource? {
        return uri?.run {
            createLeafMediaSource(uri)
        }
    }

    private fun createLeafMediaSource(uri: Uri): MediaSource? {
        return when (@C.ContentType val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory!!)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory!!)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory!!)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            data.startAutoPlay = player!!.playWhenReady
            data.startWindow = player!!.currentWindowIndex
            data.startPosition = max(0, player!!.contentPosition)
        }
    }

    private fun clearStartPosition() {
        data.startAutoPlay = true
        data.startWindow = C.INDEX_UNSET
        data.startPosition = C.TIME_UNSET
    }

    override fun saveState(): Any? {
        return data
    }

    override fun restoreState(state: Any?) {
        if (state is Data) {
            data = state as Data
            if (Build.VERSION.SDK_INT >= 23) {
                initializePlayer()
                playerView?.onResume()
            }
        } else {
            Log.w(
                javaClass.name,
                "Received a bad saved state. Received class: " + state!!.javaClass.name
            )
        }
    }

    override fun notifyHidden() {
        playerView?.onPause()
        releasePlayer()
    }

    /** Returns a [DataSource.Factory].  */
    private fun buildDataSourceFactory(): DataSource.Factory? {
        return DefaultDataSourceFactory(context, buildHttpDataSourceFactory())
    }

    /** Returns a [HttpDataSource.Factory].  */
    private fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(
            context?.let {
                Util.getUserAgent(it, "Hahalolo")
            } ?: HaloConfig.userAgent()
        )
    }

    override fun onVisibilityChange(visibility: Int) {

    }

    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }

    inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            @Player.State playbackState: Int
        ) {
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }
    }

    internal class Data {
        var startAutoPlay = false
        var startWindow = 0
        var startPosition: Long = 0
    }

    companion object {
        private const val KEY_URI = "uri"

        // Saved instance state keys.
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"

        @JvmStatic
        fun newInstance(uri: Uri): MediaSendVideoFragment {
            val args = Bundle()
            args.putParcelable(KEY_URI, uri)
            val fragment =
                MediaSendVideoFragment()
            fragment.arguments = args
            fragment.uri = uri
            return fragment
        }
    }
}