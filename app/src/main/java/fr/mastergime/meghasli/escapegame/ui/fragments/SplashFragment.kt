package fr.mastergime.meghasli.escapegame.ui.fragments


import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentSplashBinding
import kotlinx.coroutines.*


@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var _binding: FragmentSplashBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private val activityScope2 = CoroutineScope(Dispatchers.Main)
    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        val paint = _binding.titleEscapeGame.paint
        val with = paint.measureText(_binding.titleEscapeGame.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, with, _binding.titleEscapeGame.textSize, intArrayOf(
                Color.parseColor("#780206"),
                Color.parseColor("#000000"),
                Color.parseColor("#780206"),
                Color.parseColor("#000000")
            ), null, Shader.TileMode.REPEAT
        )
        _binding.titleEscapeGame.paint.shader = textShader

        (activity as AppCompatActivity).supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        lunchLogoAnimation()
    }

    private fun lunchLoadingAnimation() {
        activityScope2.launch {
            _binding.animationViewLoading.setAnimation("load_update.json")
            _binding.animationViewLoading.visibility = View.VISIBLE
            _binding.animationViewLoading.playAnimation()
            _binding.animationViewLoading.loop(true)
            delay(4000)
            activityScope2.cancel()
            _binding.animationViewLoading.visibility = View.GONE

            if (auth.currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_menuFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_logFragment)
            }
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

}