package fr.mastergime.meghasli.escapegame.backend


import android.accounts.NetworkErrorException
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.model.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.net.ConnectException
import java.util.*
import java.util.logging.Handler
import javax.inject.Inject
import kotlin.math.log

class AuthServiceFirebase @Inject constructor() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var user: User
    var message = ""

    suspend fun signup(email: String, password: String, pseudo: String): String {

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = auth.currentUser!!.uid
                        user = User(email = email, pseudo = pseudo, id = id)
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

}



