package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentGameBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.coroutines.launch
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import androidx.recyclerview.widget.LinearLayoutManager
import fr.mastergime.meghasli.escapegame.model.*

@AndroidEntryPoint
class GameFragment : Fragment(), NfcAdapter.ReaderCallback {

    val sessionViewModel: SessionViewModel by viewModels()
    var mNfcAdapter: NfcAdapter? = null
    private lateinit var binding: FragmentGameBinding

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

        disableStatusBar()
        sessionViewModel.updateSessionId()

        binding.quitButton.setOnClickListener {
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner) { value ->
            observeSessionState(value)
        }

        sessionViewModel.sessionId.observe(viewLifecycleOwner) {
            sessionId = it
        }

        createListEnigmaAdapter()
        createListCluesAdapter()
    }

    private fun observeSessionState(value: String?) {
        if (value == "Success"){
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
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
            UserForRecycler("Enigme One"),
            UserForRecycler("Enigme Two"),
            UserForRecycler("Enigme Three"),
            UserForRecycler("Enigme Four"),
        )
        val enigmaListAdapter = EnigmaListAdapter()
        enigmaListAdapter.submitList(enigmaList)
        binding.recyclerEnigma.apply {
            setHasFixedSize(true)
            adapter = enigmaListAdapter
            layoutManager = LinearLayoutManager(context)
            layoutManager = CenterZoomLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun createListCluesAdapter(){
        val clueList = mutableListOf(
            UserForRecycler("Clue One"),
            UserForRecycler("Clue Two"),
            UserForRecycler("Clue Three"),
            UserForRecycler("Clue Four"),
        )

        val cluesListAdapter = ClueListAdapter()
        cluesListAdapter.submitList(clueList)
        binding.recyclerViewClues.apply {
            setHasFixedSize(true)
            adapter = cluesListAdapter
            layoutManager = CenterZoomLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
    }

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

            when (msg) {
                "enigme1" -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val bundle = bundleOf("enigmeTag" to "enigme1")
                        findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment, bundle)
                    }
                }
                "enigme2" -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val bundle = bundleOf("enigmeTag" to "enigme2")
                        findNavController().navigate(R.id.action_gameFragment_to_enigme21Fragment, bundle)
                    }
                }
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

}