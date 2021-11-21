package fr.mastergime.meghasli.escapegame.ui.fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.FragmentJoinSessionBinding
import fr.mastergime.meghasli.escapegame.model.Utils
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class JoinSessionFragment : Fragment(R.layout.fragment_join_session), NfcAdapter.ReaderCallback {

    private lateinit var binding: FragmentJoinSessionBinding
    var mNfcAdapter: NfcAdapter? = null
    private val sessionViewModel: SessionViewModel by viewModels()

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentJoinSessionBinding.bind(view)

        setTitleGradientColor()
        startAnimation()
        disableStatusBar()
        observeJoinSession()
        hideKeyBoard()
        joinSession()

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_joinSessionFragment_to_menuFragment)
        }
    }


    override fun onResume() {
        super.onResume()
        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mNfcAdapter!!.disableReaderMode(activity)
        }
    }

    private fun enableNfc() {

        mNfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {

            mNfcAdapter!!.enableReaderMode(
                this.activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A, Bundle.EMPTY
            )
        } else {
        }
    }

    override fun onTagDiscovered(tag: Tag?) {

        val isoDep: IsoDep? = IsoDep.get(tag)
        val mNdef: Ndef? = Ndef.get(tag)

        if (isoDep != null) {

            isoDep.connect()

            val response = isoDep.transceive(
                Utils.hexStringToByteArray(
                    "00A4040007D2760000850101"
                )
            )


            sessionViewModel.joinSession(String(response))

            lifecycleScope.launch(Dispatchers.IO) {
                sessionViewModel.updateIdSession(sessionViewModel.getSessionName())
            }

            Log.d("tagg", "valeurr :" + String(response))

            isoDep.close()
        }
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

    private fun hideKeyBoard() {
        binding.joinFragment.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.edtJoinSession.clearFocus()
        }
    }

    private fun observeJoinSession() {
        sessionViewModel.joinSessionState.observe(viewLifecycleOwner) { value ->
            if (value == "Success") {
                lifecycleScope.launch(Dispatchers.IO) {
                    sessionViewModel.updateIdSession(sessionViewModel.getSessionName())
                    entringRoomAnimation()
                }
            } else if (value == "UnknownSession")
                Toast.makeText(
                    activity, "Can't find the session you looking for",
                    Toast.LENGTH_SHORT
                ).show()
            else if (value == "FailedUserStep" || value == "FailedSessionStep")
                Toast.makeText(
                    activity, "Can't join Session Please retry",
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = View.INVISIBLE
            binding.btnJoinSession.isEnabled = true
        }
    }

    private fun entringRoomAnimation() {
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in_create)
        binding.joinFragment.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                binding.joinWithNfc.visibility = View.INVISIBLE
                binding.btnJoinSession.visibility = View.INVISIBLE
                binding.edtJoinSession.visibility = View.INVISIBLE
                binding.txtJoinSession.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(p0: Animation?) {
                Log.d("entring", "onAnimationEnd: ")
                findNavController().navigate(
                    R.id
                        .action_joinSessionFragment_to_sessionRoomFragment
                )
            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("Not yet implemented")
            }

        })
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

    private fun joinSession() {
        binding.btnJoinSession.setOnClickListener() {
            if (binding.edtJoinSession.editText!!.text.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                it.isEnabled = false
                hideKeyBoard()
                sessionViewModel.joinSession(binding.edtJoinSession.editText!!.text.toString())
            } else
                Toast.makeText(
                    activity, "Please give a name for the Session you want to join",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

}