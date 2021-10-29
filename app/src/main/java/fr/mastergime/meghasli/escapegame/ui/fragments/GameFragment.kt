package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.databinding.FragmentGameBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.android.synthetic.main.fragment_game.*
@AndroidEntryPoint
class GameFragment : Fragment() {
    val sessionViewModel : SessionViewModel by viewModels()
    lateinit var binding : FragmentGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel.updateSessionId()

        binding.quitButton.setOnClickListener{
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner){value ->
            if(value == "Success")
                findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
            else
                Toast.makeText(activity,"Can't leave Session please retry",
                    Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.quitButton.isEnabled = true
        }

        sessionViewModel.sessionId.observe(viewLifecycleOwner){
            sessionId = it
        }

        // le tag nfc de l'Enigme 1
        binding.btnEnigma1.setOnClickListener {
            val bundle = bundleOf("enigmeTag" to "enigme1")
            findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment,bundle)
        }
        // le tag nfc de l'Enigme 2   partie1
        binding.btnEnigma21.setOnClickListener {
            val bundle = bundleOf("enigmeTag" to "enigme2")
            findNavController().navigate(R.id.action_gameFragment_to_enigme21Fragment,bundle)
        }
        // le tag nfc de l'Enigme 2   partie2
        binding.btnEnigma22.setOnClickListener {
            val bundle = bundleOf("enigmeTag" to "enigme2")
            findNavController().navigate(R.id.action_gameFragment_to_enigme22Fragment,bundle)
        }
        // le tag nfc de l'Enigme 3
        binding.btnEnigma3.setOnClickListener {
            val bundle = bundleOf("enigmeTag" to "enigme3")
            //findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment,bundle)
        }
        // le tag nfc de l'Enigme4
        binding.btnEnigma4.setOnClickListener {
            val bundle = bundleOf("enigmeTag" to "enigme4")
           // findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment,bundle)
        }




       /* val onlineUri = Uri.parse("https://www.youtube.com/watch?v=Jd3nTm-wvyA&ab_channel=CodePalace")
        val offlineUri = Uri.parse("android.resource://"+requireActivity().packageName+"/"+R.raw.video_test)
       // binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(offlineUri)
        binding.videoView.requestFocus()
        binding.videoView.start()

        binding.imgReplay.setOnClickListener {
            binding.videoView.stopPlayback()
            binding.videoView.setVideoURI(offlineUri)
            binding.videoView.start()
        }*/

    }
    companion object {

        var sessionId = ""
    }

}