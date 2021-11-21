package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
import android.animation.Animator.AnimatorListener
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
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme1Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import kotlinx.coroutines.*

@AndroidEntryPoint
class Enigme1Fragment : Fragment() {


    private lateinit var binding: FragmentEnigme1Binding
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

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

        binding.buttonBack.setOnClickListener {
            ioScope.launch {
                enigmeViewModel.setEnigmeOpen("Death Chapter", 1);
                findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
            }
        }


        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "Death Chapter")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("tagTrue", it.toString())
                mediaPlayer.pause()
                loadAnimation()
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
                                    Toast.makeText(activity, "Resolved", Toast.LENGTH_SHORT)
                                        .show()
                                    indice = enigme.indice
                                    state = enigme.state
                                    loadAnimation()
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



        binding.imageViewEnigme1Indice1.setOnClickListener {
            showImageFragment("scene_door")
        }
        binding.imageViewEnigme1Indice2.setOnClickListener {
            showImageFragment("scene_victime")
        }
        binding.readStory.setOnClickListener {
            binding.readStory.playAnimation()
            binding.readStory.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    showTextFragment("Enigme1")
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun loadAnimation() {
        binding.imageViewEnigme1Indice1.visibility = View.INVISIBLE
        binding.imageViewEnigme1Indice2.visibility = View.INVISIBLE
        binding.edtReponse.visibility = View.INVISIBLE
        binding.btnRepondre.visibility = View.INVISIBLE
        binding.animateEnigmeDone.visibility = View.VISIBLE

        binding.animateEnigmeDone.setAnimation("done.json")
        binding.animateEnigmeDone.playAnimation()
        binding.animateEnigmeDone.addAnimatorListener(object :
            AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                ioScope.launch {
                    enigmeViewModel.setEnigmeOpen("Death Chapter", 1);
                    if (findNavController().currentDestination?.label == "fragment_enigme1")
                        findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun testReponse(): Boolean {

        if ((binding.edtReponse.editText!!.text.toString().lowercase().contains("heure")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("moment")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("instant"))
            &&
            (binding.edtReponse.editText!!.text.toString().contains("couche")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("coucher")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("roupiller")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("roupille")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("dormir")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("dors")
                    || binding.edtReponse.editText!!.text.toString().lowercase().contains("roupille"))
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
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_1)
        mediaPlayer.start()
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