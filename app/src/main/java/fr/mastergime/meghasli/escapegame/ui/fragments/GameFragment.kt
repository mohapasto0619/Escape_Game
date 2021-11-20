package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
import android.graphics.Color
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.os.CountDownTimer
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
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentGameBinding
import androidx.recyclerview.widget.LinearLayoutManager
import fr.mastergime.meghasli.escapegame.model.*
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.*
import kotlin.math.log

@AndroidEntryPoint
class GameFragment : Fragment(), NfcAdapter.ReaderCallback {

    val sessionViewModel: SessionViewModel by viewModels()
    val enigmeViewModel: EnigmesViewModel by viewModels()

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }


    var mNfcAdapter: NfcAdapter? = null
    private lateinit var binding: FragmentGameBinding

    //remove
    var enigme1State = false
    var enigme2State = false
    var enigme3State = false
    var enigme4State = false
    var enigme5State = false

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.intro_jeux);
        mediaPlayer.setOnCompletionListener {
            Log.d("TAG_INTRO", "onViewCreated:  ")
            mediaStartedOnce = true
        }

        enigmeViewModel.updateEnigme1State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme2State(RoomSessionFragment.sessionId)
        //add enigme 2_2 update
        enigmeViewModel.updateEnigme3State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme4State(RoomSessionFragment.sessionId)
        enigmeViewModel.updateEnigme5State(RoomSessionFragment.sessionId)
        //enigmeViewModel.updateEnigme1State(RoomSessionFragment.sessionId)

        disableStatusBar()
        sessionViewModel.updateSessionId()
        sessionViewModel.starTimerSession()

        sessionViewModel.endTime.observe(viewLifecycleOwner) { value ->
            Log.d("valueTime", "mainTimer: $value ")
            mainTimer(value)
        }

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
                createListCluesAdapter()
            }
        })
        enigmeViewModel.enigme2State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme2State = true
                createListEnigmaAdapter()
                createListCluesAdapter()
            }
        })
        enigmeViewModel.enigme3State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme3State = true
                createListEnigmaAdapter()
                createListCluesAdapter()
            }
        })
        enigmeViewModel.enigme4State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme4State = true // remove
                createListEnigmaAdapter() //remove
            }
        })

        enigmeViewModel.enigme5State.observe(viewLifecycleOwner, Observer {
            if (it) {
                enigme5State = true // remove
                createListEnigmaAdapter() //remove
                win()
            }
        })

        createListEnigmaAdapter()
        createListCluesAdapter()
    }

    private fun mainTimer(endTime: Long) {
        val endTime = endTime
        val current = System.currentTimeMillis()
        Log.d("currentTime", "mainTimer: $current -> $endTime ")
        var stay = endTime - current
        Log.d("stayTime", "mainTimer: $stay ")

        object : CountDownTimer(stay, 1000) {
            override fun onTick(p0: Long) {
                stay = p0
                val minute = stay / 60000
                val second = stay % 60000 / 1000
                if (minute < 10) {
                    if (second < 10) {
                        "0$minute:0$second".also {
                            binding.textViewTime.text = it
                        }
                    } else {
                        "0$minute:$second".also {
                            binding.textViewTime.text = it
                        }
                    }
                } else if (second < 10) {
                    "$minute:0$second".also {
                        binding.textViewTime.text = it
                    }
                } else {
                    "$minute:$second".also {
                        binding.textViewTime.text = it
                    }
                }

                if (minute < 1) {
                    binding.textViewTime.setTextColor(Color.RED);
                }
            }

            override fun onFinish() {
                "Party is Over".also {
                    binding.textViewTime.text = it
                }
                lose()
            }
        }.start()

    }

    private fun observeSessionState(value: String?) {
        if (value == "Success") {
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            sessionViewModel.notReadyPlayer()
            findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
        } else
            Toast.makeText(
                activity, "Can't leave Session please retry",
                Toast.LENGTH_SHORT
            ).show()
        binding.progressBar.visibility = View.INVISIBLE
        binding.quitButton.visibility = View.VISIBLE
        binding.quitButton.isEnabled = true
    }

    private fun createListEnigmaAdapter() {
        lateinit var optionalEnigma: EnigmeRecyclerObject
        lateinit var enigmaList: MutableList<EnigmeRecyclerObject>
        ioScope.launch {
            val getOptionEnigme = enigmeViewModel.getOptionalEnigme()
            optionalEnigma = EnigmeRecyclerObject(
                getOptionEnigme["name"] as String,
                getOptionEnigme["state"] as Boolean,
                getOptionEnigme["indice"] as String
            )
            enigmaList = mutableListOf(
                optionalEnigma,
                EnigmeRecyclerObject("Death Chapter", enigme1State, "indice1"),
                EnigmeRecyclerObject("Crime Chapter P1", enigme2State, "indice2"),
                EnigmeRecyclerObject("Crime Chapter P2", enigme2State, "indice2"),
                EnigmeRecyclerObject("Live Chapter", enigme4State, "indice3"),
                EnigmeRecyclerObject("Enigme Final", enigme5State, "indice final")
            )

            val enigmaListAdapter = EnigmaListAdapter {
                when (it) {
                    0 -> ioScope.launch {
                        if (!enigmeViewModel.getOptionalEnigmeOpenClos())
                            findNavController().navigate(R.id.action_gameFragment_to_optionel_enigme_fragment)
                        else
                            Toast.makeText(
                                requireContext(),
                                "Enigma Already Done",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    1 -> {
                        loadAnimationSignUpDone("enigme1")
                    }
                    2 -> {
                        loadAnimationSignUpDone("enigme21")
                    }
                    3 -> {
                        loadAnimationSignUpDone("enigme22")
                    }
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
    }

    private fun createListCluesAdapter() {

        var clueList = mutableListOf<Clue>()

        ioScope.launch {
            enigmeViewModel.getIndices().map { indice ->
                Log.d("SHOW_INDICES", "createListCluesAdapter: $indice ")
                if (indice.isNotEmpty()) {
                    val clue = Clue(indice)
                    clueList.add(clue)
                }
                val cluesListAdapter = ClueListAdapter()
                cluesListAdapter.submitList(clueList)
                binding.recyclerViewClues.apply {
                    setHasFixedSize(true)
                    adapter = cluesListAdapter
                    layoutManager =
                        CenterZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            }


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

            // msg = msg du tag
            lifecycleScope.launch(Dispatchers.Main) {
                loadAnimationSignUpDone(msg)
            }

            mNdef.close()
        } else {
            ReaderMode.message = "FAILED"
        }
    }

    private fun disableStatusBar() {
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
        if (!mediaPlayer.isPlaying && !mediaStartedOnce) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mNfcAdapter!!.disableReaderMode(activity)
        }

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }

    }

    companion object {
        var sessionId = ""
        var mediaStartedOnce = false;
    }

    private fun loadAnimationSignUpDone(enigmeTag: String) {
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
                    "enigmeXXXX" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_optionel_enigme_fragment)
                        mediaStartedOnce = true
                    }
                    "enigme1" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_enigme1Fragment)
                        mediaStartedOnce = true
                    }
                    "enigme21" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_enigme21Fragment)
                        mediaStartedOnce = true
                    }
                    "enigme22" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_enigme22Fragment)
                        mediaStartedOnce = true
                    }
                    "enigme3" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_enigme3Fragment)
                        mediaStartedOnce = true
                    }
                    "enigme4" -> {
                        findNavController().navigate(R.id.action_gameFragment_to_enigme4Fragment)
                        mediaStartedOnce = true
                    }
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    fun win() {
        val mediaPlayerWine = MediaPlayer.create(requireContext(), R.raw.audio_win)

        Log.d("currentFrag", "lose: ${findNavController().currentDestination?.label} ")

        mediaPlayerWine.setOnCompletionListener {
            Log.d("TAG_WIN", "win: ")
            binding.animationViewWinLose.visibility = View.INVISIBLE
            sessionViewModel.quitSession()
            sessionViewModel.notReadyPlayer()
            if (findNavController().currentDestination?.label == "fragment_game") {
                viewModelStore.clear()
                findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            binding.animationViewWinLose.setAnimation("win.json")
            binding.animationViewWinLose.visibility = View.VISIBLE
            binding.animationViewWinLose.playAnimation()
            binding.animationViewWinLose.addAnimatorListener(object :
                Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    mediaPlayerWine.start()
                    binding.textViewTitleOfClues.visibility = View.INVISIBLE
                    binding.textViewTitleOfEnigme.visibility = View.INVISIBLE
                    binding.recyclerEnigma.visibility = View.INVISIBLE
                    binding.recyclerViewClues.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(p0: Animator?) {

                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }
            })
        }
        //Animation win
        //Navigate to pop up or fragment
    }

    fun lose() {
        //audio lose
        val mediaPlayerLose = MediaPlayer.create(requireContext(), R.raw.audio_lose)

        mediaPlayerLose.setOnCompletionListener {
            Log.d("_LOSE", "lose: ")
            binding.animationViewWinLose.visibility = View.INVISIBLE
            sessionViewModel.quitSession()
            sessionViewModel.notReadyPlayer()
            if (findNavController().currentDestination?.label == "fragment_game") {
                viewModelStore.clear()
                findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            binding.animationViewWinLose.setAnimation("lose.json")
            binding.animationViewWinLose.visibility = View.VISIBLE
            binding.animationViewWinLose.playAnimation()
            binding.animationViewWinLose.addAnimatorListener(object :
                Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    mediaPlayerLose.start()
                    binding.textViewTitleOfClues.visibility = View.INVISIBLE
                    binding.textViewTitleOfEnigme.visibility = View.INVISIBLE
                    binding.recyclerEnigma.visibility = View.INVISIBLE
                    binding.recyclerViewClues.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(p0: Animator?) {

                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }
            })
        }
    }
}