package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.coroutines.*

@AndroidEntryPoint
class Enigme21Fragment : Fragment(R.layout.fragment_enigme21) {

    private lateinit var binding: FragmentEnigme21Binding
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_1)
        binding = FragmentEnigme21Binding.bind(view)
        hideKeyBoard()

        binding.readVoice.setOnClickListener {
            resetAudioVoice()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }

        binding.buttonBack.setOnClickListener {
            ioScope.launch {
                enigmeViewModel.setEnigmeOpen("Crime Chapter P1", 1);
                findNavController().navigate(R.id.action_enigme21Fragment_to_gameFragment)
            }
        }

        enigmeViewModel.updateEnigmeState(RoomSessionFragment.sessionId, "Crime Chapter P1")
        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {

            if (it) {
                Log.d("tagTrue", it.toString())
                mediaPlayer.pause()
                loadAnimation()
            } else {
                Log.d("tagFalse", it.toString())
//                binding.csResolu.visibility=View.GONE
//                binding.csNonResolue.visibility=View.VISIBLE
            }
        })

        enigmeViewModel.getEnigme("Crime Chapter P1")
            .observe(viewLifecycleOwner, Observer { enigme ->
                if (enigme != null) {

                    binding.btnRepondre.setOnClickListener {
                        val inputMethodManager =
                            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                        binding.btnRepondre.clearFocus()
                        //test if user's response = enigme response
                        if (binding.edtReponse.editText!!.text.toString() == enigme.reponse) {
                            enigmeViewModel.changeEnigmeStateToTrue(enigme)
                                .observe(viewLifecycleOwner,
                                    Observer { stateChanged ->
                                        if (stateChanged) {
                                            Toast.makeText(activity, "Resolved", Toast.LENGTH_SHORT)
                                                .show()
                                            Enigme1Fragment.indice = enigme.indice
                                            Enigme1Fragment.state = enigme.state
                                            loadAnimation()
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "Error network",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                        } else {
                            Toast.makeText(activity, "Wrong Answer", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            })



        binding.imageViewEnigme2Indice1.setOnClickListener {
            showDialogFragment("murder_sketch")
        }
        binding.imageViewEnigme2Indice2.setOnClickListener {
            showDialogFragment("note1636216613307")
        }

        binding.readStory.setOnClickListener {
            binding.readStory.playAnimation()
            binding.readStory.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    showTextFragment("Enigme21")
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun resetAudioVoice() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_1)
        mediaPlayer.start()
    }


    private fun loadAnimation() {
        binding.imageViewEnigme2Indice1.visibility = View.INVISIBLE
        binding.imageViewEnigme2Indice2.visibility = View.INVISIBLE
        binding.edtReponse.visibility = View.INVISIBLE
        binding.btnRepondre.visibility = View.INVISIBLE
        binding.animateEnigmeDone.visibility = View.VISIBLE

        binding.animateEnigmeDone.setAnimation("done.json")
        binding.animateEnigmeDone.playAnimation()
        binding.animateEnigmeDone.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                ioScope.launch {
                    enigmeViewModel.setEnigmeOpen("Crime Chapter P1", 1);
                    if (findNavController().currentDestination?.label == "fragment_enigme21")
                        findNavController().navigate(R.id.action_enigme21Fragment_to_gameFragment)
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
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