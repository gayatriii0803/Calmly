package com.example.calmly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class Sound(
    val id: Int,
    val title: String,
    val description: String,
    val resourceId: Int,
    val iconResId: Int,
    val category: SoundCategory,
    var isPlaying: Boolean = false
)

enum class SoundCategory {
    MEDITATION,
    SLEEP
}