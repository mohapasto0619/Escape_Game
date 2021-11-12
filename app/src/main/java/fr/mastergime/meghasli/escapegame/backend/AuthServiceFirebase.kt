package fr.mastergime.meghasli.escapegame.backend


import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.model.Session
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceFirebase @Inject constructor() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var user: User
    lateinit var session : Session
    var message = ""

    var sessionName = ""

    suspend fun signup(email: String, password: String, pseudo: String): String {

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        message =""
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = auth.currentUser!!.uid
                        user = User(email = email, pseudo = pseudo, id = id, sessionId = "null")
                    } else {
                        message = task.exception!!.message.toString()
                    }
                }.await()

        } catch (e: FirebaseNetworkException) {
            message = "Network Error, Check Your Connectivity"
        } catch (e: FirebaseAuthException) {
            message = e.message.toString()
        }

        return if (message.isEmpty()) {
            registerUserInDatabase(user)
        } else {
            message
        }
    }


    suspend fun login(email: String, password: String):String {
        var state = ""
        auth = FirebaseAuth.getInstance()
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        state = "success"
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("xxx", "signInWithEmail:success")
                    }
                }.await()
            return state
        }catch (ea : FirebaseAuthException){
            state = "Failed : Invalid email or password"
            return state
        }catch (en : FirebaseNetworkException){
            state = "Failed : Network Error"
            return state
        }


    }

    private suspend fun registerUserInDatabase(user: User): String {
        db.collection("Users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                message = "Profile Created"
            }.addOnFailureListener { e ->
                message = e.message.toString()
            }.await()

        return message
    }





    //to get the users of the session
    suspend fun getUsersList():MutableList<UserForRecycler>{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userNameList = mutableListOf<UserForRecycler>()

        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()

            if(userQuery.documents.isNotEmpty()){
                for (document in userQuery){
                    val sessionId = document.getString("sessionId")
                    val sessionQuery = db.collection("Sessions")
                        .whereEqualTo("id",sessionId).get().await()

                    if (sessionQuery.documents.isNotEmpty()){

                        for (document2 in sessionQuery){
                            val usersList = document2.get("usersList") as ArrayList<*>

                            for(user in usersList){
                                val userDocument =  db.collection("Users")
                                    .document(user as String).get().addOnSuccessListener {userDocument ->
                                        val userName = userDocument.get("pseudo") as String
                                        val ready = userDocument.get("ready") as Boolean
                                        val userForRecycler = UserForRecycler(userName,ready)
                                        userNameList.add(userForRecycler)
                                        Log.d("Username12 :", "Operation Success !")
                                    }.await()
                            }
                        }
                    }

                }
                Log.d("getUsersList :","Successful")

            }
        }catch (e:Exception){
            Log.d("getUsersList :","Failed $e")
        }
        return userNameList
    }

    //to launch the game inside session room
    suspend fun launchSession():String{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val stateMap = mutableMapOf<String,Any>()
        stateMap["state"] = true
        var launchSessionState = "Unknown Error"
        if(getPlayersState()){
            try {
                val userQuery = db.collection("Users")
                    .whereEqualTo("id",auth.currentUser!!.uid).get().await()
                if(userQuery.documents.isNotEmpty()) {
                    for (document in userQuery) {
                        val sessionId = document.get("sessionId") as String
                        db.collection("Sessions").document(sessionId)
                            .set(stateMap, SetOptions.merge()).addOnSuccessListener {
                                launchSessionState = "Success"
                                Log.d("Launch Session : ","Succeed")
                            }.await()
                    }

                }
            }catch (e:Exception){
                launchSessionState = "Fatal Exception : $e"
            }
            Log.d("Launch Session : ","OK")
        }
        else{
            launchSessionState = "Waiting for other Players"
        }

        return launchSessionState
    }

    suspend fun getPlayersState():Boolean{
        val playersStateList = mutableListOf<Boolean>()
        var playersState = false
        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id", auth.currentUser!!.uid).get().await()

            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.getString("sessionId")
                    val sessionQuery = db.collection("Sessions")
                        .whereEqualTo("id", sessionId).get(Source.SERVER).await()

                    if (sessionQuery.documents.isNotEmpty()) {

                        for (document2 in sessionQuery) {
                            val usersList = document2.get("usersList") as ArrayList<*>

                            for (user in usersList) {
                                val userDocument =  db.collection("Users")
                                    .document(user as String).get(Source.SERVER).addOnSuccessListener { userDocument ->
                                        val ready = userDocument.get("ready") as Boolean
                                        playersStateList.add(ready)
                                    }.await()
                            }
                        }
                    }
                }
            }

        }catch (e:Exception){

        }
        if(playersStateList.isNotEmpty()){
            for (state in playersStateList){
                if(state == false){
                    playersState = false
                    break
                }else
                    playersState = true
            }
        }
        Log.d("playerState","$playersState")
        return playersState
    }

    suspend fun readyPlayer():String{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val stateMap = mutableMapOf<String,Any>()
        stateMap["ready"] = true
        var playerState = "Unknown Error"
        try {
            db.collection("Users").document(auth.currentUser!!.uid)
                .set(stateMap, SetOptions.merge()).addOnSuccessListener {
                    playerState = "Success"
                }.await()

            Log.d("Launch Session : ","Succeed")
        }catch (e:Exception){
            playerState = "Fatal Exception : $e"
        }
        return playerState
    }

    suspend fun notReadyPlayer():String{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val stateMap = mutableMapOf<String,Any>()
        stateMap["ready"] = false
        var playerState = "Unknown Error"
        try {
            db.collection("Users").document(auth.currentUser!!.uid)
                .set(stateMap, SetOptions.merge()).addOnSuccessListener {
                    playerState = "Success"
                }.await()

            Log.d("Launch Session : ","Succeed")
        }catch (e:Exception){
            playerState = "Fatal Exception : $e"
        }
        return playerState
    }
}



