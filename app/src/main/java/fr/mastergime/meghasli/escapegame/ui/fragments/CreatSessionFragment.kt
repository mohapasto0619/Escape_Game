package fr.mastergime.meghasli.escapegame.ui.fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreatSessionFragment : Fragment() {

    private lateinit var binding : FragmentCreatSessionBinding
    private val sessionViewModel : SessionViewModel by viewModels()

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer


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

        disableStatusBar()
        setTitleGradientColor()
        startAnimation()

        binding.constraintCreatSessionLayout.setOnClickListener {
            hideKeyBoard()
        }

        binding.btnCreateSession.setOnClickListener(){
            if(binding.edtNomSession.editText!!.text.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                it.isEnabled = false
                sessionViewModel.createSession(binding.edtNomSession.editText!!.text.toString())
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
                gettingInRoom()
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

    private fun setTitleGradientColor() {
        val paint = binding.txtCreatSeassion.paint
        val with = paint.measureText(binding.txtCreatSeassion.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, with, binding.txtCreatSeassion.textSize, intArrayOf(
                Color.parseColor("#780206"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#780206"),
                Color.parseColor("#ffffff")
            ), null, Shader.TileMode.REPEAT
        )
        binding.txtCreatSeassion.paint.shader = textShader
    }

    private fun startAnimation() {
        val txtCreateAnimation: Animation =
            AnimationUtils.loadAnimation(context, R.anim.back_menu_2)
        binding.imageView1.startAnimation(txtCreateAnimation)
        binding.imageView2.startAnimation(txtCreateAnimation)
        binding.imageView3.startAnimation(txtCreateAnimation)
        binding.imageView4.startAnimation(txtCreateAnimation)
        binding.btnCreateSession.startAnimation(txtCreateAnimation)
    }

    private fun gettingInRoom() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in_create)
        binding.constraintCreatSessionLayout.startAnimation(animation)

        binding.txtCreatSeassion.visibility = View.INVISIBLE
        binding.edtNomSession.visibility = View.INVISIBLE

        binding.imageView1.clearAnimation()
        binding.imageView2.clearAnimation()
        binding.imageView3.clearAnimation()
        binding.imageView4.clearAnimation()
        binding.btnCreateSession.clearAnimation()


        binding.imageView1.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.imageView3.visibility = View.INVISIBLE
        binding.imageView4.visibility = View.INVISIBLE
        binding.btnCreateSession.visibility = View.INVISIBLE


        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                findNavController().navigate(R.id.action_creatSessionFragment_to_sessionRoomFragment)
            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("Not yet implemented")
            }

        })
    }

    fun hideKeyBoard() {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        binding.edtNomSession.clearFocus()
    }

    private fun disableStatusBar(){
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

}