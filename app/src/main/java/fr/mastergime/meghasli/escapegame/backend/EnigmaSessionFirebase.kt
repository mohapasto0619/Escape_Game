package fr.mastergime.meghasli.escapegame.backend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var enigme : Enigme? = null

        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()
            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    val docRef = db.collection("Sessions").document(sessionId).collection("enigmes").document(tagEnigme)
                    docRef.get(Source.SERVER).addOnSuccessListener { documentSnapshot ->

                        val id = documentSnapshot.get("id") as Long
                        val name = documentSnapshot.get("name") as String
                        val reponse = documentSnapshot.get("reponse") as String
                        val state = documentSnapshot.get("state") as Boolean
                        val indice = documentSnapshot.get("indice") as String
                        enigme= Enigme(id.toInt(),name, reponse, state,indice)

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
        auth = FirebaseAuth.getInstance()
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
                        .get(Source.SERVER).addOnSuccessListener { documentSnapshot ->
                            state = documentSnapshot.get("state") as Boolean
                            Log.d("stati", state.toString())
                        }.await()
                }
            }
        } catch (e: Exception) {

        }
        return state
    }

    suspend fun setOptionalEnigmeState(type: Int) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var state = false
        var sessionId = ""
        val userQuery = db
            .collection("Users")
            .document(auth.currentUser!!.uid)

        userQuery.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    sessionId = document.data!!["sessionId"] as String
                } else {
                    Log.d("USER_EMPTY", "No such document")
                }
            }.await()

        val refDoc = db.collection("Sessions").document(sessionId).collection("Optional")
            .document("Optional")
        when(type){
            0 ->  refDoc.update(
                mapOf(
                    "state" to true,
                    "closed" to true,
                    "playedTime" to 1
                )
            ).addOnSuccessListener {
                Log.d("OPTIONAL_ENIGMA", "setOptionalEnigmeState: SET TO TRUE  ")
            }.await()
            1 ->  refDoc.update(
                mapOf(
                    "state" to false,
                    "closed" to true,
                    "playedTime" to 1
                )
            ).addOnSuccessListener {
                Log.d("OPTIONAL_ENIGMA", "setOptionalEnigmeState: SET TO TRUE  ")
            }.await()
        }

    }

    suspend fun getOptionalEnigmeState(): Boolean {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var state = false
        var sessionId = ""
        val userQuery = db
            .collection("Users")
            .document(auth.currentUser!!.uid)

        userQuery.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    sessionId = document.data!!["sessionId"] as String
                } else {
                    Log.d("USER_EMPTY", "No such document")
                }
            }.await()

        val refDoc = db.collection("Sessions").document(sessionId).collection("Optional")
            .document("Optional")

        refDoc.get(Source.SERVER)
            .addOnSuccessListener { documentSnapshot ->
                state = documentSnapshot.get("state") as Boolean
            }.await()

        return state
    }


    //Get Optional Clos/Open true ( to push )
    suspend fun getOptionalEnigmeOpenClos(): Boolean {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var closed = false
        var sessionId = ""
        val userQuery = db
            .collection("Users")
            .document(auth.currentUser!!.uid)

        userQuery.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    sessionId = document.data!!["sessionId"] as String
                } else {
                    Log.d("USER_EMPTY", "No such document")
                }
            }.await()

        val refDoc = db.collection("Sessions").document(sessionId).collection("Optional")
            .document("Optional")

        refDoc.get(Source.SERVER)
            .addOnSuccessListener { documentSnapshot ->
                closed = documentSnapshot.get("closed") as Boolean
            }.await()

        return closed
    }


}