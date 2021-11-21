package fr.mastergime.meghasli.escapegame.ui.fragments

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme4ragmentBinding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.*

@AndroidEntryPoint
class Enigme4ragment : Fragment(R.layout.fragment_enigme4ragment) {


    private lateinit var binding: FragmentEnigme4ragmentBinding
    private lateinit var mediaPlayer: MediaPlayer
    private val enigmeViewModel: EnigmesViewModel by viewModels()

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_final)
        binding = FragmentEnigme4ragmentBinding.bind(view)
        hideKeyBoard()

        binding.readVoice.setOnClickListener {
            resetAudioVoice()
        }

        binding.buttonBack.setOnClickListener {
            ioScope.launch {
                enigmeViewModel.setEnigmeOpen("The Last",1);
                if(findNavController().currentDestination?.label == "fragment_enigme4ragment")
                findNavController().navigate(R.id.action_enigme4Fragment_to_gameFragment)
            }
        }

        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "The Last")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("tagTrue", it.toString())
                Toast.makeText(activity, "Already Resolved", Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(activity, "Resolved", Toast.LENGTH_SHORT)
                                        .show()
                                    Enigme4ragment.indice = enigme.indice
                                    Enigme4ragment.state = enigme.state
                                    findNavController().navigate(R.id.action_enigme4Fragment_to_gameFragment)
                                } else {
                                    Toast.makeText(activity, "Error network", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    } else {
                        Toast.makeText(activity, "Wrong Answer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }

        binding.imageViewEnigme5.setOnClickListener {
            showDialogFragment("enigme_5")
        }

        binding.readStory.setOnClickListener {
            showTextFragment("enigmeFinal")
        }
    }


    private fun showTextFragment(TextName: String) {

        val dialogg = textDialogFragment()
        val bundle = Bundle()
        bundle.putString("TextName", TextName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager, "")

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }

//        binding.imageViewEnigme5.setOnClickListener {
//            showDialogFragment("enigme_5")
//        }

    }

    private suspend fun startEnigmaStoryVoice() {
        delay(500)
        mediaPlayer.start()
    }

    private fun showDialogFragment(imageName: String) {
        val dialogg = ImgDialogFragment()
        val bundle = Bundle()
        bundle.putString("ImageName", imageName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager, "")
    }

    private fun resetAudioVoice() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_final)
        mediaPlayer.start()
    }

    fun hideKeyBoard() {
        binding.csLayout.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.btnRepondre.clearFocus()
        }
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

    companion object {
        var indice: String? = null
        var state: Boolean = false
    }

}