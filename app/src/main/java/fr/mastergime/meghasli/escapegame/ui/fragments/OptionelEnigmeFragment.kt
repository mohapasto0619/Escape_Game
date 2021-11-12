package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentOptionelEnigmeBinding

@AndroidEntryPoint
class OptionelEnigmeFragment : Fragment(R.layout.fragment_optionel_enigme) {

    private lateinit var binding: FragmentOptionelEnigmeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOptionelEnigmeBinding.bind(view)



    }

}