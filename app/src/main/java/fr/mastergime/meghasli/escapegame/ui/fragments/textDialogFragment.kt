package fr.mastergime.meghasli.escapegame.ui.fragments

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import fr.mastergime.meghasli.escapegame.R
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

        binding.textdialog.movementMethod = ScrollingMovementMethod()

        var textt = arguments?.getString("TextName")
        when (textt) {
            "Enigme1" -> binding.textdialog.text = getString(R.string.text_enigme1)
            "Enigme21" -> binding.textdialog.text = getString(R.string.text_enigme21)
            "Enigme22" -> binding.textdialog.text = getString(R.string.text_enigme22)
            "Enigme3" -> binding.textdialog.text = getString(R.string.text_enigme3)

        }

        /*******

        val textStory: TextView = findViewById(R.id.readStory) as TextView
        textStory.setOnClickListener {
            textStory.text = getString(R.string.name)
        }

        */
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
