package fr.mastergime.meghasli.escapegame.backend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.model.Session
import fr.mastergime.meghasli.escapegame.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EnigmaSessionFirebase @Inject constructor(){

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var user: User
    lateinit var session : Session
    var message = ""

    suspend fun getEnigme(tagEnigme : String) : Enigme? {

        db = FirebaseFirestore.getInstance()
        var enigme : Enigme? = null

        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()
            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    val docRef = db.collection("Sessions").document(sessionId).collection("enigmes").document(tagEnigme)
                    docRef.get().addOnSuccessListener { documentSnapshot ->

                        val id = documentSnapshot.get("id") as Long
                        val name = documentSnapshot.get("name") as String
                        val reponse = documentSnapshot.get("reponse") as String
                        val state = documentSnapshot.get("state") as Boolean
                        enigme= Enigme(id.toInt(),name, reponse, state)

                        Log.d("enigme",enigme.toString())
                    }.await()
                }
            }
            Log.d("recuperation enigme : ","Successful")
        }catch (e:Exception){
            Log.d("recuperation enigme :","failed$e")
        }
        Log.d("enigme",enigme.toString())
        return enigme
    }

    suspend fun changeEnigmeStateToTrue(enigme : Enigme): Boolean {
        db = FirebaseFirestore.getInstance()
        enigme.state=true
        var stateChanged =false


        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()
            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    val docRef = db.collection("Sessions").document(sessionId).collection("enigmes").document(enigme.name)
                        .set(enigme)
                }
            }
            Log.d("update State : ","Successful")
            stateChanged = true
        }catch (e:Exception){
            Log.d("update State :","failed$e")
            stateChanged = false
        }
        return stateChanged
    }

    suspend fun getEnigmeState(enigmeTag : String) : Boolean {
        db = FirebaseFirestore.getInstance()
        var state = false
        try {

            val userQuery = db.collection("Users")
                .whereEqualTo("id", auth.currentUser!!.uid).get().await()
            if (userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    val refDoc = db.collection("Sessions").document(sessionId).collection("enigmes")
                        .document(enigmeTag)
                        .get().addOnSuccessListener { documentSnapshot ->
                            state = documentSnapshot.get("state") as Boolean
                            Log.d("stati", state.toString())
                        }.await()
                }
            }
        } catch (e: Exception) {


        }

        return state
    }

}