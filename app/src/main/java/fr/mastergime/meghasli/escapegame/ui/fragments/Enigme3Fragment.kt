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
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme1Binding
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme3Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Enigme3Fragment : Fragment() {

    private lateinit var binding: FragmentEnigme3Binding
    //private lateinit var mediaPlayer: MediaPlayer
    private val enigmeViewModel: EnigmesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       // mediaPlayer = MediaPlayer.create(requireContext(), R.raw.intro_jeux)

        /*lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }*/


        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "enigme3")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("tagTrue", it.toString())
                Toast.makeText(activity, "Enigme deja resolue", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme3Fragment_to_gameFragment)
            } else {
                Log.d("tagFalse", it.toString())
            }
        })

        enigmeViewModel.getEnigme("enigme3").observe(viewLifecycleOwner, Observer { enigme ->
            if (enigme != null) {


                binding.btnRepondre.setOnClickListener {
                    //test if user's response = enigme response
                    if (binding.edtReponse.editText!!.text.toString() == enigme.reponse) {
                        enigmeViewModel.changeEnigmeStateToTrue(enigme).observe(viewLifecycleOwner,
                            Observer { stateChanged ->
                                if (stateChanged) {
                                    Toast.makeText(activity, "Enigme resolue", Toast.LENGTH_SHORT)
                                        .show()
                                    indice = enigme.indice
                                    state = enigme.state
                                    findNavController().navigate(R.id.action_enigme3Fragment_to_gameFragment)
                                } else {
                                    Toast.makeText(activity, "Error network", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    } else {
                        Toast.makeText(activity, "fausse reponse", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnigme3Binding.inflate(inflater)
        return binding.root
    }

    /*private suspend fun startEnigmaStoryVoice() {
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
    private fun resetAudioVoice(){
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }*/


companion object  {
    var indice : String? = null
    var state : Boolean = false
}

}