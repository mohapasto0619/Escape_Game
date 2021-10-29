package fr.mastergime.meghasli.escapegame.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentSplashBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var _binding: FragmentSplashBinding
    private val sessionViewModel : SessionViewModel by viewModels()
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private val activityScope2 = CoroutineScope(Dispatchers.Main)
    private lateinit var auth: FirebaseAuth
    private var sessionId = "Empty"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        (activity as AppCompatActivity).supportActionBar?.hide()

        lunchLogoAnimation()
    }

    private fun lunchLoadingAnimation() {
        activityScope2.launch {
            _binding.animationViewLoading.setAnimation("loading.json")
            _binding.animationViewLoading.visibility = View.VISIBLE
            _binding.animationViewLoading.playAnimation()
            _binding.animationViewLoading.loop(true)
            delay(4000)
            check()
            activityScope2.cancel()
            _binding.animationViewLoading.visibility = View.GONE



            /*if(auth.currentUser!=null){
                findNavController().navigate(R.id.action_splashFragment_to_menuFragment)
            }else {
                findNavController().navigate(R.id.action_splashFragment_to_logFragment)
            }*/


        }
    }

    private fun lunchLogoAnimation() {
        activityScope.launch {
            val animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
            val animationTitle = AnimationUtils.loadAnimation(context, R.anim.zoom_in)

            _binding.animationViewLoader.startAnimation(animation)
            _binding.titleEscapeGame.startAnimation(animationTitle)

            delay(3500)
            activityScope.cancel()

            eraseLogoAnimation()
            lunchLoadingAnimation()
        }
    }

    private fun eraseLogoAnimation() {
        _binding.animationViewLoader.clearAnimation()
        _binding.titleEscapeGame.clearAnimation()

        _binding.titleEscapeGame.visibility = View.GONE
        _binding.animationViewLoader.visibility = View.GONE
    }

    private suspend fun check(){
        sessionId = sessionViewModel.getSessionIdFromUser()
        if(auth.currentUser!=null){
            if(sessionId == "null")
                findNavController().navigate(R.id.action_splashFragment_to_menuFragment)
            else if(sessionId == "Empty"){
                check()
                Log.d("recursive : ","here working !")
            }
            else
                findNavController().navigate(R.id.action_splashFragment_to_sessionRoomFragment)
        }

        else{
            findNavController().navigate(R.id.action_splashFragment_to_logFragment)
        }
    }

}