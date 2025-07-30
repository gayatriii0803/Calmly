package com.example.calmly

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.calmly.databinding.ActivityMainBinding
import com.example.calmly.Sound
import com.example.calmly.MediaPlayerService
import com.example.calmly.MeditationFragment
import com.example.calmly.SleepFragment
import com.example.calmly.SoundViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SoundViewModel by viewModels()

    private var mediaPlayerService: MediaPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isServiceBound = true

            mediaPlayerService?.onPlaybackStateChanged = { isPlaying ->
                viewModel.setPlayingState(isPlaying)
                if (!isPlaying && mediaPlayerService?.getCurrentSound() != null) {
                    viewModel.setCurrentPlayingSound(null)
                }
            }

            // Restore current playing sound if service was already running
            mediaPlayerService?.getCurrentSound()?.let { sound ->
                viewModel.setCurrentPlayingSound(sound)
                viewModel.setPlayingState(mediaPlayerService?.isPlaying() ?: false)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaPlayerService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        bindMediaService()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.meditation)
                1 -> getString(R.string.sleep)
                else -> ""
            }
        }.attach()
    }

    private fun saveLastPlayedSound(sound: Sound) {
        val prefs = getSharedPreferences("calmly_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("last_sound_id", sound.id)
            .putString("last_sound_title", sound.title)
            .apply()
    }

    private fun restoreLastPlayedSound() {
        val prefs = getSharedPreferences("calmly_prefs", Context.MODE_PRIVATE)
        val lastSoundId = prefs.getInt("last_sound_id", -1)
        // Restore sound based on ID
    }

    private fun bindMediaService() {
        val intent = Intent(this, MediaPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun handleSoundSelection(sound: Sound) {
        if (!isServiceBound) return

        val currentSound = viewModel.getCurrentSound()

        if (currentSound?.id == sound.id) {
            // Same sound clicked - toggle play/pause
            if (mediaPlayerService?.isPlaying() == true) {
                mediaPlayerService?.pausePlayback()
            } else {
                mediaPlayerService?.resumePlayback()
            }
        } else {
            // New sound selected
            viewModel.setCurrentPlayingSound(sound)
            mediaPlayerService?.playSound(sound)
        }
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MeditationFragment().apply {
                    onSoundSelected = { sound -> handleSoundSelection(sound) }
                }
                1 -> SleepFragment().apply {
                    onSoundSelected = { sound -> handleSoundSelection(sound) }
                }
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}