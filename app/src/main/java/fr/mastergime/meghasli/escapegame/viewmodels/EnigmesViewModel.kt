package fr.mastergime.meghasli.escapegame.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.repositories.EnigmaRepository
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnigmesViewModel @Inject constructor(
    private val enigmaRepository: EnigmaRepository
) : ViewModel() {


    var enigmeState: MutableLiveData<Boolean> = MutableLiveData(false)

    var enigme1State: MutableLiveData<Boolean> = MutableLiveData(false)
    var enigme2State: MutableLiveData<Boolean> = MutableLiveData(false)
    var enigme3State: MutableLiveData<Boolean> = MutableLiveData(false)
    var enigme4State: MutableLiveData<Boolean> = MutableLiveData(false)
    var enigmeOptionalState: MutableLiveData<Boolean> = MutableLiveData(false)

    var enigme = MutableLiveData<Enigme?>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    fun getEnigme(enigmeTag: String): LiveData<Enigme?> {
        viewModelScope.launch(Dispatchers.IO) {
            enigme.postValue(enigmaRepository.getEnigme(enigmeTag))
            Log.d("vmEnigme", enigmaRepository.getEnigme(enigmeTag).toString())
        }

        return enigme
    }


    var stateChanged = MutableLiveData<Boolean>()

    fun changeEnigmeStateToTrue(enigme: Enigme): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            stateChanged.postValue(enigmaRepository.changeEnigmeStateToTrue(enigme))
            //Log.d("vmEnigme",globalRepository.getEnigme(enigmeTag).toString())
        }
        return stateChanged
    }


    //fonction utilisée dans les fragment des enigmes
    fun updateEnigmeState(sessionId: String, enigmeTag: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document(enigmeTag)
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigmeState.postValue(enigmaRepository.getEnigmeState(enigmeTag))
                }
            }
    }
    fun updateEnigme1State(sessionId: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document("enigme1")
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigme1State.postValue(enigmaRepository.getEnigmeState("enigme1"))
                }
            }
    }
    fun updateEnigme2State(sessionId: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document("enigme2")
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigme2State.postValue(enigmaRepository.getEnigmeState("enigme2"))
                }
            }
    }
    fun updateEnigme3State(sessionId: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document("enigme3")
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigme3State.postValue(enigmaRepository.getEnigmeState("enigme3"))
                }
            }
    }
    fun updateEnigme4State(sessionId: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document("enigme4")
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigme4State.postValue(enigmaRepository.getEnigmeState("enigme4"))
                }
            }
    }
    /*fun updateEnigmeOptionalState(sessionId: String) {
        FirebaseFirestore.getInstance()
            .collection("Sessions").document(sessionId).collection("enigmes").document(enigmeTag)
            .addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    enigmeOptionalState.postValue(enigmaRepository.getEnigmeState(enigmeTag))
                }
            }
    }*/


}