package fr.mastergime.meghasli.escapegame.viewModels
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.mastergime.meghasli.escapegame.model.Enigme
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EnigmesViewModel @Inject constructor(private val globalRepository: GlobalRepository) : ViewModel() {



    var enigmeState : MutableLiveData<Boolean> = MutableLiveData(false)
    var enigme = MutableLiveData<Enigme?>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    fun getEnigme(enigmeTag : String) : LiveData<Enigme?>{
        viewModelScope.launch(Dispatchers.IO) {
           enigme.postValue(globalRepository.getEnigme(enigmeTag))
            Log.d("vmEnigme",globalRepository.getEnigme(enigmeTag).toString())
        }

        return enigme
    }


    var stateChanged = MutableLiveData<Boolean>()

    fun changeEnigmeStateToTrue(enigme : Enigme) : LiveData<Boolean>{
        viewModelScope.launch(Dispatchers.IO) {
            stateChanged.postValue(globalRepository.changeEnigmeStateToTrue(enigme))
            //Log.d("vmEnigme",globalRepository.getEnigme(enigmeTag).toString())
        }
        return stateChanged
    }






     fun updateEnigmeState(sessionId : String, enigmeTag: String){

         /*viewModelScope.launch(Dispatchers.IO) {
             enigmeState.postValue(globalRepository.getEnigmeState())
         }*/
         FirebaseFirestore.getInstance()
             .collection("Sessions").document(sessionId).collection("enigmes").document(enigmeTag)
             .addSnapshotListener { _, _ ->
                 viewModelScope.launch(Dispatchers.IO) {
                     enigmeState.postValue(globalRepository.getEnigmeState(enigmeTag))
                 }
             }
         /*auth = FirebaseAuth.getInstance()
         db = FirebaseFirestore.getInstance()

         val userQuery = db.collection("Users")
             .whereEqualTo("id",auth.currentUser!!.uid).get().await()
         if(userQuery.documents.isNotEmpty()) {
             for (document in userQuery) {
                 val sessionId = document.get("sessionId") as String
                 db.collection("Sessions").document(sessionId).collection("enigmes").document("enigme2")
                     .addSnapshotListener { _, _ ->

                         sessionState.postValue(authServiceFirebase.getEnigmeState())

                     }
             }
         }*/

    }





}