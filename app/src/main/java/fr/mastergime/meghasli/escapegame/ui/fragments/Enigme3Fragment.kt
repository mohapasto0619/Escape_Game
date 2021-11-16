package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme3Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Enigme3Fragment : Fragment(R.layout.fragment_enigme3) {

    private lateinit var binding: FragmentEnigme3Binding
    private val mediaPlayer: MediaPlayer =
        MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEnigme3Binding.bind(view)

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }

        binding.imageViewEnigme3.setOnClickListener{
            showDialogFragment( "live__")
        }

    }

    private suspend fun startEnigmaStoryVoice() {
        delay(500)
        mediaPlayer.start()
    }

    private fun showDialogFragment( imageName : String) {
        val dialogg = ImgDialogFragment ()
        val bundle = Bundle()
        bundle.putString("ImageName",imageName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager,"")
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

}