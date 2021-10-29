package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel

@AndroidEntryPoint
class RoomSessionFragment : Fragment() {

    private lateinit var binding : FragmentRoomSessionBinding
    private val sessionViewModel : SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        sessionViewModel.updateUsersList()
        sessionViewModel.updateSessionState()

        var usersList = mutableListOf(
            UserForRecycler("Adding Users ..."),
            UserForRecycler(""),
            UserForRecycler(""),
            UserForRecycler(""),
            UserForRecycler(""),
            UserForRecycler(""),
        )


        var usersListAdapter = UsersListAdapter()
        usersListAdapter.submitList(usersList)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = usersListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.button.setOnClickListener(){
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.launchSession()
        }

        binding.button2.setOnClickListener(){
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.quitSession()
        }

        sessionViewModel.userNameList.observe(viewLifecycleOwner){value ->
            if(value.isNotEmpty())
            usersListAdapter.submitList(value)
            else
                sessionViewModel.getUsersList()
        }

        sessionViewModel.launchSessionState.observe(viewLifecycleOwner){value ->
            if(value == "Success")
                Toast.makeText(activity,"Session launched successfully",
                    Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity,"Can't launch Session please retry",
                    Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.button.isEnabled = true
        }

        sessionViewModel.sessionState.observe(viewLifecycleOwner){value ->
            if(value == true){
                findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
            }else
                Toast.makeText(activity,"Can't launch Session please retry",
                    Toast.LENGTH_SHORT).show()
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner){value ->
            if(value == "Success")
                findNavController().navigate(R.id.action_sessionRoomFragment_to_menuFragment)
            else
                Toast.makeText(activity,"Can't leave Session please retry",
                    Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.button2.isEnabled = true
        }

    }


}