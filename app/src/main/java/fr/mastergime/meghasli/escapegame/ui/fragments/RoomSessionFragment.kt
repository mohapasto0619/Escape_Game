package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import javax.sql.StatementEvent

@AndroidEntryPoint
class RoomSessionFragment : Fragment() {



    private lateinit var binding : FragmentRoomSessionBinding
    private val sessionViewModel : SessionViewModel by viewModels()

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

        sessionViewModel.updateUsersList()
        sessionViewModel.updateSessionState()
        //sessionViewModel.updateSessionId()





        binding.button.setOnClickListener(){
            //findNavController().navigate(R.id.action_sessionRoomFragment_to_enigme1Fragment)
            sessionViewModel.launchSession()
        }

        binding.button2.setOnClickListener(){
            sessionViewModel.getUsersList()
        }

        sessionViewModel.userNameList.observe(viewLifecycleOwner){value ->
            usersListAdapter.submitList(value)
        }

        sessionViewModel.sessionState.observe(viewLifecycleOwner){value ->
            if(value == true){

                findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
            }
        }
    }



}