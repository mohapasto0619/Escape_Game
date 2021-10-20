package fr.mastergime.meghasli.escapegame.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val globalRepository: GlobalRepository
) : ViewModel() {

    var messageSignUp: MutableLiveData<String> = MutableLiveData()
    var stateLogin : MutableLiveData<String> = MutableLiveData()

    fun signUp(email: String, password: String, pseudo: String): MutableLiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {
            messageSignUp.postValue(globalRepository.signUp(email, password, pseudo))
        }
        return messageSignUp
    }

    fun login(email: String, password: String): MutableLiveData<String> {
       viewModelScope.launch(Dispatchers.IO) {
           stateLogin.postValue(globalRepository.login(email, password))
       }
        return stateLogin
    }


}


