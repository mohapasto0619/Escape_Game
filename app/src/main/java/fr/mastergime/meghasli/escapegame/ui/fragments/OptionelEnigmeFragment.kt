package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
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
import kotlinx.coroutines.*

@AndroidEntryPoint
class OptionelEnigmeFragment : Fragment(R.layout.fragment_optionel_enigme) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: FragmentOptionelEnigmeBinding
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.audio_enigme_temps)

        binding = FragmentOptionelEnigmeBinding.bind(view)

        chooseFirst()
        chooseSeconde()
        chooseThird()
        chooseFourth()

        binding.readStory.setOnClickListener {
            showTextFragment("enigmeTime")
        }

        binding.buttonBack.setOnClickListener {
            ioScope.launch {
                findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
            }
        }

    }

    private fun showTextFragment(TextName: String) {

        val dialogg = textDialogFragment()
        val bundle = Bundle()
        bundle.putString("TextName", TextName)
        dialogg.arguments = bundle
        dialogg.show(parentFragmentManager, "")

    }

    private fun chooseFirst() {
        binding.doorOne.setOnClickListener {

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE
            binding.doorOne.visibility = View.INVISIBLE
            binding.doorOpned.visibility = View.VISIBLE

            ioScope.launch {
                binding.doorOpned.setAnimation("door_one.json")
                enigmeViewModel.setOptionalEnigmeState(1, RoomSessionFragment.sessionId)
                sessionViewModel.starTimerSession(RoomSessionFragment.sessionId)
                delay(1000)
                binding.doorOpned.setAnimation("error.json")
                binding.doorOpned.playAnimation()
                binding.doorOpned.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
            }
        }
    }

    private fun chooseSeconde() {
        binding.doorTwo.setOnClickListener {
            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE
            binding.doorOne.visibility = View.INVISIBLE
            binding.doorOpned.visibility = View.VISIBLE
            ioScope.launch {
                binding.doorOpned.setAnimation("door_two.json")
                enigmeViewModel.setOptionalEnigmeState(1, RoomSessionFragment.sessionId)
                sessionViewModel.starTimerSession(RoomSessionFragment.sessionId)
                binding.doorOpned.clearAnimation()
                delay(1000)
                binding.doorOpned.setAnimation("error.json")
                binding.doorOpned.playAnimation()
                binding.doorOpned.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
            }
        }
    }

    private fun chooseThird() {
        binding.doorThree.setOnClickListener {

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE
            binding.doorOne.visibility = View.INVISIBLE
            binding.doorOpned.visibility = View.VISIBLE

            ioScope.launch {
                binding.doorOpned.setAnimation("door_three.json")
                enigmeViewModel.setOptionalEnigmeState(0, RoomSessionFragment.sessionId)
                sessionViewModel.setUpBonusTimer()
                sessionViewModel.starTimerSession(RoomSessionFragment.sessionId)
                delay(1000)
                binding.doorOpned.setAnimation("done.json")
                binding.doorOpned.playAnimation()
                binding.doorOpned.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
            }

        }
    }

    private fun chooseFourth() {
        binding.doorFour.setOnClickListener {

            binding.doorTwo.visibility = View.INVISIBLE
            binding.doorThree.visibility = View.INVISIBLE
            binding.doorFour.visibility = View.INVISIBLE
            binding.doorOne.visibility = View.INVISIBLE
            binding.doorOpned.visibility = View.VISIBLE

            ioScope.launch {
                binding.doorOpned.setAnimation("door4.lottie_2.json")
                enigmeViewModel.setOptionalEnigmeState(1, RoomSessionFragment.sessionId)
                sessionViewModel.starTimerSession(RoomSessionFragment.sessionId)
                delay(1000)
                binding.doorOpned.setAnimation("error.json")
                binding.doorOpned.playAnimation()
                binding.doorOpned.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        findNavController().navigate(R.id.action_optionel_enigmeFragment_to_gameFragment)
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }
                })
            }

        }
    }

    private fun loadAnimation() {
        TODO("Not yet implemented")
    }

}