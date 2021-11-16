package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentOptionelEnigmeBinding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OptionelEnigmeFragment : Fragment(R.layout.fragment_optionel_enigme) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: FragmentOptionelEnigmeBinding
    private val enigmeViewModel : EnigmesViewModel by viewModels()
    private val sessionViewModel : SessionViewModel by viewModels()

    private val job =  SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_temps)

        binding = FragmentOptionelEnigmeBinding.bind(view)

        chooseFirst()
        chooseSeconde()
        chooseThird()
        chooseFourth()

    }

    private fun chooseFirst() {
        binding.doorOne.setOnClickListener {

            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_door_one)
            binding.fragmentOptionel.startAnimation(animation)

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE

            ioScope.launch {
                enigmeViewModel.setOptionalEnigmeState(1)
            }



        }
    }

    private fun chooseSeconde() {
        binding.doorTwo.setOnClickListener {

            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_door_one)
            binding.fragmentOptionel.startAnimation(animation)

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE

            ioScope.launch {
                enigmeViewModel.setOptionalEnigmeState(1)
            }
        }
    }

    private fun chooseThird() {
        binding.doorThree.setOnClickListener {

            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_door_one)
            binding.fragmentOptionel.startAnimation(animation)

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE
            binding.doorOpned.visibility = View.VISIBLE
            binding.doorOne.visibility = View.INVISIBLE

            ioScope.launch {
                binding.doorOpned.setAnimation("door_three.json")
                enigmeViewModel.setOptionalEnigmeState(0)
                sessionViewModel.setUpBonusTimer()
            }

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                    TODO("Not yet implemented")
                }

            })

        }
    }

    private fun chooseFourth() {
        binding.doorFour.setOnClickListener {

            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_door_one)
            binding.fragmentOptionel.startAnimation(animation)

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE

            ioScope.launch {
                enigmeViewModel.setOptionalEnigmeState(1)
            }
        }
    }

}