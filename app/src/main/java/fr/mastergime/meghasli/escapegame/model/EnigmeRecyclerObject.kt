package fr.mastergime.meghasli.escapegame.model

import androidx.recyclerview.widget.DiffUtil

data class EnigmeRecyclerObject(val name: String, val state : Boolean, val indice : String?) {

    class DiffCallback : DiffUtil.ItemCallback<EnigmeRecyclerObject>() {
        override fun areItemsTheSame(oldItem: EnigmeRecyclerObject, newItem: EnigmeRecyclerObject): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: EnigmeRecyclerObject,
            newItem: EnigmeRecyclerObject
        ): Boolean {
            return oldItem.name == newItem.name  && oldItem.state == newItem.state && oldItem.indice == newItem.indice
        }

    }
}