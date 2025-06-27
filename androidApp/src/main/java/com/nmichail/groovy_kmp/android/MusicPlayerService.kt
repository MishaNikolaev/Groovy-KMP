package com.nmichail.groovy_kmp.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_TRACK_URL)
                play(url)
            }
            ACTION_PAUSE -> pause()
            ACTION_RESUME -> resume()
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun play(url: String?) {
        if (url == null) return
        stopMedia()
        val player = MediaPlayer()
        player.setDataSource(url)
        player.setOnPreparedListener {
            player.start()
            isPlaying = true
            showNotification()
        }
        player.setOnCompletionListener {
            stopSelf()
        }
        player.prepareAsync()
        mediaPlayer = player
    }

    private fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                showNotification()
            }
        }
    }

    private fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
                showNotification()
            }
        }
    }

    private fun stopMedia() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    override fun onDestroy() {
        stopMedia()
        super.onDestroy()
    }

    private fun showNotification() {
        val channelId = "music_player_channel"
        val channelName = "Music Player"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Музыка")
            .setContentText(if (isPlaying) "Играет" else "Пауза")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(isPlaying)
        val notification: Notification = builder.build()
        startForeground(1, notification)
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TRACK_URL = "EXTRA_TRACK_URL"
        const val EXTRA_TRACK_TITLE = "EXTRA_TRACK_TITLE"
        const val EXTRA_TRACK_ARTIST = "EXTRA_TRACK_ARTIST"
    }
} 