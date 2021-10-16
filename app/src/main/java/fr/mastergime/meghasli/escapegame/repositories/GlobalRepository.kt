package fr.mastergime.meghasli.escapegame.repositories

import fr.mastergime.meghasli.escapegame.backend.AuthServiceFirebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepository @Inject constructor (private val authServiceFirebase : AuthServiceFirebase){


   suspend  fun signUp (email : String , password : String , pseudo :String): String {
       return    authServiceFirebase.signup(email, password,pseudo)
    }

     fun login (email : String , password : String)  {
         authServiceFirebase.login(email, password)
    }

    suspend fun createSession(name : String){
        return authServiceFirebase.createSession(name)
    }

    suspend fun  joinSession(name: String){
        return authServiceFirebase.joinSession(name)
    }

}