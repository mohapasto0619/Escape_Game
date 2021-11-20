package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme3Binding
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme4ragmentBinding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Enigme4ragment : Fragment(R.layout.fragment_enigme4ragment) {


    private lateinit var binding: FragmentEnigme4ragmentBinding
    private lateinit var  mediaPlayer: MediaPlayer
    private val enigmeViewModel: EnigmesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_final)
        binding = FragmentEnigme4ragmentBinding.bind(view)



      /*  binding.readStory.setOnClickListener{
            showTextFragment( "Enigme1")
        }*/
    }


    private fun showTextFragment(TextName : String ) {

        val dialogg = textDialogFragment ()
        val bundle = Bundle()
        bundle.putString("TextName",TextName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager,"")

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice() }

        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "The Last")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("tagTrue", it.toString())
                Toast.makeText(activity, "Enigme deja resolue", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme3Fragment_to_gameFragment)
            } else {
                Log.d("tagFalse", it.toString())
            }
        })

        enigmeViewModel.getEnigme("The Last").observe(viewLifecycleOwner, Observer { enigme ->
            if (enigme != null) {


                binding.btnRepondre.setOnClickListener {
                    //test if user's response = enigme response
                    if (binding.edtReponse.editText!!.text.toString() == enigme.reponse) {
                        enigmeViewModel.changeEnigmeStateToTrue(enigme).observe(viewLifecycleOwner,
                            Observer { stateChanged ->
                                if (stateChanged) {
                                    Toast.makeText(activity, "Enigme resolue", Toast.LENGTH_SHORT)
                                        .show()
                                    Enigme3Fragment.indice = enigme.indice
                                    Enigme3Fragment.state = enigme.state
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

        binding.imageViewEnigme5.setOnClickListener{
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
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    companion object  {
        var indice : String? = null
        var state : Boolean = false
    }

}