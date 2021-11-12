package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentOptionelEnigmeBinding

@AndroidEntryPoint
class OptionelEnigmeFragment : Fragment(R.layout.fragment_optionel_enigme) {

    private lateinit var binding: FragmentOptionelEnigmeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        }
    }

    private fun chooseSeconde() {

    }

    private fun chooseThird() {

    }

    private fun chooseFourth() {

    }

}