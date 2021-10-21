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
        val sessionState : MutableLiveData<Boolean> = MutableLiveData(false)


        fun createSession(name : String){
            viewModelScope.launch(Dispatchers.IO) {
                globalRepository.createSession(name)
            }
        }

        fun joinSession(name: String){
            viewModelScope.launch(Dispatchers.IO) {
                globalRepository.joinSession(name)
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
                globalRepository.launchSession()
            }
        }

        fun getSessionState(){
            viewModelScope.launch(Dispatchers.IO) {
                sessionState.postValue(globalRepository.getSessionState())
            }
        }

        fun updateSessionState(){
            FirebaseFirestore.getInstance()
                .collection("Sessions").addSnapshotListener { _, _ ->
                viewModelScope.launch(Dispatchers.IO) {
                    sessionState.postValue(globalRepository.getSessionState())
                }
            }
        }
}