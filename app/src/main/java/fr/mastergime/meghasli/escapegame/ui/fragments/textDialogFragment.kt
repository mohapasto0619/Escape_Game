package fr.mastergime.meghasli.escapegame.ui.fragments

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import fr.mastergime.meghasli.escapegame.databinding.DialogTextBinding


class textDialogFragment : DialogFragment( ) {

    lateinit var binding : DialogTextBinding

    override fun onStart() {
        super.onStart()
        setPopupSize(95,80)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogTextBinding.inflate(layoutInflater)
        return  binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.text.movementMethod = ScrollingMovementMethod()


    }

    fun setPopupSize(width: Int, height : Int) {
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * width.toFloat() / 100
        val percentHeight = rect.height() * height.toFloat() / 100
        dialog?.window?.setLayout(percentWidth.toInt(), percentHeight.toInt())
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
