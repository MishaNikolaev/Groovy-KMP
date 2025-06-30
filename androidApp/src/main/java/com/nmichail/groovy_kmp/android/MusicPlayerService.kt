package com.nmichail.groovy_kmp.android

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
    private var artLoadingJob: Job? = null
    private val albumArtCache = mutableMapOf<String, Bitmap>()
    
    private var mediaPlayer: MediaPlayer? = null
    
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        // ... existing code ...
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

        when (intent?.action) {
            "START_PLAYING" -> {
                val trackTitle = intent.getStringExtra("track_title") ?: "Unknown Track"
                val trackArtist = intent.getStringExtra("track_artist") ?: "Unknown Artist"
                val trackStoragePath = intent.getStringExtra("track_storage_path") ?: ""
                val trackCoverUrl = intent.getStringExtra("track_cover_url")
                
                val track = Track(
                    id = null,
                    title = trackTitle,
                    artist = trackArtist,
                    artistId = null,
                    albumId = null,
                    coverUrl = trackCoverUrl,
                    duration = null,
                    storagePath = trackStoragePath
                )
                
                currentTrack = track
                startAudioPlayback(track)
                println("Started playing track from service: ${track.title}")
            }
            "CLEAR_CACHE" -> {
                albumArtCache.clear()
                println("Album art cache cleared.")
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
                            println("Failed to load album art: ${e.message}")
                        }
                    }
                }

                updateNotification(playerInfo)
                updateMediaSessionState(playerInfo)

                // Sync MediaPlayer state
                when (playerInfo.state) {
                    is PlayerState.Playing -> mediaPlayer?.let { if (!it.isPlaying) it.start() }
                    is PlayerState.Paused -> mediaPlayer?.let { if (it.isPlaying) it.pause() }
                    is PlayerState.Stopped -> cleanupMediaPlayer()
                    else -> Unit
                }
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
        // Create intent for opening the app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track.title ?: "Unknown Track")
            .setContentText(track.artist ?: "Unknown Artist")
            .setSmallIcon(R.mipmap.ic_launcher)
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
            println("Cleaned up previous MediaPlayer instance")
        }
        mediaPlayer = null
    }
    
    private fun startAudioPlayback(track: Track) {
        try {
            println("Starting audio playback for track: ${track.title}")
            println("Audio URL: ${track.storagePath}")
            
            cleanupMediaPlayer() // Stop any existing playback without changing ViewModel state
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(track.storagePath)
                prepareAsync()
                setOnPreparedListener { mp ->
                    println("MediaPlayer prepared successfully for: ${track.title}")

                    serviceScope.launch {
                        val actualDuration = mp.duration.toLong()
                        if (actualDuration > 0) {
                            println("Actual track duration: ${actualDuration}ms for track '${track.title}'")
                            playerViewModel.updateTrackDuration(actualDuration)
                        } else {
                            println("!!! Failed to get valid duration from MediaPlayer for track '${track.title}'. Received: ${actualDuration}ms")
                        }

                        mp.start()
                        println("Started audio playback for: ${track.title}")

                        playerViewModel.resume()
                    }
                }
                setOnCompletionListener { mp ->
                    println("Audio playback completed for: ${track.title}")
                }
                setOnErrorListener { mp, what, extra ->
                    println("Audio playback error for ${track.title}: what=$what, extra=$extra")
                    true
                }
            }
        } catch (e: Exception) {
            println("Failed to start audio playback: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun pauseAudioPlayback() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                println("Paused audio playback")
            }
        }
        serviceScope.launch { playerViewModel.pause() }
    }
    
    private fun resumeAudioPlayback() {
        mediaPlayer?.let { mp ->
            if (!mp.isPlaying) {
                mp.start()
                println("Resumed audio playback")
            }
        }
        serviceScope.launch { playerViewModel.resume() }
    }
    
    private fun stopAudioPlayback() {
        cleanupMediaPlayer()
        serviceScope.launch { playerViewModel.stop() }
    }
    
    private fun seekAudioPlayback(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
    }
} 