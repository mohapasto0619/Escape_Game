package fr.mastergime.meghasli.escapegame.model

import com.google.firebase.Timestamp

data class Session(var id : String,
                   val name : String,
                   var usersList : MutableList<String>,
                   var state : Boolean,
                   //Updated to push
                   var timerStarted : Boolean
                    ) {
}