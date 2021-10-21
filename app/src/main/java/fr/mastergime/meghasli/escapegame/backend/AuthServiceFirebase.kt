package fr.mastergime.meghasli.escapegame.backend


import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    suspend fun registerUserInDatabase(user: User): String {

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

    //to create a new Session
    suspend fun createSession(name :String){
        val userList : MutableList<String> = mutableListOf()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        //try to create a Session
        try{
            userList.add(auth.currentUser!!.uid)
            session = Session("null",name,userList,false)
            createSessionInDatabase(session)
            Log.d("Create Session 2 :", "Success")
        } catch (e : Exception){
            Log.d("Create Session 2 :","Failed $e")
        }
    }


    //used inside createSession
    suspend fun createSessionInDatabase(session: Session) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val sessionIdMap = mutableMapOf<String, Any>()
        val userSessionIdMap = mutableMapOf<String, Any>()

        db.collection("Sessions").add(session).addOnSuccessListener { docRef ->
            session.id = docRef.id
            sessionIdMap["id"] = session.id
            userSessionIdMap["sessionId"] = session.id
        }.await()

        db.collection("Users").document(auth.
        currentUser!!.uid).set(userSessionIdMap, SetOptions.merge()).await()

        db.collection("Sessions").document(session.id).set(sessionIdMap,
            SetOptions.merge()).await()
    }

    //join session created by another player
    suspend fun joinSession(name : String){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userListMap = mutableMapOf<String,Any>()
        val sessionIdMap = mutableMapOf<String,Any>()

        try{
            userListMap["usersList"] = FieldValue.arrayUnion(auth.currentUser?.uid)
            val sessionQuery = db.collection("Sessions")
                .whereEqualTo("name",name).get().await()

            if(sessionQuery.documents.isNotEmpty()){
                for(document in sessionQuery){
                    sessionIdMap["sessionId"] = document.id
                    db.collection("Users").document(auth.
                    currentUser!!.uid).set(sessionIdMap, SetOptions.merge()).await()
                    db.collection("Sessions")
                        .document(document.id).update(userListMap).await()
                }
            }

            else{
                Log.d("Join Session 2 : ","No Session with this name avaible ")
            }

        }catch (e:Exception){
            Log.d("Join Session 3 : ","Failed $e")
        }

    }

    //to get the users of the session
    suspend fun getUsersList():MutableList<UserForRecycler>{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userQuery = db.collection("Users")
            .whereEqualTo("id",auth.currentUser!!.uid).get().await()
        val userNameList = mutableListOf<UserForRecycler>()

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
                                    val userForRecycler = UserForRecycler(userName)
                                    userNameList.add(userForRecycler)
                                    Log.d("Username12 :", "Operation Success !")
                                }.addOnFailureListener{
                                    Log.d("Username12 :", "Cannot get username document !")
                                }.await()
                        }
                    }
                    Log.d("getUsersList :","Session is not empty can get the list")
                }

            }
            Log.d("getUsersList :","Successful")
            return userNameList
        }
        else{
            Log.d("getUsersList","Failed")
            return userNameList //TODO if user list empty send toast to user
        // user list update failed try to refresh
        }
    }

    /*fun updateUsersList(){
        db.collection("Sessions").addSnapshotListener{ _, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                getUsersList()
            }
        }
    }*/

    //to launch the game inside session room
    suspend fun launchSession(){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val stateMap = mutableMapOf<String,Any>()
        stateMap["state"] = true
        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()
            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    db.collection("Sessions").document(sessionId)
                        .set(stateMap, SetOptions.merge()).addOnSuccessListener {
                            Log.d("Launch Session : ","Session Launched")
                        }.addOnFailureListener{
                            Log.d("Launch Session : ","Session Launching failed")
                        }
                }
                Log.d("Launch Session : ","Session found")
            }
        }catch (e:Exception){
            Log.d("Launch Session : ","failed $e")
        }

    }

    //to get the state of the session use this fun
    suspend fun getSessionState():Boolean{
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var sessionState = false
        try {
            val userQuery = db.collection("Users")
                .whereEqualTo("id",auth.currentUser!!.uid).get().await()
            if(userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    val sessionId = document.get("sessionId") as String
                    val sessionQuery = db.collection("Sessions")
                        .whereEqualTo("id",sessionId).get().await()
                    for (document2 in sessionQuery) {
                        sessionState = document2.get("state") as Boolean
                    }
                }
            }
            Log.d("get Session state : ","Successful")
            return sessionState
        }catch (e:Exception){
            Log.d("get Session State :","failed$e")
            return sessionState
        }
    }


}



