package fr.mastergime.meghasli.escapegame.model

data class Session(var id : String,
                   val name : String,
                   var usersList : MutableList<String>,
                   var state : Boolean) {
}