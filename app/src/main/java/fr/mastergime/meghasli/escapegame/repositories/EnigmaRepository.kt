package fr.mastergime.meghasli.escapegame.repositories

import fr.mastergime.meghasli.escapegame.backend.EnigmaSessionFirebase
import fr.mastergime.meghasli.escapegame.model.Enigme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnigmaRepository @Inject constructor(
    private val enigmaSessionFirebase: EnigmaSessionFirebase,
) {

    suspend fun getEnigme(enigmeTag: String): Enigme? {
        return enigmaSessionFirebase.getEnigme(enigmeTag)
    }

    suspend fun changeEnigmeStateToTrue(enigme: Enigme): Boolean {
        return enigmaSessionFirebase.changeEnigmeStateToTrue(enigme)
    }

    suspend fun getEnigmeState(enigmeTag: String): Boolean {
        return enigmaSessionFirebase.getEnigmeState(enigmeTag)
    }

    suspend fun getOptionalEnigmeState():Boolean{
        return enigmaSessionFirebase.getOptionalEnigmeState()
    }

    suspend fun getOptionalEnigmeOpenClos():Boolean{
        return enigmaSessionFirebase.getOptionalEnigmeOpenClos()
    }

    suspend fun setOptionalEnigmeState(type : Int){
        return enigmaSessionFirebase.setOptionalEnigmeState(type)
    }

    suspend fun getIndices(): MutableList<String>{
        return enigmaSessionFirebase.getIndices()
    }

    suspend fun getOptionalEnigme(): HashMap<String, Any?> {
        return enigmaSessionFirebase.getOptionalEnigme()
    }

}