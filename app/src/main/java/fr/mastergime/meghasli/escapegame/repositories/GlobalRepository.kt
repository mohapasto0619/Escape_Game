package fr.mastergime.meghasli.escapegame.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.mastergime.meghasli.escapegame.backend.AuthServiceFirebase
import fr.mastergime.meghasli.escapegame.backend.EnigmaSessionFirebase
import fr.mastergime.meghasli.escapegame.backend.SessionServiceFirebase
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.model.MyHostApduService
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepository @Inject constructor(
    private val authServiceFirebase: AuthServiceFirebase,
) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    suspend fun signUp(email: String, password: String, pseudo: String): String {
        return authServiceFirebase.signup(email, password, pseudo)
    }

    suspend fun login(email: String, password: String): String {
        return authServiceFirebase.login(email, password)
    }

    /*fun updateUsersList(){
        return authServiceFirebase.updateUsersList()
    }*/

}