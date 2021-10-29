package fr.mastergime.meghasli.escapegame.repositories

import fr.mastergime.meghasli.escapegame.backend.AuthServiceFirebase
import fr.mastergime.meghasli.escapegame.model.MyHostApduService
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepository @Inject constructor (private val authServiceFirebase : AuthServiceFirebase){


   suspend  fun signUp (email : String , password : String , pseudo :String): String {
       return    authServiceFirebase.signup(email, password,pseudo)
    }

     suspend fun login (email : String , password : String):String  {
         return authServiceFirebase.login(email, password)
    }

    suspend fun createSession(name : String):String{
        return authServiceFirebase.createSession(name)
    }

    suspend fun  joinSession(name: String):String{
        return authServiceFirebase.joinSession(name)
    }

    suspend fun quitSession():String{
        return authServiceFirebase.quitSession()
    }

    suspend fun getUsersList():MutableList<UserForRecycler>{
        return authServiceFirebase.getUsersList()
    }

    /*fun updateUsersList(){
        return authServiceFirebase.updateUsersList()
    }*/

    suspend fun launchSession():String{
        return authServiceFirebase.launchSession()
    }

    suspend fun getSessionState():Boolean{
        return authServiceFirebase.getSessionState()
    }



    suspend fun updateIdSession (value : String)  {
     MyHostApduService.SUCCESS = value
    }

    suspend fun getSessionName():String{
        return authServiceFirebase.getSessionName()
    }

    suspend fun getSessionIdFromUser():String{
        return authServiceFirebase.getSessionIdFromUser()
    }

}