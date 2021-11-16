package fr.mastergime.meghasli.escapegame.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import fr.mastergime.meghasli.escapegame.R
import kotlinx.android.synthetic.main.enigm_item.view.*

class EnigmaListAdapter(
    val itemClickListener: (position :Int) -> Unit
) : ListAdapter<EnigmeRecyclerObject,
        EnigmaListAdapter.UsersListViewHolder>(EnigmeRecyclerObject.DiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.enigm_item, parent, false)
        return UsersListViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersListViewHolder, position: Int) {
        val enigmeRecyclerObject = getItem(position)

        if (enigmeRecyclerObject.state){
            holder.txt_state.text  = "Resolved"
            holder.lotieAnimation.visibility =View.VISIBLE

            holder.txt_indice.visibility = View.VISIBLE
            holder.txt_indice.text  = enigmeRecyclerObject.indice
        } else{
            holder.txt_state.text  = "Unresolved"
            holder.lotieAnimation.visibility =View.INVISIBLE
            holder.txt_indice.visibility = View.INVISIBLE
        }



        holder.itemView.apply {
            text_view_enigma_num.text = getItem(position).name
        }

        holder.itemView.setOnClickListener{
            itemClickListener (position)
        }
    }
    inner class UsersListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txt_indice : TextView = itemView . findViewById (R.id.txt_indice )
        val txt_state : TextView = itemView . findViewById (R.id.txt_state )
        val lotieAnimation : LottieAnimationView = itemView . findViewById (R.id.lottieAnimationView )
    }
}


