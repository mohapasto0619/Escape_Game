package fr.mastergime.meghasli.escapegame.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.mastergime.meghasli.escapegame.R
import kotlinx.android.synthetic.main.user_item.view.*

class UsersListAdapter: ListAdapter<UserForRecycler,
        UsersListAdapter.UsersListViewHolder>(UserForRecycler.DiffCallback()) {

    inner class UsersListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UsersListViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersListViewHolder, position: Int) {
        holder.itemView.apply {
            name.text = getItem(position).name
        }
    }


}