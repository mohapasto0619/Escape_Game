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
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme21Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Enigme21Fragment : Fragment(R.layout.fragment_enigme21) {

    private lateinit var binding: FragmentEnigme21Binding
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private lateinit var  mediaPlayer: MediaPlayer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_1)

        binding = FragmentEnigme21Binding.bind(view)

        binding.imageViewEnigme2Indice1.setOnClickListener {
            resetAudioVoice()
        }

        binding.imageViewEnigme2Indice2.setOnClickListener {
            resetAudioVoice()
        }

        lifecycleScope.launch(Dispatchers.IO){
            startEnigmaStoryVoice()
        }



        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "Crime Chapter P1")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {

            if (it) {
                Log.d("tagTrue", it.toString())
//                binding.csResolu.visibility=View.VISIBLE
//                binding.csNonResolue.visibility=View.GONE
                Toast.makeText(activity, "Enigme deja resolue", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme21Fragment_to_gameFragment)
            } else {
                Log.d("tagFalse", it.toString())
//                binding.csResolu.visibility=View.GONE
//                binding.csNonResolue.visibility=View.VISIBLE
            }
        })

        enigmeViewModel.getEnigme("Crime Chapter P1").observe(viewLifecycleOwner, Observer { enigme ->
            if (enigme != null) {

                binding.btnRepondre.setOnClickListener {

                    //test if user's response = enigme response
                    if (binding.edtReponse.editText!!.text.toString() == enigme.reponse) {
                        enigmeViewModel.changeEnigmeStateToTrue(enigme).observe(viewLifecycleOwner,
                            Observer { stateChanged ->
                                if (stateChanged) {
                                    Toast.makeText(activity, "Enigme resolue", Toast.LENGTH_SHORT)
                                        .show()
                                    Enigme1Fragment.indice = enigme.indice
                                    Enigme1Fragment.state = enigme.state
                                    findNavController().navigate(R.id.action_enigme21Fragment_to_gameFragment)
                                } else {
                                    Toast.makeText(activity, "Enigme resolue", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    } else {
                        Toast.makeText(activity, "fausse reponse", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })



        binding.imageViewEnigme2Indice1.setOnClickListener{
            showDialogFragment( "murder_sketch")
        }
        binding.imageViewEnigme2Indice2.setOnClickListener{
            showDialogFragment( "note1636216613307")
        }

        binding.readStory.setOnClickListener{
            showTextFragment( "Enigme21")
        }
    }

    private fun resetAudioVoice() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
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

    private fun showTextFragment(TextName : String ) {

        val dialogg = textDialogFragment ()
        val bundle = Bundle()
        bundle.putString("TextName",TextName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager,"")

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    companion object  {
        var indice : String? = null
        var state : Boolean = false
    }

}