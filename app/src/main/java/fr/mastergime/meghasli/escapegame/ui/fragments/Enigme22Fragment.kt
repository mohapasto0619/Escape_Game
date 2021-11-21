package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
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
import kotlinx.coroutines.*


@AndroidEntryPoint
class Enigme22Fragment : Fragment() {

    private lateinit var binding: FragmentEnigme22Binding
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
        binding = FragmentEnigme22Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_2)


        binding.readVoice.setOnClickListener {
            resetAudioVoice()
        }

        binding.buttonBack.setOnClickListener {
            ioScope.launch {
                enigmeViewModel.setEnigmeOpen("Crime Chapter P2", 1);
                if(findNavController().currentDestination?.label == "fragment_enigme22")
                findNavController().navigate(R.id.action_enigme22Fragment_to_gameFragment)
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

        lifecycleScope.launch(Dispatchers.IO) {
            startEnigmaStoryVoice()
        }

        binding.imageViewEnigme22Indice1.setOnClickListener {
            showDialogFragment("murder_sketch")
        }
        binding.imageViewEnigme22Indice2.setOnClickListener {
            showDialogFragment("tel_ind")
        }

        binding.readStory.setOnClickListener {
                    showTextFragment("Enigme22")
                }
    }

    private fun loadAnimation() {
        binding.imageViewEnigme22Indice1.visibility = View.INVISIBLE
        binding.imageViewEnigme22Indice2.visibility = View.INVISIBLE
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
                    enigmeViewModel.setEnigmeOpen("Crime Chapter P2", 1);
                    if (findNavController().currentDestination?.label == "fragment_enigme22")
                        findNavController().navigate(R.id.action_enigme22Fragment_to_gameFragment)
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun resetAudioVoice() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_2_2)
        mediaPlayer.start()
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

}