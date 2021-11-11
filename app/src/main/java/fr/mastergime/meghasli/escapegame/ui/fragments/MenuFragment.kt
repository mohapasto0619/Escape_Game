package fr.mastergime.meghasli.escapegame.ui.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentMenuBinding
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity


@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var auth: FirebaseAuth
    var mNfcAdapter: NfcAdapter? = null
    private lateinit var  mediaPlayer : MediaPlayer
    private lateinit var binding : FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.txtTest.text= auth.currentUser!!.email
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)

        binding.imgLogout.setOnClickListener {
            auth.signOut()
            if (auth.currentUser!=null){
                Toast.makeText(activity,"Error Logout",Toast.LENGTH_SHORT).show()
            }else{
                findNavController().navigate(R.id.action_menuFragment_to_logFragment)
                Toast.makeText(activity,"Logout",Toast.LENGTH_SHORT).show()
            }
        }
        binding.txtCreatSeassion.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_creatSessionFragment)
        }
        binding.btnRejoindre.setOnClickListener {

            findNavController().navigate(R.id.action_menuFragment_to_joinSessionFragment)
        }

        mediaPlayer = MediaPlayer.create(context,R.raw.music)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

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

        val txtCreateAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.back_menu)
        binding.txtCreatSeassion.startAnimation(txtCreateAnimation)


        (activity as AppCompatActivity
                ).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater)
        return binding.root
    }

}