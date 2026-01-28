package io.github.vinnih.kipty.ui.player

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi

@UnstableApi
class FakeKiptyPlayer : KiptyPlayer {
    override fun getApplicationLooper(): Looper = Looper.getMainLooper()

    override fun addListener(listener: Player.Listener) {}

    override fun removeListener(listener: Player.Listener) {}

    override fun setMediaItems(mediaItems: List<MediaItem>) {}

    override fun setMediaItems(mediaItems: List<MediaItem>, resetPosition: Boolean) {}

    override fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {}

    override fun setMediaItem(mediaItem: MediaItem) {}

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {}

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {}

    override fun addMediaItem(mediaItem: MediaItem) {}

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {}

    override fun addMediaItems(mediaItems: List<MediaItem>) {}

    override fun addMediaItems(index: Int, mediaItems: List<MediaItem>) {}

    override fun moveMediaItem(currentIndex: Int, newIndex: Int) {}

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {}

    override fun replaceMediaItem(index: Int, mediaItem: MediaItem) {}

    override fun replaceMediaItems(fromIndex: Int, toIndex: Int, mediaItems: List<MediaItem>) {}

    override fun removeMediaItem(index: Int) {}

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {}

    override fun clearMediaItems() {}

    override fun isCommandAvailable(command: Int): Boolean = true

    override fun canAdvertiseSession(): Boolean = true

    override fun getAvailableCommands(): Player.Commands =
        Player.Commands.Builder().addAllCommands().build()

    override fun prepare() {}

    override fun getPlaybackState(): Int = Player.STATE_READY

    override fun getPlaybackSuppressionReason(): Int = Player.PLAYBACK_SUPPRESSION_REASON_NONE

    override fun isPlaying(): Boolean = false

    override fun getPlayerError(): PlaybackException? = null

    override fun play() {}

    override fun pause() {}

    override fun setPlayWhenReady(playWhenReady: Boolean) {}

    override fun getPlayWhenReady(): Boolean = false

    override fun getRepeatMode(): Int = Player.REPEAT_MODE_OFF

    override fun setRepeatMode(repeatMode: Int) {}

    override fun getShuffleModeEnabled(): Boolean = false

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {}

    override fun isLoading(): Boolean = false

    override fun seekToDefaultPosition() {}

    override fun seekToDefaultPosition(mediaItemIndex: Int) {}

    override fun seekTo(positionMs: Long) {}

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {}

    override fun getSeekBackIncrement(): Long = 10000

    override fun seekBack() {}

    override fun getSeekForwardIncrement(): Long = 10000

    override fun seekForward() {}

    override fun hasPreviousMediaItem(): Boolean = true

    override fun seekToPreviousMediaItem() {}

    override fun getMaxSeekToPreviousPosition(): Long = 0

    override fun seekToPrevious() {}

    override fun hasNextMediaItem(): Boolean = true

    override fun seekToNextMediaItem() {}

    override fun seekToNext() {}

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {}

    override fun setPlaybackSpeed(speed: Float) {}

    override fun getPlaybackParameters(): PlaybackParameters = PlaybackParameters.DEFAULT

    override fun stop() {}

    override fun release() {}

    override fun getCurrentTracks(): Tracks = Tracks.EMPTY

    override fun getTrackSelectionParameters(): TrackSelectionParameters =
        TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {}

    override fun getMediaMetadata(): MediaMetadata = MediaMetadata.EMPTY

    override fun getPlaylistMetadata(): MediaMetadata = MediaMetadata.EMPTY

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {}

    override fun getCurrentManifest(): Any? = null

    override fun getCurrentTimeline(): Timeline = Timeline.EMPTY

    override fun getCurrentPeriodIndex(): Int = 0

    override fun getCurrentWindowIndex(): Int = 0

    override fun getCurrentMediaItemIndex(): Int = 0

    override fun getNextWindowIndex(): Int = -1

    override fun getNextMediaItemIndex(): Int = -1

    override fun getPreviousWindowIndex(): Int = -1

    override fun getPreviousMediaItemIndex(): Int = -1

    override fun getCurrentMediaItem(): MediaItem? = MediaItem.fromUri("fake://uri")

    override fun getMediaItemCount(): Int = 1

    override fun getMediaItemAt(index: Int): MediaItem = MediaItem.fromUri("fake://uri")

    override fun getDuration(): Long = 3600000

    override fun getCurrentPosition(): Long = 1800000

    override fun getBufferedPosition(): Long = 2000000

    override fun getBufferedPercentage(): Int = (2000000 * 100 / 3600000).toInt()

    override fun getTotalBufferedDuration(): Long = 2000000

    override fun isCurrentWindowDynamic(): Boolean = false

    override fun isCurrentMediaItemDynamic(): Boolean = false

    override fun isCurrentWindowLive(): Boolean = false

    override fun isCurrentMediaItemLive(): Boolean = false

    override fun getCurrentLiveOffset(): Long = 0L

    override fun isCurrentWindowSeekable(): Boolean = true

    override fun isCurrentMediaItemSeekable(): Boolean = true

    override fun isPlayingAd(): Boolean = false

    override fun getCurrentAdGroupIndex(): Int = -1

    override fun getCurrentAdIndexInAdGroup(): Int = -1

    override fun getContentDuration(): Long = 3600000

    override fun getContentPosition(): Long = 1800000

    override fun getContentBufferedPosition(): Long = 2000000

    override fun getAudioAttributes(): AudioAttributes = AudioAttributes.DEFAULT

    override fun setVolume(volume: Float) {}

    override fun getVolume(): Float = 1.0f

    override fun clearVideoSurface() {}

    override fun clearVideoSurface(surface: Surface?) {}

    override fun setVideoSurface(surface: Surface?) {}

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {}

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {}

    override fun setVideoTextureView(textureView: TextureView?) {}

    override fun clearVideoTextureView(textureView: TextureView?) {}

    override fun getVideoSize(): VideoSize = VideoSize.UNKNOWN

    override fun getSurfaceSize(): Size = Size.UNKNOWN

    override fun getCurrentCues(): CueGroup = CueGroup.EMPTY_TIME_ZERO

    override fun getDeviceInfo(): DeviceInfo = DeviceInfo.UNKNOWN

    override fun getDeviceVolume(): Int = 100

    override fun isDeviceMuted(): Boolean = false

    override fun setDeviceVolume(volume: Int) {}

    override fun setDeviceVolume(volume: Int, flags: Int) {}

    override fun increaseDeviceVolume() {}

    override fun increaseDeviceVolume(flags: Int) {}

    override fun decreaseDeviceVolume() {}

    override fun decreaseDeviceVolume(flags: Int) {}

    override fun setDeviceMuted(muted: Boolean) {}

    override fun setDeviceMuted(muted: Boolean, flags: Int) {}

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {}
}
