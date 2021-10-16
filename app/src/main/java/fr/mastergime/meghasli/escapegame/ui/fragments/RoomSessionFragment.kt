package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter


class RoomSessionFragment : Fragment() {

    private lateinit var binding : FragmentRoomSessionBinding

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
        var usersList = mutableListOf(
            UserForRecycler("Ted"),
            UserForRecycler("bob"),
            UserForRecycler("mike"),
            UserForRecycler("lucie")
        )

        var usersList2 = mutableListOf(
            UserForRecycler("Danny"),
            UserForRecycler("marley"),
            UserForRecycler("mike"),
        )



        var usersListAdapter = UsersListAdapter()
        usersListAdapter.submitList(usersList)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = usersListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.button.setOnClickListener(){
            usersListAdapter.submitList(usersList2)
        }


    }


}