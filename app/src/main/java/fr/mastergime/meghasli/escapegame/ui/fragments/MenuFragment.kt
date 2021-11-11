package fr.mastergime.meghasli.escapegame.ui.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentLogBinding
import fr.mastergime.meghasli.escapegame.databinding.FragmentMenuBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import fr.mastergime.meghasli.escapegame.model.Session
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import javax.inject.Inject


@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentMenuBinding
    var mNfcAdapter: NfcAdapter? = null

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.txtTest.text= auth.currentUser!!.email

        binding = FragmentMenuBinding.bind(view)

        disableStatusBar()
        animteColorTitle()
        animateTitle()
        startMusic()
        logOut()

        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)

        binding.txtCreatSeassion.setOnClickListener {
            cleatAnimations()
            findNavController().navigate(R.id.action_menuFragment_to_creatSessionFragment)
        }
        binding.btnRejoindre.setOnClickListener {
            cleatAnimations()
            findNavController().navigate(R.id.action_menuFragment_to_joinSessionFragment)
        }

    }

    private fun logOut() {
        binding.imgLogout.setOnClickListener {
            auth.signOut()
            if (auth.currentUser!=null){
                Toast.makeText(activity,"Error Logout",Toast.LENGTH_SHORT).show()
            }else{
                findNavController().navigate(R.id.action_menuFragment_to_logFragment)
                Toast.makeText(activity,"Logout",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animteColorTitle(){
        val paint = binding.txtMenu.paint
        val with = paint.measureText(binding.txtMenu.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, with, binding.txtMenu.textSize, intArrayOf(
                Color.parseColor("#F80023"),
                Color.parseColor("#F24D65"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#F24D65"),
                Color.parseColor("#F80023"),
                Color.parseColor("#ffffff")
            ), null, Shader.TileMode.REPEAT
        )
        binding.txtMenu.paint.shader = textShader
    }

    private fun animateTitle(){
        val txtCreateAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.back_menu)
        binding.txtCreatSeassion.startAnimation(txtCreateAnimation)
    }

    private fun startMusic(){
        mediaPlayerFactory.start()
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

    private fun cleatAnimations(){
        binding.txtCreatSeassion.clearAnimation()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerFactory.pause()
    }

    override fun onResume() {
        super.onResume()
        disableStatusBar()
        if(!mediaPlayerFactory.isPlaying){
            mediaPlayerFactory.start()
        }
    }



}