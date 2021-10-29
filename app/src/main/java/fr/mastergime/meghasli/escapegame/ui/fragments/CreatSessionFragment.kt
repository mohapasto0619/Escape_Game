package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreatSessionFragment : Fragment() {

    private lateinit var binding : FragmentCreatSessionBinding
    private val sessionViewModel : SessionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatSessionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateSession.setOnClickListener(){
            if(binding.edtNomSession.text.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                it.isEnabled = false
                sessionViewModel.createSession(binding.edtNomSession.text.toString())
                Log.d("Button create : ","click")

            }
            else
                Toast.makeText(activity,"Please give a name for the Session",
                    Toast.LENGTH_SHORT).show()
        }

        sessionViewModel.createSessionState.observe(viewLifecycleOwner){value ->
            if(value == "Success") {

                lifecycleScope.launch(Dispatchers.IO) {
                    sessionViewModel.updateIdSession(sessionViewModel.getSessionName())
                }
                findNavController().navigate(R.id
                    .action_creatSessionFragment_to_sessionRoomFragment)

            }
            else if(value == "FailedCreateSession" || value == "FailedUserStep"
                || value == "FailedSessionStep" )
                Toast.makeText(activity,"Can't create Session Please retry",
                    Toast.LENGTH_SHORT).show()

            else
                Toast.makeText(activity,value,Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.btnCreateSession.isEnabled = true
        }
    }

}