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
import fr.mastergime.meghasli.escapegame.databinding.FragmentJoinSessionBinding
import fr.mastergime.meghasli.escapegame.viewModels.SessionViewModel


@AndroidEntryPoint
class JoinSessionFragment : Fragment() {
    private lateinit var binding: FragmentJoinSessionBinding
    private val sessionViewModel: SessionViewModel by viewModels()

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

        binding.btnJoinSession.setOnClickListener() {
            if (binding.edtJoinSession.editText!!.text.isNotEmpty()) {
                sessionViewModel.joinSession(binding.edtJoinSession.editText!!.text.toString())
                findNavController().navigate(R.id.action_joinSessionFragment_to_sessionRoomFragment)
            }
        }

        setTitleGradientColor()
        startAnimation()

    }

    private fun startAnimation() {
        val txtJoinAnimation: Animation =
            AnimationUtils.loadAnimation(context, R.anim.back_menu_2)
        binding.joinWithNfc.startAnimation(txtJoinAnimation)
    }

    private fun setTitleGradientColor() {
        val paint = binding.txtJoinSession.paint
        val with = paint.measureText(binding.txtJoinSession.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, with, binding.txtJoinSession.textSize, intArrayOf(
                Color.parseColor("#F80023"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#F80023"),
                Color.parseColor("#ffffff")
            ), null, Shader.TileMode.REPEAT
        )
        binding.txtJoinSession.paint.shader = textShader
    }
}