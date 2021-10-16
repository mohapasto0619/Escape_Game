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


    fun login(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(Activity()) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("xxx", "signInWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("xxx", "signInWithEmail:failure")
                }
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

    suspend fun createSession(name :String){
        val userList : MutableList<String> = mutableListOf()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        try{
            userList.add(auth.currentUser!!.uid)
            session = Session("null",name,userList,false)
            createSessionInDatabase(session)
            Log.d("Create Session 2 :", "Success")
        }catch (e : Exception){
            Log.d("Create Session 2 :","Failed $e")
        }
    }



    suspend fun createSessionInDatabase(session: Session){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

            db.collection("Sessions").add(session).addOnSuccessListener {docRef ->
                session.id = docRef.id
                val sessionIdMap = mutableMapOf<String,Any>()
                val userSessionIdMap = mutableMapOf<String,Any>()
                sessionIdMap["id"] = session.id
                userSessionIdMap["sessionId"] = session.id
                db.collection("Sessions").document(session.id).set(sessionIdMap,
                    SetOptions.merge()).addOnSuccessListener {
                        db.collection("Users").document(auth.
                        currentUser!!.uid).set(userSessionIdMap, SetOptions.merge())
                }
                Log.d("Session Creation : ", "succes")
            }.addOnFailureListener{
                Log.d("Session Creation : ", "failed")
            }.await()
    }

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
                    db.collection("Sessions")
                        .document(document.id).update(userListMap).addOnSuccessListener {
                            db.collection("Users").document(auth.
                            currentUser!!.uid).set(sessionIdMap, SetOptions.merge())
                            Log.d("Join session : ","Successfully join")
                        }.addOnFailureListener{
                            Log.d("Join session : ","Failed to join")
                        }.await()
                }
            }
            else{
                Log.d("Join Session 2 : ","No Session with this name avaible ")
            }
        }catch (e:Exception){
            Log.d("Join Session 3 : ","Failed $e")
        }

    }
}



