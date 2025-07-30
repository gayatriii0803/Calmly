package com.example.calmly

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat




class MediaPlayerService : Service() {

    companion object {
        const val CHANNEL_ID = "MediaPlayerChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: Sound? = null
    private val binder = MediaPlayerBinder()

    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_STOP -> stopPlayback()
        }
        return START_STICKY
    }

    fun playSound(sound: Sound) {
        if (currentSound?.id == sound.id && mediaPlayer?.isPlaying == true) {
            pausePlayback()
            return
        }

        stopPlayback()
        currentSound = sound

        try {
            mediaPlayer = MediaPlayer.create(this, sound.resourceId)
            mediaPlayer?.apply {
                isLooping = true
                setOnPreparedListener {
                    start()
                    updateNotification()
                    onPlaybackStateChanged?.invoke(true)
                }
                setOnErrorListener { _, _, _ ->
                    onPlaybackStateChanged?.invoke(false)
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onPlaybackStateChanged?.invoke(false)
        }
    }

    fun pausePlayback() {
        mediaPlayer?.pause()
        updateNotification()
        onPlaybackStateChanged?.invoke(false)
    }

    fun resumePlayback() {
        mediaPlayer?.start()
        updateNotification()
        onPlaybackStateChanged?.invoke(true)
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        currentSound = null
        stopForeground(true)
        onPlaybackStateChanged?.invoke(false)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getCurrentSound(): Sound? {
        return currentSound
    }

    private fun togglePlayPause() {
        if (isPlaying()) {
            pausePlayback()
        } else {
            resumePlayback()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val sound = currentSound ?: return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = Intent(this, MediaPlayerService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MediaPlayerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(sound.title)
            .setContentText(getString(R.string.now_playing))
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(pendingIntent)
            .addAction(
                if (isPlaying()) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying()) getString(R.string.pause) else getString(R.string.play),
                playPausePendingIntent
            )
            .addAction(R.drawable.ic_stop, getString(R.string.stop), stopPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1))
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}