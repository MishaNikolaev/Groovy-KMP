package com.nmichail.groovy_kmp.platform.service

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URL
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nmichail.groovy_kmp.domain.models.PlaybackProgress
import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import kotlinx.coroutines.Job
import androidx.media.session.MediaButtonReceiver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MusicPlayerService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_player_channel"
        private const val CHANNEL_NAME = "Music Player"
    }

    private val binder = MusicPlayerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playerViewModel: PlayerViewModel
    private var currentTrack: Track? = null
    private var currentPlaylist: List<Track>? = null
    private var artLoadingJob: Job? = null
    private val albumArtCache = mutableMapOf<String, Bitmap>()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        playerViewModel = getKoin().get()
        createNotificationChannel()
        setupMediaSession()
        observePlayerState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)

        val playlistJson = intent?.getStringExtra("playlist_json")
        val index = intent?.getIntExtra("track_index", 0) ?: 0
        val playlist = try {
            if (playlistJson != null) Json.decodeFromString<List<Track>>(playlistJson) else null
        } catch (e: Exception) {
            null
        }

        when (intent?.action) {
            "PLAY" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val track = playlist.getOrNull(index)
                    if (track != null) {
                        if (currentTrack?.id == track.id && mediaPlayer?.isPlaying == true) {
                            return START_NOT_STICKY
                        }
                        currentTrack = track
                        startAudioPlayback(track)
                    }
                }
            }
            "PAUSE" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val track = playlist.getOrNull(index)
                    if (track != null) currentTrack = track
                }
                pauseAudioPlayback()
            }
            "RESUME" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val track = playlist.getOrNull(index)
                    if (track != null) currentTrack = track
                }
                resumeAudioPlayback()
            }
            "NEXT" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val nextIndex = (index + 1).coerceAtMost(playlist.size - 1)
                    val nextTrack = playlist.getOrNull(nextIndex)
                    if (nextTrack != null) {
                        currentTrack = nextTrack
                        startAudioPlayback(nextTrack)
                    }
                }
            }
            "PREVIOUS" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val prevIndex = (index - 1).coerceAtLeast(0)
                    val prevTrack = playlist.getOrNull(prevIndex)
                    if (prevTrack != null) {
                        currentTrack = prevTrack
                        startAudioPlayback(prevTrack)
                    }
                }
            }
            "SEEK_TO" -> {
                if (playlist != null) {
                    currentPlaylist = playlist
                    val track = playlist.getOrNull(index)
                    if (track != null) currentTrack = track
                }
                val position = intent.getLongExtra("seek_position", 0L)
                seekAudioPlayback(position)
            }
            "CLEAR_CACHE" -> {
                albumArtCache.clear()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        stopAudioPlayback()
        mediaSession.release()
        playerViewModel.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Music player controls"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicPlayerService")
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
    }

    private fun observePlayerState() {
        serviceScope.launch {
            playerViewModel.playerInfo.collectLatest { playerInfo ->
                val track = playerInfo.track
                val coverUrl = track?.coverUrl
                if (track == null || coverUrl == null) {
                    stopForeground(true)
                    artLoadingJob?.cancel()
                    return@collectLatest
                }

                if (!albumArtCache.containsKey(coverUrl)) {
                    artLoadingJob?.cancel()
                    artLoadingJob = serviceScope.launch(Dispatchers.IO) {
                        try {
                            val bitmap = BitmapFactory.decodeStream(URL(coverUrl).openConnection().getInputStream())
                            if (bitmap != null) {
                                albumArtCache[coverUrl] = bitmap
                                updateNotification(playerViewModel.playerInfo.value)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                updateNotification(playerInfo)
                updateMediaSessionState(playerInfo)

            }
        }
    }

    private fun updateNotification(playerInfo: PlayerInfo?) {
        val track = playerInfo?.track
        val coverUrl = track?.coverUrl
        if (track == null || coverUrl == null) {
            stopForeground(true)
            return
        }

        val art = albumArtCache[coverUrl]
        val notification = createNotification(track, playerInfo.state is PlayerState.Playing, art)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateMediaSessionState(playerInfo: PlayerInfo) {
        val track = playerInfo.track
        val progress = playerInfo.progress

        val state = if (playerInfo.state is PlayerState.Playing) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, progress.currentPosition, 1.0f)
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
        )

        val metadataBuilder = MediaMetadataCompat.Builder()
        if (track != null) {
            metadataBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, progress.totalDuration)
        }
        mediaSession.setMetadata(metadataBuilder.build())
        mediaSession.isActive = true
    }

    private fun createNotification(track: Track, isPlaying: Boolean, albumArt: Bitmap?): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val intent = launchIntent ?: Intent()
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track.title ?: "Unknown Track")
            .setContentText(track.artist ?: "Unknown Artist")
            .setSmallIcon(applicationInfo.icon)
            .setContentIntent(pendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                ).build()
            )
            .addAction(
                if (isPlaying) {
                    NotificationCompat.Action.Builder(
                        android.R.drawable.ic_media_pause,
                        "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)
                    ).build()
                } else {
                    NotificationCompat.Action.Builder(
                        android.R.drawable.ic_media_play,
                        "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY)
                    ).build()
                }
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_next,
                    "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Stop",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)
                ).build()
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setLargeIcon(albumArt)

        return notificationBuilder.build()
    }

    private fun cleanupMediaPlayer() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
        mediaPlayer = null
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                val pos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                playerViewModel.updateTrackPosition(pos)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun startAudioPlayback(track: Track) {
        val storagePath = track.storagePath ?: return
        try {
            cleanupMediaPlayer()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(storagePath)
                prepareAsync()
                setOnPreparedListener { mp ->
                    serviceScope.launch {
                        val actualDuration = mp.duration.toLong()
                        playerViewModel.updateTrackDuration(actualDuration)
                        mp.start()
                        startProgressUpdates()
                    }
                }
                setOnCompletionListener { mp ->
                    stopProgressUpdates()
                    playNextTrack()
                }
                setOnErrorListener { mp, what, extra ->
                    stopProgressUpdates()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseAudioPlayback() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
            }
        }
        stopProgressUpdates()
    }

    private fun resumeAudioPlayback() {
        mediaPlayer?.let { mp ->
            if (!mp.isPlaying) {
                mp.start()
            }
        }
        startProgressUpdates()
    }

    private fun stopAudioPlayback() {
        cleanupMediaPlayer()
        stopProgressUpdates()
        serviceScope.launch { playerViewModel.stop() }
    }

    private fun seekAudioPlayback(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        playerViewModel.updateTrackPosition(position)
    }

    private fun playNextTrack() {
        val playlist = currentPlaylist
        val current = currentTrack
        if (playlist != null && current != null) {
            val idx = playlist.indexOfFirst { it.id == current.id }
            val next = if (idx != -1 && idx + 1 < playlist.size) playlist[idx + 1] else null
            if (next != null) {
                startAudioPlayback(next)
            }
        }
    }

    private fun playPreviousTrack() {
        val playlist = currentPlaylist
        val current = currentTrack
        if (playlist != null && current != null) {
            val idx = playlist.indexOfFirst { it.id == current.id }
            val prev = if (idx > 0) playlist[idx - 1] else null
            if (prev != null) {
                startAudioPlayback(prev)
            }
        }
    }
} 