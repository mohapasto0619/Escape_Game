package fr.mastergime.meghasli.escapegame.ui.fragments

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentCreatSessionBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel


@AndroidEntryPoint
class CreatSessionFragment : Fragment() {

    private lateinit var binding: FragmentCreatSessionBinding
    private val sessionViewModel: SessionViewModel by viewModels()


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

        binding.btnCreateSession.setOnClickListener() {
            if (binding.edtNomSession.editText!!.text.isNotEmpty()) {
                sessionViewModel.createSession(binding.edtNomSession.editText!!.text.toString())
                gettingInRoom()
            }
        }

        setTitleGradientColor()
        startAnimation()

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


}