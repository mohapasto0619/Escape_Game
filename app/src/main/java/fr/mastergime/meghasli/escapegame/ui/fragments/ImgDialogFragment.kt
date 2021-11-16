package fr.mastergime.meghasli.escapegame.ui.fragments


import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.databinding.PopupImgBinding


class ImgDialogFragment : DialogFragment( ) {

    lateinit var binding : PopupImgBinding

    override fun onStart() {
        super.onStart()
        setPopupSize(95,60)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = PopupImgBinding.inflate(layoutInflater)
        return  binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var textt = arguments?.getString("ImageName")

        var constraintL : ConstraintLayout = binding.popup
        when (textt) {
            "scene_door" -> constraintL.setBackgroundResource(R.drawable.scene_door)
            "scene_victime" -> constraintL.setBackgroundResource(R.drawable.scene_victime)
            "murder_sketch" -> constraintL.setBackgroundResource(R.drawable.murder_sketch)
            "note1636216613307" -> constraintL.setBackgroundResource(R.drawable.note1636216613307)
            "tel_ind" -> constraintL.setBackgroundResource(R.drawable.tel_ind)
            "live__" -> constraintL.setBackgroundResource(R.drawable.live__)
        }
    }


    fun setPopupSize(width: Int, height : Int) {
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * width.toFloat() / 100
        val percentHeight = rect.height() * height.toFloat() / 100
        dialog?.window?.setLayout(percentWidth.toInt(), percentHeight.toInt())
    }
}
