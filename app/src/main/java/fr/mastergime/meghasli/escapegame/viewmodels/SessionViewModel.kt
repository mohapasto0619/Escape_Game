package fr.mastergime.meghasli.escapegame.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {


    val userNameList : MutableLiveData<List<UserForRecycler>> = MutableLiveData()
    val createSessionState: MutableLiveData<String> = MutableLiveData()
    val joinSessionState: MutableLiveData<String> = MutableLiveData()
    val quitSessionState: MutableLiveData<String> = MutableLiveData()
    val launchSessionState : MutableLiveData<String> = MutableLiveData()
    val userSessionIdState : MutableLiveData<String> = MutableLiveData("Empty")
    val readyPlayerState : MutableLiveData<String> = MutableLiveData()
    val sessionState : MutableLiveData<Boolean> = MutableLiveData(false)
    val btServerDeviceName : MutableLiveData<String> = MutableLiveData("null")
    val endTime: MutableLiveData<Long> = MutableLiveData()
    var sessionId = MutableLiveData<String>()


    fun createSession(name : String){
        viewModelScope.launch(Dispatchers.IO) {
            createSessionState.postValue(sessionRepository.createSession(name))
        }
    }

    fun joinSession(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            joinSessionState.postValue(sessionRepository.joinSession(name))
        }
    }

    fun quitSession(){
        viewModelScope.launch(Dispatchers.IO) {
            quitSessionState.postValue(sessionRepository.quitSession())
        }
    }

    fun getUsersList(){
        viewModelScope.launch(Dispatchers.IO) {
            userNameList.postValue(sessionRepository.getUsersList())
        }
    }

    fun updateUsersList(){
        FirebaseFirestore.getInstance()
            .collection("Sessions").addSnapshotListener{ _, _ ->
                viewModelScope.launch (Dispatchers.IO){
                    userNameList.postValue(sessionRepository.getUsersList())
                }
            }

        FirebaseFirestore.getInstance()
            .collection("Users").addSnapshotListener{ _, _ ->
                viewModelScope.launch (Dispatchers.IO){
                    userNameList.postValue(sessionRepository.getUsersList())
                }
            }
    }

    fun launchSession(){
        FirebaseFirestore.getInstance()
            .collection("Users").addSnapshotListener { _, firebaseException ->
                viewModelScope.launch(Dispatchers.IO) {
                    firebaseException?.let {
                        Log.d("UpdateSessionState : ","Failed firebaseException")
                    }
                    launchSessionState.postValue(sessionRepository.launchSession())
                }
            }
    }

    fun starTimerSession() {
        viewModelScope.launch(Dispatchers.IO) {
            endTime.postValue(sessionRepository.startTimer())
        }
    }

    fun getSessionState(){
        viewModelScope.launch(Dispatchers.IO) {
            sessionState.postValue(sessionRepository.getSessionState())
        }
    }

    //get the value from the methode directly (Should be used in coroutine)
    suspend fun getSessionState2():Boolean{
        return sessionRepository.getSessionState()
    }


    suspend fun updateIdSession (value : String)  {
        sessionRepository.updateIdSession(value)
    }

    suspend fun getSessionName():String{
        return sessionRepository.getSessionName()
    }

    suspend fun getSessionIdFromUser(): String {
        return sessionRepository.getSessionIdFromUser()

    }

    fun updateSessionId() {
        viewModelScope.launch(Dispatchers.IO) {
            sessionId.postValue(sessionRepository.getSessionId())
        }
    }

    fun getPlayerState(){
        viewModelScope.launch {
            sessionRepository.getPlayersState()
        }
    }

    fun readyPlayer(){
        viewModelScope.launch {
            readyPlayerState.postValue(sessionRepository.readyPlayer())
        }
    }

    fun notReadyPlayer(){
        viewModelScope.launch {
            readyPlayerState.postValue(sessionRepository.notReadyPlayer())
        }
    }

    fun readNameServerBluetoothOnFirebase(){
        viewModelScope.launch {
            btServerDeviceName.postValue(sessionRepository.readNameServerBluetoothOnFirebase())
        }
    }

    suspend fun readNameServerBluetoothOnFirebase2():String{
        return sessionRepository.readNameServerBluetoothOnFirebase()
    }


}