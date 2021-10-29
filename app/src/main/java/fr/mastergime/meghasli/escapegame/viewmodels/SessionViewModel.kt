package fr.mastergime.meghasli.escapegame.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val globalRepository: GlobalRepository
    ) : ViewModel() {

        val userNameList : MutableLiveData<List<UserForRecycler>> = MutableLiveData()
        val createSessionState: MutableLiveData<String> = MutableLiveData()
        val joinSessionState: MutableLiveData<String> = MutableLiveData()
        val quitSessionState: MutableLiveData<String> = MutableLiveData()
        val launchSessionState : MutableLiveData<String> = MutableLiveData()
        val userSessionIdState : MutableLiveData<String> = MutableLiveData("Empty")
        val sessionState : MutableLiveData<Boolean> = MutableLiveData(false)


        fun createSession(name : String){
            viewModelScope.launch(Dispatchers.IO) {
                createSessionState.postValue(globalRepository.createSession(name))
            }
        }

        fun joinSession(name: String){
            viewModelScope.launch(Dispatchers.IO) {
                joinSessionState.postValue(globalRepository.joinSession(name))
            }
        }

        fun quitSession(){
            viewModelScope.launch(Dispatchers.IO) {
                quitSessionState.postValue(globalRepository.quitSession())
            }
        }

        fun getUsersList(){
            viewModelScope.launch(Dispatchers.IO) {
                userNameList.postValue(globalRepository.getUsersList())
            }
        }

        fun updateUsersList(){
            FirebaseFirestore.getInstance()
                .collection("Sessions").addSnapshotListener{ _, _ ->
                viewModelScope.launch (Dispatchers.IO){
                    userNameList.postValue(globalRepository.getUsersList())
                }
            }

        }

        fun launchSession(){
            viewModelScope.launch(Dispatchers.IO) {
                launchSessionState.postValue(globalRepository.launchSession())
            }
        }

        fun getSessionState(){
            viewModelScope.launch(Dispatchers.IO) {
                sessionState.postValue(globalRepository.getSessionState())
            }
        }

        //get the value from the methode directly (Should be used in coroutine)
        suspend fun getSessionState2():Boolean{
            return globalRepository.getSessionState()
        }

        fun updateSessionState(){
            FirebaseFirestore.getInstance()
                .collection("Sessions").addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    sessionState.postValue(globalRepository.getSessionState())
                }
            }
        }

        suspend fun getSessionIdFromUser(): String {
                return globalRepository.getSessionIdFromUser()

        }

}