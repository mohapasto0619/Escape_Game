package fr.mastergime.meghasli.escapegame.ui.fragments

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
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentGameBinding
import fr.mastergime.meghasli.escapegame.model.ReaderMode
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.model.Utils
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.coroutines.launch
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.ClueListAdapter
import fr.mastergime.meghasli.escapegame.model.EnigmaListAdapter
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder


@AndroidEntryPoint
class GameFragment : Fragment(), NfcAdapter.ReaderCallback {

    val sessionViewModel: SessionViewModel by viewModels()
    var mNfcAdapter: NfcAdapter? = null
    private lateinit var binding: FragmentGameBinding

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel.updateSessionId()

        binding.quitButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner) { value ->
            if (value == "Success"){
                sessionViewModel.notReadyPlayer()
                findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
            }

            else
                Toast.makeText(activity, "Can't leave Session please retry",
                    Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.quitButton.isEnabled = true
        }

        sessionViewModel.sessionId.observe(viewLifecycleOwner) {
            sessionId = it
        }

        var enigmaList = mutableListOf(
            UserForRecycler("Enigme One",false),
            UserForRecycler("Enigme Two",false),
            UserForRecycler("Enigme Three",false),
            UserForRecycler("Enigme Four",false)
        )

        var enigmaListAdapter = EnigmaListAdapter()
        enigmaListAdapter.submitList(enigmaList)
        binding.recyclerEnigma.apply {
            setHasFixedSize(true)
            adapter = enigmaListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        var clueList = mutableListOf(
            UserForRecycler("Clue One",false),
            UserForRecycler("Clue Two",false),
            UserForRecycler("Clue Three",false),
            UserForRecycler("Clue Four",false)
        )

        var cluesListAdapter = ClueListAdapter()
        cluesListAdapter.submitList(clueList)
        binding.recyclerViewClues.apply {
            setHasFixedSize(true)
            adapter = cluesListAdapter
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        mNfcAdapter!!.disableReaderMode(activity)
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


    companion object {

        var sessionId = ""
    }

}