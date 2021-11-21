package fr.mastergime.meghasli.escapegame.ui.fragments

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.AlertDialogBinding
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme1Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Enigme1Fragment : Fragment() {


    private lateinit var binding: FragmentEnigme1Binding

    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnigme1Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_1)
        hideKeyBoard()

        binding.readVoice.setOnClickListener {
            resetAudioVoice()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }


        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "Death Chapter")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("tagTrue", it.toString())
                Toast.makeText(activity, "Enigme deja resolue", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
            } else {
                Log.d("tagFalse", it.toString())
            }
        })

        enigmeViewModel.getEnigme("Death Chapter").observe(viewLifecycleOwner, Observer { enigme ->
            if (enigme != null) {


                binding.btnRepondre.setOnClickListener {
                    val inputMethodManager =
                        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                    binding.btnRepondre.clearFocus()
                    if (testReponse()) {
                        enigmeViewModel.changeEnigmeStateToTrue(enigme).observe(viewLifecycleOwner,
                            Observer { stateChanged ->
                                if (stateChanged) {
                                    Toast.makeText(activity, "Enigme resolue", Toast.LENGTH_SHORT)
                                        .show()
                                    indice = enigme.indice
                                    state = enigme.state
                                    findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
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



        binding.imageViewEnigme1Indice1.setOnClickListener {
            showImageFragment("scene_door")
        }
        binding.imageViewEnigme1Indice2.setOnClickListener {
            showImageFragment("scene_victime")
        }
        binding.readStory.setOnClickListener {
            showTextFragment("Enigme1")
        }
    }

    private fun testReponse(): Boolean {

        if ((binding.edtReponse.editText!!.text.toString().contains("heure")
                    || binding.edtReponse.editText!!.text.toString().contains("moment")
                    || binding.edtReponse.editText!!.text.toString().contains("instant"))
            &&
            (binding.edtReponse.editText!!.text.toString().contains("couche")
                    || binding.edtReponse.editText!!.text.toString().contains("coucher")
                    || binding.edtReponse.editText!!.text.toString().contains("roupiller")
                    || binding.edtReponse.editText!!.text.toString().contains("roupille")
                    || binding.edtReponse.editText!!.text.toString().contains("dormir")
                    || binding.edtReponse.editText!!.text.toString().contains("dors")
                    || binding.edtReponse.editText!!.text.toString().contains("roupille"))
        ) {
            return true
        }
        return false
    }

    private suspend fun startEnigmaStoryVoice() {
        delay(500)
        mediaPlayer.start()
    }

    private fun resetAudioVoice() {
        mediaPlayer.reset()
    }

    private fun showImageFragment(imageName: String) {
        val dialogg = ImgDialogFragment()
        val bundle = Bundle()
        bundle.putString("ImageName", imageName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager, "")
    }

    private fun showTextFragment(TextName: String) {

        val dialogg = textDialogFragment()
        val bundle = Bundle()
        bundle.putString("TextName", TextName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager, "")

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

    fun hideKeyBoard() {
        binding.csLayout.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.btnRepondre.clearFocus()
        }
    }

}