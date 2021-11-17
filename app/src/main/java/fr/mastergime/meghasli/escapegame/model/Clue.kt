package fr.mastergime.meghasli.escapegame.model

import androidx.recyclerview.widget.DiffUtil

data class Clue(val indice: String) {

    class DiffCallback : DiffUtil.ItemCallback<Clue>() {
        override fun areItemsTheSame(oldItem: Clue, newItem: Clue): Boolean {
            return oldItem.indice == newItem.indice
        }

        override fun areContentsTheSame(
            oldItem: Clue,
            newItem: Clue
        ): Boolean {
            return oldItem.indice == newItem.indice && oldItem.indice == newItem.indice
        }

    }

}
