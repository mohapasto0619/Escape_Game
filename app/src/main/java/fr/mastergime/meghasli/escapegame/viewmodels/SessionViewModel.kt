package fr.mastergime.meghasli.escapegame.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val globalRepository: GlobalRepository
    ) : ViewModel() {

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
}