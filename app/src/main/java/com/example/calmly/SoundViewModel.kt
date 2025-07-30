package com.example.calmly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundViewModel : ViewModel() {

    private val _currentPlayingSound = MutableLiveData<Sound?>()
    val currentPlayingSound: LiveData<Sound?> = _currentPlayingSound
    private val _timerMinutes = MutableLiveData<Int>()
    val timerMinutes: LiveData<Int> = _timerMinutes

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private var timerJob: Job? = null

    fun setCurrentPlayingSound(sound: Sound?) {
        _currentPlayingSound.value = sound
    }

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun getCurrentSound(): Sound? {
        return _currentPlayingSound.value
    }
    fun setTimer(minutes: Int) {
        _timerMinutes.value = minutes
        timerJob?.cancel()

        if (minutes > 0) {
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                delay(minutes * 60 * 1000L)
                // Stop playback after timer expires
                // Call service stop method
            }
        }
    }
}