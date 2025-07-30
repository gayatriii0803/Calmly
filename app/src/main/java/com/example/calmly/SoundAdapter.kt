package com.example.calmly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calmly.R
import com.example.calmly.databinding.ItemSoundCardBinding
import com.example.calmly.Sound

class SoundAdapter(
    private val sounds: List<Sound>,
    private val onSoundClick: (Sound) -> Unit
) : RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    private var currentPlayingSound: Sound? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val binding = ItemSoundCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(sounds[position])
    }

    override fun getItemCount(): Int = sounds.size

    fun updatePlayingSound(sound: Sound?) {
        val previousIndex = sounds.indexOfFirst { it.id == currentPlayingSound?.id }
        currentPlayingSound = sound
        val currentIndex = sounds.indexOfFirst { it.id == sound?.id }

        if (previousIndex != -1) notifyItemChanged(previousIndex)
        if (currentIndex != -1) notifyItemChanged(currentIndex)
    }

    inner class SoundViewHolder(private val binding: ItemSoundCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sound: Sound) {
            binding.apply {
                soundTitle.text = sound.title
                soundDescription.text = sound.description

                val isCurrentlyPlaying = currentPlayingSound?.id == sound.id
                playPauseButton.setImageResource(
                    if (isCurrentlyPlaying) R.drawable.ic_pause else R.drawable.ic_play
                )

                playPauseButton.setOnClickListener {
                    onSoundClick(sound)
                }

                root.setOnClickListener {
                    onSoundClick(sound)
                }
            }
        }
    }
}