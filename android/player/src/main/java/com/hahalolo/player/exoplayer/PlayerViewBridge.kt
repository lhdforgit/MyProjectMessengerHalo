/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.hahalolo.player.R
import com.hahalolo.player.core.*
import com.hahalolo.player.exoplayer.internal.addEventListener
import com.hahalolo.player.exoplayer.internal.getVolumeInfo
import com.hahalolo.player.exoplayer.internal.removeEventListener
import com.hahalolo.player.exoplayer.internal.setVolumeInfo
import com.hahalolo.player.logInfo
import com.hahalolo.player.media.Media
import com.hahalolo.player.media.PlaybackInfo
import com.hahalolo.player.media.PlaybackInfo.Companion.INDEX_UNSET
import com.hahalolo.player.media.VolumeInfo
import kotlin.math.max

/**
 * @author ndn (2018/06/24).
 */
class PlayerViewBridge(
  context: Context,
  private val media: Media,
  private val playerProvider: ExoPlayerProvider,
  mediaSourceFactoryProvider: MediaSourceFactoryProvider
) : AbstractBridge<PlayerView>(), PlayerEventListener {

  companion object {
    internal fun isBehindLiveWindow(error: ExoPlaybackException?): Boolean {
      if (error?.type != ExoPlaybackException.TYPE_SOURCE) return false
      var cause: Throwable? = error.sourceException
      while (cause != null) {
        if (cause is BehindLiveWindowException) return true
        cause = cause.cause
      }
      return false
    }
  }

  private val context = context.applicationContext
  private val mediaSourceFactory = mediaSourceFactoryProvider.provideMediaSourceFactory(media)

  private var listenerApplied = false
  private var sourcePrepared = false

  private var _playbackInfo = PlaybackInfo() // Backing field for PlaybackInfo set/get
  private var _repeatMode = Common.REPEAT_MODE_OFF // Backing field
  private var _playbackParams = PlaybackParameters.DEFAULT // Backing field
  private var mediaSource: MediaSource? = null

  private var lastSeenTrackGroupArray: TrackGroupArray? = null
  private var inErrorState = false

  internal var player: Player? = null

  override val playerState: Int
    get() = player?.playbackState ?: Common.STATE_IDLE

  override fun prepare(loadSource: Boolean) {
    super.addEventListener(this)

    if (player == null) {
      sourcePrepared = false
      listenerApplied = false
    }

    if (loadSource) {
      prepareMediaSource()
      ensurePlayerView()
    }

    this.lastSeenTrackGroupArray = null
    this.inErrorState = false
  }

  override var renderer: PlayerView? = null
    set(value) {
      if (field === value) return // same reference
      this.lastSeenTrackGroupArray = null
      this.inErrorState = false
      if (value == null) {
        requireNotNull(field).also {
          // 'field' must be not null here
          it.player = null
          it.setErrorMessageProvider(null)
        }
      } else {
        this.player?.also {
          PlayerView.switchTargetView(it, field, value)
        }
      }

      field = value
    }

  override fun ready() {
    prepareMediaSource()
    requireNotNull(player) { "Player must be available." }
    ensurePlayerView()
  }

  override fun play() {
    super.play()
    if (playerParameters.playerShouldStart()) {
      requireNotNull(player).playWhenReady = true
    }
  }

  override fun pause() {
    super.pause()
    if (sourcePrepared) player?.playWhenReady = false
  }

  override fun reset(resetPlayer: Boolean) {
    if (resetPlayer) _playbackInfo = PlaybackInfo()
    else updatePlaybackInfo()
    player?.also {
      it.setVolumeInfo(VolumeInfo(false, 1.0F))
      it.stop(resetPlayer)
    }
    this.mediaSource = null // So it will be re-prepared later.
    this.sourcePrepared = false
    this.lastSeenTrackGroupArray = null
    this.inErrorState = false
  }

  override fun release() {
    this.removeEventListener(this)
    // this.playerView = null // Bridge's owner must do this.
    this.renderer?.player = null
    _playbackInfo = PlaybackInfo()
    player?.also {
      if (listenerApplied) {
        it.removeEventListener(eventListeners)
        listenerApplied = false
      }
      (it as? VolumeInfoController)?.removeVolumeChangedListener(volumeListeners)
      it.stop(true)
      playerProvider.releasePlayer(this.media, it)
    }

    this.player = null
    this.mediaSource = null
    this.sourcePrepared = false
    this.lastSeenTrackGroupArray = null
    this.inErrorState = false
  }

  override fun isPlaying(): Boolean {
    return player?.run {
      playWhenReady &&
              playbackState in Player.STATE_BUFFERING..Player.STATE_READY &&
              playbackSuppressionReason == Player.PLAYBACK_SUPPRESSION_REASON_NONE
    } ?: false
  }

  override var volumeInfo: VolumeInfo = player?.getVolumeInfo() ?: VolumeInfo()
    set(value) {
      "Bridge#volumeInfo: $field -> $value, $this".logInfo()
      if (field == value) return
      field = value
      player?.setVolumeInfo(value)
    }

  override fun seekTo(positionMs: Long) {
    val playbackInfo = this.playbackInfo
    playbackInfo.resumePosition = positionMs
    playbackInfo.resumeWindow = player?.currentWindowIndex ?: playbackInfo.resumeWindow
    this.playbackInfo = playbackInfo
  }

  override var playbackInfo: PlaybackInfo
    get() {
      updatePlaybackInfo()
      return _playbackInfo
    }
    set(value) {
      this.setPlaybackInfo(value, false)
    }

  override var playerParameters: PlayerParameters = PlayerParameters()
    set(value) {
      field = value
      applyPlayerParameters(value)
    }

  private fun applyPlayerParameters(parameters: PlayerParameters) {
    val player = this.player
    if (player is DefaultTrackSelectorHolder) {
      player.trackSelector.parameters = player.trackSelector.parameters.buildUpon()
        .setMaxVideoSize(parameters.maxVideoWidth, parameters.maxVideoHeight)
        .setMaxVideoBitrate(parameters.maxVideoBitrate)
        .setMaxAudioBitrate(parameters.maxAudioBitrate)
        .build()
    }
  }

  private fun setPlaybackInfo(
    playbackInfo: PlaybackInfo,
    volumeOnly: Boolean
  ) {
    _playbackInfo = playbackInfo

    player?.also {
      if (!volumeOnly) {
        val haveResumePosition = _playbackInfo.resumeWindow != INDEX_UNSET
        if (haveResumePosition) {
          it.seekTo(_playbackInfo.resumeWindow, _playbackInfo.resumePosition)
        }
      }
    }
  }

  override var repeatMode: Int
    get() = _repeatMode
    set(value) {
      _repeatMode = value
      this.player?.also { it.repeatMode = value }
    }

  private fun updatePlaybackInfo() {
    player?.also {
      if (it.playbackState == Common.STATE_IDLE) return
      _playbackInfo = PlaybackInfo(
        it.currentWindowIndex,
        max(0, it.currentPosition)
      )
    }
  }

  private fun ensurePlayerView() {
    renderer?.also { if (it.player !== this.player) it.player = this.player }
  }

  private fun prepareMediaSource() {
    val mediaSource: MediaSource = this.mediaSource ?: run {
      sourcePrepared = false
      mediaSourceFactory.createMediaSource(this.media.uri).also { this.mediaSource = it }
    }

    // Player was reset, need to prepare again.
    if (player?.playbackState == Common.STATE_IDLE) {
      sourcePrepared = false
    }

    if (!sourcePrepared) {
      ensurePlayer()
      (player as? ExoPlayer)?.also {
        it.prepare(mediaSource, playbackInfo.resumeWindow == INDEX_UNSET, false)
        sourcePrepared = true
      }
    }
  }

  private fun ensurePlayer() {
    if (player == null) {
      sourcePrepared = false
      listenerApplied = false
      val player = playerProvider.acquirePlayer(this.media)
      applyPlayerParameters(playerParameters)
      this.player = player
    }

    requireNotNull(player).also {
      if (!listenerApplied) {
        (player as? VolumeInfoController)?.addVolumeChangedListener(volumeListeners)
        it.addEventListener(eventListeners)
        listenerApplied = true
      }

      it.setPlaybackParameters(_playbackParams)
      val hasResumePosition = _playbackInfo.resumeWindow != INDEX_UNSET
      if (hasResumePosition) {
        it.seekTo(_playbackInfo.resumeWindow, _playbackInfo.resumePosition)
      }
      it.setVolumeInfo(volumeInfo)
      it.repeatMode = _repeatMode
    }
  }

  private fun onErrorMessage(
    message: String,
    cause: Throwable?
  ) {
    // Sub class can have custom reaction about the error here, including not to show this toast
    // (by not calling super.onErrorMessage(message)).
    if (this.errorListeners.isNotEmpty()) {
      this.errorListeners.onError(RuntimeException(message, cause))
    } else {
      Toast.makeText(context, message, Toast.LENGTH_SHORT)
        .show()
    }
  }

  // DefaultEventListener ??????

  override fun onPlayerError(error: ExoPlaybackException) {
    Log.e("halo::Bridge", "Error: ${error.cause}")
    if (renderer == null) {
      var errorString: String? = null
      if (error.type == ExoPlaybackException.TYPE_RENDERER) {
        val exception = error.rendererException
        if (exception is DecoderInitializationException) {
          // Special case for decoder initialization failures.
          errorString = if (exception.codecInfo == null) {
            when {
              exception.cause is MediaCodecUtil.DecoderQueryException ->
                context.getString(R.string.error_querying_decoders)
              exception.secureDecoderRequired ->
                context.getString(R.string.error_no_secure_decoder, exception.mimeType)
              else -> context.getString(R.string.error_no_decoder, exception.mimeType)
            }
          } else {
            context.getString(R.string.error_instantiating_decoder, exception.codecInfo?.name ?: "")
          }
        }
      }

      if (errorString != null) onErrorMessage(errorString, error)
    }

    inErrorState = true
    if (isBehindLiveWindow(error)) {
      reset()
    } else {
      updatePlaybackInfo()
    }
    this.errorListeners.onError(error)
  }

  override fun onPositionDiscontinuity(reason: Int) {
    if (inErrorState) {
      // Adapt from ExoPlayer demo.
      // "This will only occur if the user has performed a seek whilst in the error state. Update
      // the resume position so that if the user then retries, playback will resume from the
      // position to which they seek." - ExoPlayer
      updatePlaybackInfo()
    }
  }

  override fun onTracksChanged(
    trackGroups: TrackGroupArray,
    trackSelections: TrackSelectionArray
  ) {
    if (trackGroups == lastSeenTrackGroupArray) return
    lastSeenTrackGroupArray = trackGroups
    val player = this.player as? HaloExoPlayer ?: return
    val trackInfo = player.trackSelector.currentMappedTrackInfo
    if (trackInfo != null) {
      if (trackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
        onErrorMessage(context.getString(R.string.error_unsupported_video), player.playbackError)
      }

      if (trackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO) == RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
        onErrorMessage(context.getString(R.string.error_unsupported_audio), player.playbackError)
      }
    }
  }
}
