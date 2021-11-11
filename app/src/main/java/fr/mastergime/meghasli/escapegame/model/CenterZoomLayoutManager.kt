package fr.mastergime.meghasli.escapegame.model

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView

class CenterZoomLayoutManager : LinearLayoutManager {
    private val mShrinkAmount = 0.15f
    private val mShrinkDistance = 0.9f

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context,
        orientation,
        reverseLayout) {
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        val orientation = orientation
        return if (orientation == VERTICAL) {
            scroll (dy,recycler,state)
        } else {
            0
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        val orientation = orientation
        return if (orientation == HORIZONTAL) {
           scroll (dx,recycler,state)
        } else {
            0
        }
    }


    fun scroll (d: Int, recycler: Recycler, state: RecyclerView.State): Int {

        val scrolled = super.scrollHorizontallyBy(d, recycler, state)
        val midpoint = width / 2f
        val d0 = 0f
        val d1 = mShrinkDistance * midpoint
        val s0 = 1f
        val s1 = 1f - mShrinkAmount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMidpoint = (getDecoratedRight(child!!) + getDecoratedLeft(
                child)) / 2f
            val d = Math.min(d1, Math.abs(midpoint - childMidpoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            child.scaleY = scale - 0.05f
        }
      return  scrolled

    }

}