package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import fr.mastergime.meghasli.escapegame.model.*
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel

@AndroidEntryPoint
class GameFragment : Fragment(), NfcAdapter.ReaderCallback {

    val sessionViewModel: SessionViewModel by viewModels()
    private val enigmeViewModel: EnigmesViewModel by viewModels()
    var mNfcAdapter: NfcAdapter? = null
    private lateinit var binding: FragmentGameBinding

    var enigme1State = false
    var enigme2State = false
    var enigme3State = false
    var enigme4State = false

    private lateinit var  mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.intro_jeux)


        enigmeViewModel.updateEnigme1State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme2State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme3State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme4State(RoomSessionFragment.sessionId)
        //enigmeViewModel.updateEnigme1State(RoomSessionFragment.sessionId)

        disableStatusBar()
        //sessionViewModel.updateSessionId()

        binding.quitButton.setOnClickListener {
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner) { value ->
            observeSessionState(value)
        }

        /*sessionViewModel.sessionId.observe(viewLifecycleOwner) {
            sessionId = it
        }*/

        enigmeViewModel.enigme1State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme1State = true
                createListEnigmaAdapter()
            }
        })
        enigmeViewModel.enigme2State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme2State = true
                createListEnigmaAdapter()
            }
        })
        enigmeViewModel.enigme3State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme3State = true
                createListEnigmaAdapter()
            }
        })
        enigmeViewModel.enigme4State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme4State = true
                createListEnigmaAdapter()
            }
        })

        createListEnigmaAdapter()
        //createListCluesAdapter()
    }

    private fun observeSessionState(value: String?) {
        if (value == "Success"){
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            sessionViewModel.notReadyPlayer()
            findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
        }
        else
            Toast.makeText(activity, "Can't leave Session please retry",
                Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.INVISIBLE
        binding.quitButton.visibility = View.VISIBLE
        binding.quitButton.isEnabled = true
    }

    private fun createListEnigmaAdapter(){
        val enigmaList = mutableListOf(
            EnigmeRecyclerObject("Enigme Optionel",false,"indice opt"),
            EnigmeRecyclerObject("Enigme One",enigme1State,"indice1"),
            EnigmeRecyclerObject("Enigme Two: Part One",enigme2State,"indice2"),
            EnigmeRecyclerObject("Enigme Two: Part Two",enigme2State,"indice2"),
            EnigmeRecyclerObject("Enigme Three",enigme3State,"indice3"),
            EnigmeRecyclerObject("Enigme Final",enigme4State,"indice final")
        )
        val enigmaListAdapter = EnigmaListAdapter{

            Toast.makeText(activity,"item clicked $it",Toast.LENGTH_SHORT).show()
            when (it) {
                0 -> findNavController().navigate(R.id.action_gameFragment_to_optionel_enigme_fragment)
                1 -> findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment)
                2 -> findNavController().navigate(R.id.action_gameFragment_to_enigme21Fragment)
                3 -> findNavController().navigate(R.id.action_gameFragment_to_enigme22Fragment)
                4 -> findNavController().navigate(R.id.action_gameFragment_to_enigme3Fragment)
                5 -> findNavController().navigate(R.id.action_gameFragment_to_enigme4Fragment)
            }
        }
        enigmaListAdapter.submitList(enigmaList)
        binding.recyclerEnigma.apply {
            setHasFixedSize(true)
            adapter = enigmaListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /*private fun createListCluesAdapter(){
        val clueList = mutableListOf(
            UserForRecycler("Clue One",false,null),
            UserForRecycler("Clue Two",false,null),
            UserForRecycler("Clue Three",false,null),
            UserForRecycler("Clue Four",false,null)
        )

        val cluesListAdapter = ClueListAdapter()
        cluesListAdapter.submitList(clueList)
        binding.recyclerViewClues.apply {
            setHasFixedSize(true)
            adapter = cluesListAdapter
            layoutManager = CenterZoomLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
    }*/

    private fun enableNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {

            mNfcAdapter!!.enableReaderMode(
                this.activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A, Bundle.EMPTY
            )
        } else {
        }
    }

    override fun onTagDiscovered(tag: Tag?) {

        val mNdef: Ndef? = Ndef.get(tag)

        if (mNdef != null) {
            mNdef.connect()
            val mNdefMessage = mNdef.ndefMessage
            val msg = mNdefMessage.records[0].toUri().toString()

            // msg = msg du tag
            lifecycleScope.launch(Dispatchers.Main) {
                loadAnimationSignUpDone(msg)
            }

            mNdef.close()
        } else {
            ReaderMode.message = "FAILED"
        }
    }

    private fun disableStatusBar(){
        (activity as AppCompatActivity).supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }



    override fun onResume() {
        super.onResume()
        enableNfc()
        disableStatusBar()
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mNfcAdapter!!.disableReaderMode(activity)
        }

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }

    }

    companion object {
        var sessionId = ""
    }
    private fun loadAnimationSignUpDone(enigmeTag :String) {
        binding.animationViewLoading.setAnimation("done.json")
        binding.animationViewLoading.visibility = View.VISIBLE
        binding.animationViewLoading.playAnimation()
        binding.animationViewLoading.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.textViewTitleOfClues.visibility = View.INVISIBLE
                binding.textViewTitleOfEnigme.visibility = View.INVISIBLE
                binding.recyclerEnigma.visibility = View.INVISIBLE
                binding.recyclerViewClues.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {

                when (enigmeTag) {
                "enigmeXXXX" -> findNavController().navigate(R.id.action_gameFragment_to_optionel_enigme_fragment)
                "enigme1" -> findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment)
                    "enigme21"   -> findNavController().navigate(R.id.action_gameFragment_to_enigme21Fragment)
                    "enigme22" -> findNavController().navigate(R.id.action_gameFragment_to_enigme22Fragment)
                "enigme3" -> findNavController().navigate(R.id.action_gameFragment_to_enigme3Fragment)
                "enigme4" -> findNavController().navigate(R.id.action_gameFragment_to_enigme4Fragment)
            }

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

}