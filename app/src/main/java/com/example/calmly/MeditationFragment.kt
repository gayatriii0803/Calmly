package com.example.calmly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.calmly.databinding.FragmentMeditationBinding
import com.example.calmly.Sound
import com.example.calmly.SoundAdapter
import com.example.calmly.SoundViewModel
import com.example.calmly.SoundManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MeditationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeditationFragment : Fragment() {

    private var _binding: FragmentMeditationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SoundViewModel by activityViewModels()
    private lateinit var soundAdapter: SoundAdapter

    var onSoundSelected: ((Sound) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        soundAdapter = SoundAdapter(SoundManager.getMeditationSounds()) { sound ->
            onSoundSelected?.invoke(sound)
        }

        binding.meditationRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = soundAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.currentPlayingSound.observe(viewLifecycleOwner) { sound ->
            soundAdapter.updatePlayingSound(sound)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}