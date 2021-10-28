package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.databinding.FragmentJoinSessionBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel


@AndroidEntryPoint
class JoinSessionFragment : Fragment() {
    private lateinit var binding : FragmentJoinSessionBinding
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
            if (value == "Success")
                findNavController().navigate(R.id
                    .action_joinSessionFragment_to_sessionRoomFragment)
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

    }