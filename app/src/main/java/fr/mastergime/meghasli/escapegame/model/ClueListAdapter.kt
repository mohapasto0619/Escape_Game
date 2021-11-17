package fr.mastergime.meghasli.escapegame.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.mastergime.meghasli.escapegame.R
import kotlinx.android.synthetic.main.clue_item.view.*

class ClueListAdapter(

) : ListAdapter<Clue,
        ClueListAdapter.ClueListViewHolder>(Clue.DiffCallback()) {

    inner class ClueListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClueListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.clue_item, parent, false)
        return ClueListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClueListViewHolder, position: Int) {
        Log.d("zbii", "onBindViewHolder: $position")
        getItem(position).indice.also {
            holder.itemView.text_clue.text = it
        }
    }
}