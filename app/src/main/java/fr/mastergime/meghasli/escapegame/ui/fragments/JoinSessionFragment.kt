package fr.mastergime.meghasli.escapegame.ui.fragments

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentJoinSessionBinding
import fr.mastergime.meghasli.escapegame.model.ReaderMode
import fr.mastergime.meghasli.escapegame.model.Utils
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinSessionFragment : Fragment() ,NfcAdapter.ReaderCallback  {
    private lateinit var binding : FragmentJoinSessionBinding
    private var readerMode = ReaderMode ()
    var mNfcAdapter: NfcAdapter? = null
    private val sessionViewModel : SessionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinSessionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {

            Toast.makeText(context, "veuillez desactiver le nfc", Toast.LENGTH_SHORT).show()

        }

        binding.btnJoinSession.setOnClickListener(){
            if(binding.edtJoinSession.text.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                it.isEnabled = false
                sessionViewModel.joinSession(binding.edtJoinSession.text.toString())
            }
            else
                Toast.makeText(activity,"Please give a name for the Session you want to join",
                    Toast.LENGTH_SHORT).show()
        }

        sessionViewModel.joinSessionState.observe(viewLifecycleOwner){value ->
            if (value == "Success") {
                lifecycleScope.launch(Dispatchers.IO) {
                    sessionViewModel.updateIdSession(sessionViewModel.getSessionName())
                }
                findNavController().navigate(R.id
                    .action_joinSessionFragment_to_sessionRoomFragment)
            }
            else if(value == "UnknownSession")
                Toast.makeText(activity,"Can't find the session you looking for",
                    Toast.LENGTH_SHORT).show()
            else if(value == "FailedUserStep" || value == "FailedSessionStep")
                Toast.makeText(activity,"Can't join Session Please retry",
                    Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity,value,Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.btnJoinSession.isEnabled = true
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

        val isoDep: IsoDep? = IsoDep.get(tag)
        val mNdef: Ndef? = Ndef.get(tag)

        if (isoDep != null) {

            isoDep.connect()

            val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007D2760000850101"))


            sessionViewModel.joinSession(String(response))

            lifecycleScope.launch(Dispatchers.IO) {
                sessionViewModel.updateIdSession(sessionViewModel.getSessionName())
            }

            Log.d("tagg","valeurr :"+String(response))

            isoDep.close()
        }
    }
}