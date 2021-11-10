package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentEnigme1Binding
import fr.mastergime.meghasli.escapegame.viewmodels.EnigmesViewModel

@AndroidEntryPoint
class Enigme1Fragment : Fragment() {


    private lateinit var binding : FragmentEnigme1Binding
    private val enigmeViewModel : EnigmesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnigme1Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var enigmeTag = arguments?.get("enigmeTag") as String

        enigmeViewModel.updateEnigmeState(GameFragment.sessionId,enigmeTag)

        enigmeViewModel.enigmeState.observe(viewLifecycleOwner, Observer {
            if (it){
                Log.d("tagTrue",it.toString())
                binding.csResolu.visibility=View.VISIBLE
                binding.csNonResolue.visibility=View.GONE
                Toast.makeText(activity,"Enigme deja resolue",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
            }else {
                Log.d("tagFalse",it.toString())
                binding.csResolu.visibility=View.GONE
                binding.csNonResolue.visibility=View.VISIBLE
            }
        })

        enigmeViewModel.getEnigme(enigmeTag).observe(viewLifecycleOwner, Observer {enigme ->
            if (enigme != null){

                binding.btnRepondre.setOnClickListener {

                    //test if user's response = enigme response
                    if (binding.edtReponse.text.toString()==enigme.reponse){
                        enigmeViewModel.changeEnigmeStateToTrue(enigme).observe(viewLifecycleOwner,
                            Observer { stateChanged ->

                                if (stateChanged){
                                    Toast.makeText(activity,"Enigme resolue",Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_enigme1Fragment_to_gameFragment)
                                }else {
                                    Toast.makeText(activity,"Enigme resolue",Toast.LENGTH_SHORT).show()
                                }
                            })
                    }else{
                        Toast.makeText(activity,"fausse reponse",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })
    }
}