package fr.mastergime.meghasli.escapegame.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import fr.mastergime.meghasli.escapegame.backend.AuthServiceFirebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepository @Inject constructor (val authServiceFirebase : AuthServiceFirebase){


    private val _requestState = MutableStateFlow(MessageResult.START)
    val requestState: Flow<MessageResult> get() = _requestState


   suspend  fun signUp (email : String , password : String , pseudo :String): String {
     //  _requestState.emit(MessageResult.START)
       return    authServiceFirebase.signup(email, password,pseudo)
    }

     fun login (email : String , password : String)  {
         authServiceFirebase.login(email, password)
    }


    enum class MessageResult{
        START,SUCCESS,FAILED
    }



}