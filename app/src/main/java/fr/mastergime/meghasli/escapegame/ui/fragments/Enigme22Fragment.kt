package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme22Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class Enigme22Fragment : Fragment() {

    private lateinit var binding: FragmentEnigme22Binding
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private lateinit var  mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnigme22Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewEnigme22Indice1.setOnClickListener{
            resetAudioVoice()
        }

        binding.imageViewEnigme22Indice2.setOnClickListener{
            resetAudioVoice()
        }


        lifecycleScope.launch(Dispatchers.IO){
            startEnigmaStoryVoice()
        }

        var enigmeTag = arguments?.get("enigmeTag") as String
        Log.d("sessid", GameFragment.sessionId)
        enigmeViewModel.updateEnigmeState(GameFragment.sessionId, enigmeTag)
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {


            if (it) {
                Log.d("tagTrue", it.toString())
//                binding.csResolu.visibility=View.VISIBLE
//                binding.csNonResolue.visibility=View.GONE
                Toast.makeText(activity, "Enigme deja resolue", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme22Fragment_to_gameFragment)
            } else {
                Log.d("tagFalse", it.toString())
//                binding.csResolu.visibility=View.GONE
//                binding.csNonResolue.visibility=View.VISIBLE
            }
        })
    }

    private fun resetAudioVoice(){
       if(!mediaPlayer.isPlaying){
           mediaPlayer.start()
       }
    }

    private suspend fun startEnigmaStoryVoice() {
        delay(500)
        mediaPlayer.start()
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