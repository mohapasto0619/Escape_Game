package fr.mastergime.meghasli.escapegame.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.mastergime.meghasli.escapegame.backend.AuthServiceFirebase
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.model.MyHostApduService
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    suspend fun getPlayersState(): Boolean {
        return authServiceFirebase.getPlayersState()
    }

    suspend fun readyPlayer():String{
        return authServiceFirebase.readyPlayer()
    }

    suspend fun notReadyPlayer():String{
        return authServiceFirebase.notReadyPlayer()
    }



}