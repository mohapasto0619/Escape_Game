package fr.mastergime.meghasli.escapegame.repositories

import fr.mastergime.meghasli.escapegame.backend.SessionServiceFirebase
import fr.mastergime.meghasli.escapegame.model.MyHostApduService
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionServiceFirebase: SessionServiceFirebase
) {

    suspend fun createSession(name: String): String {
        return sessionServiceFirebase.createSession(name)
    }

    suspend fun joinSession(name: String): String {
        return sessionServiceFirebase.joinSession(name)
    }

    suspend fun quitSession(): String {
        return sessionServiceFirebase.quitSession()
    }

    suspend fun getUsersList(): MutableList<UserForRecycler> {
        return sessionServiceFirebase.getUsersList()
    }

    suspend fun launchSession(): String {
        return sessionServiceFirebase.launchSession()
    }

    suspend fun getSessionState(): Boolean {
        return sessionServiceFirebase.getSessionState()
    }

    suspend fun getSessionId(): String {
        return sessionServiceFirebase.getSessionId()
    }

    suspend fun updateIdSession(value: String) {
        MyHostApduService.SUCCESS = value
    }

    suspend fun getSessionName(): String {
        return sessionServiceFirebase.getSessionName()
    }

    suspend fun getSessionIdFromUser(): String {
        return sessionServiceFirebase.getSessionIdFromUser()
    }

    suspend fun getPlayersState(): Boolean {
        return sessionServiceFirebase.getPlayersState()
    }

    suspend fun readyPlayer():String{
        return sessionServiceFirebase.readyPlayer()
    }

    suspend fun notReadyPlayer():String{
        return sessionServiceFirebase.notReadyPlayer()
    }

}