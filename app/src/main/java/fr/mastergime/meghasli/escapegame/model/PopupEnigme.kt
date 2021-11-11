package fr.mastergime.meghasli.escapegame.model

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import fr.mastergime.meghasli.escapegame.databinding.PopupBinding

class PopupEnigme(
    private val view: View,
    private val enigme: UserForRecycler
) : Dialog(view.context) {

    private var binding: PopupBinding = PopupBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //setContentView(R.layout.popup)
        setupCloseButton()
    }

    private fun setupCloseButton() {
        binding.exitPopup.setOnClickListener {
//            dismiss()
//        }

        }
    }

}