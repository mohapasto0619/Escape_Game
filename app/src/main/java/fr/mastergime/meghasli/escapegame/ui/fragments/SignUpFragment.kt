package fr.mastergime.meghasli.escapegame.ui.fragments

import android.animation.Animator
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentSignUpBinding
import fr.mastergime.meghasli.escapegame.viewmodels.AuthViewModel


@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        disableStatusBar()
        backCallBack()
        observeMessageSignUp()
        hideKeyBoard()
        register()
    }

    private fun observeMessageSignUp() {
        authViewModel.messageSignUp.observe(viewLifecycleOwner,
            Observer {
                if (it == "Profile Created") {
                    loadAnimationSignUpDone()
                } else {
                    Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                    binding.progressBar2.visibility = View.INVISIBLE
                }
            })

    }

    private fun register(){
        binding.registerButton.setOnClickListener {
            hideKeyBoard()
            val email = binding.emailTextInput.editText?.text.toString()
            val password = binding.passwordTextInput.editText?.text.toString()
            val pseudo = binding.pseudoTextInput.editText?.text.toString()

            if (test(email)) {
                binding.progressBar2.visibility = View.VISIBLE
                authViewModel.signUp(email, password, pseudo)
            }
        }
    }

    private fun loadAnimationSignUpDone() {
        binding.animationViewLoading.setAnimation("done.json")
        binding.animationViewLoading.visibility = View.VISIBLE
        binding.animationViewLoading.playAnimation()
        binding.animationViewLoading.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.progressBar2.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                auth.signOut()
                findNavController().navigate(R.id.action_signUpFragment_to_logFragment)
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun test(email: String): Boolean {
        if (binding.emailTextInput.editText?.text.toString().isEmpty()) {
            binding.emailTextInput.error = "enter email"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInput.error = "enter a valid email"
            return false
        }

        if (binding.passwordTextInput.editText?.text.toString().length < 6) {
            binding.emailTextInput.error = null
            binding.passwordTextInput.error = "password should have at least 6 characters"
            return false
        }
        return true
    }

    private fun backCallBack() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_signUpFragment_to_logFragment)
                }
            })

    }

    fun hideKeyBoard() {
        binding.signUpRagment.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.emailTextInput.clearFocus()
            binding.passwordTextInput.clearFocus()
            binding.pseudoTextInput.clearFocus()
        }
    }

    private fun disableStatusBar() {
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
    }

}