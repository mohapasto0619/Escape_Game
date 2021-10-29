package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.mastergime.meghasli.escapegame.databinding.NfcNoSupportedBinding


class NoSupportedNFC : Fragment() {
    private lateinit var binding: NfcNoSupportedBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NfcNoSupportedBinding.inflate(inflater)
        return binding.root
    }
}