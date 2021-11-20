package fr.mastergime.meghasli.escapegame.model

import androidx.recyclerview.widget.DiffUtil

data class BluetoothDeviceContent(val mac :String, val name : String, val paired : Boolean) {
    class DiffCallback : DiffUtil . ItemCallback < BluetoothDeviceContent >() {
        override fun areItemsTheSame (oldItem : BluetoothDeviceContent, newItem : BluetoothDeviceContent ): Boolean {
            return oldItem.name == newItem . name
        }
        override fun areContentsTheSame (oldItem : BluetoothDeviceContent, newItem : BluetoothDeviceContent ) : Boolean {
            return oldItem.name == newItem . name && oldItem . mac ==
                    newItem.mac
        }
    }
}