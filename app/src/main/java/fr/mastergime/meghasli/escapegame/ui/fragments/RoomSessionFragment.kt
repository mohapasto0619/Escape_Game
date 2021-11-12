package fr.mastergime.meghasli.escapegame.ui.fragments

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RoomSessionFragment : Fragment() {


    private lateinit var binding: FragmentRoomSessionBinding
    private val sessionViewModel: SessionViewModel by viewModels()

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* var sessionName = arguments?.get("sessionName")
         Log.d("sessionName", sessionName as String)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomSessionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableStatusBar()

        sessionViewModel.updateUsersList()
        sessionViewModel.launchSession()

        var usersList = mutableListOf(
            UserForRecycler("Adding Users ...",false),
            UserForRecycler("",false),
            UserForRecycler("",false),
            UserForRecycler("",false),
            UserForRecycler("",false),
            UserForRecycler("",false)
        )

        var usersListAdapter = UsersListAdapter()
        usersListAdapter.submitList(usersList)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = usersListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.button.setOnClickListener() {
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.readyPlayer()
        }

        binding.quitButton.setOnClickListener() {
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.userNameList.observe(viewLifecycleOwner) { value ->
            if (value.isNotEmpty())
                usersListAdapter.submitList(value)
            else
                sessionViewModel.getUsersList()
        }

        sessionViewModel.launchSessionState.observe(viewLifecycleOwner) { value ->
            observeLunchSessionState(value)
        }

        sessionViewModel.sessionState.observe(viewLifecycleOwner) { value ->
            observeSessionState(value)
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner) { value ->
            observeQuiteSessionState(value)
        }

        sessionViewModel.readyPlayerState.observe(viewLifecycleOwner){value ->
            if(value == "Success"){
                binding.progressBar.visibility = View.INVISIBLE
            }else{
                binding.button.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity,"Please retry can't make you ready",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun observeLunchSessionState(value: String?) {
        if(value == "Success" ){
            Toast.makeText(activity,"Launching Session...",
                Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.Default) {
                delay(10000)
                if(sessionViewModel.getSessionIdFromUser() != "null" &&
                    findNavController().currentDestination?.label == "fragment_session_room"){
                    withContext(Dispatchers.Main){
                        findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
                    }
                }
            }
        }
        else if (value == "Waiting for other Players")
            Toast.makeText(activity,"Waiting for others...",
                Toast.LENGTH_SHORT).show()

        else
            Toast.makeText(activity,"Can't launch Session please retry",
                Toast.LENGTH_SHORT).show()
    }

    private fun observeQuiteSessionState(value: String?) {
        lifecycleScope.launch(Dispatchers.Default) {
            if(value == "Success" ){
                sessionViewModel.notReadyPlayer()
                findNavController().navigate(R.id.action_sessionRoomFragment_to_menuFragment)
            }

            else
                Toast.makeText(activity,"Can't leave Session please retry",
                    Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.INVISIBLE
            binding.button2.isEnabled = true
    }
    }

    private fun observeSessionState(value: Boolean?) {
        if (value == true) {
            findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
        } else
            Toast.makeText(
                activity, "Can't launch Session please retry",
                Toast.LENGTH_SHORT
            ).show()
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
        disableStatusBar()
        if(!mediaPlayerFactory.isPlaying){
            mediaPlayerFactory.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayerFactory.isPlaying){
            mediaPlayerFactory.stop()
        }
    }

}