package com.example.calmly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object SoundManager {

    fun getMeditationSounds(): List<Sound> {
        return listOf(
            Sound(1, "Forest Sounds", "Peaceful nature sounds", R.raw.forest, R.drawable.ic_play, SoundCategory.MEDITATION),
            Sound(2, "Rain Drops", "Gentle rainfall", R.raw.rain, R.drawable.ic_play, SoundCategory.MEDITATION),
            Sound(3, "Ocean Waves", "Calming ocean sounds", R.raw.ocean, R.drawable.ic_play, SoundCategory.MEDITATION),
            Sound(4, "Campfire", "Crackling fire sounds", R.raw.campfire, R.drawable.ic_play, SoundCategory.MEDITATION),
            Sound(5, "Mountain Wind", "Gentle wind sounds", R.raw.wind, R.drawable.ic_play, SoundCategory.MEDITATION)
        )
    }

    fun getSleepSounds(): List<Sound> {
        return listOf(
            Sound(6, "White Noise", "Consistent white noise", R.raw.whitenoise, R.drawable.ic_play, SoundCategory.SLEEP),
            Sound(7, "Lullaby", "Soft lullaby melody", R.raw.lullaby, R.drawable.ic_play, SoundCategory.SLEEP),
            Sound(8, "Fan Sound", "Cooling fan noise", R.raw.fan, R.drawable.ic_play, SoundCategory.SLEEP),
//            Sound(9, "Deep Hum", "Low frequency hum", R.raw.deephum, R.drawable.ic_play, SoundCategory.SLEEP),
            Sound(10, "Night Sounds", "Crickets and night ambience", R.raw.night, R.drawable.ic_play, SoundCategory.SLEEP)
        )
    }

}