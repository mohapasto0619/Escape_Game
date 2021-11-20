package fr.mastergime.meghasli.escapegame.viewmodels

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import fr.mastergime.meghasli.escapegame.model.BluetoothDeviceContent
import fr.mastergime.meghasli.escapegame.repositories.GlobalRepository
import fr.mastergime.meghasli.escapegame.services.BluetoothService

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton



class BluetoothViewModel : ViewModel() {

    val list : MutableLiveData<List<BluetoothDeviceContent>> = MutableLiveData(emptyList())

    // notifier le client si le thread envoie un message
    val notification = MutableLiveData<String>()
    
    /*
     * remplire la liste de device bluetooth nom et @ Mac
     */
    fun setListUnpairedDevices(ob : List<BluetoothDeviceContent>){
        list.value =  ob
    }

    /* server side :
    ** lancer le service de Thread server bluetooth, thread en état "running" */
    fun listenThenAccept(mContext : Context,  sessionId:String){
        var intent = Intent(mContext, BluetoothService::class.java)
        // faire passer la session
        intent.putExtra("Session", sessionId);
        mContext?.startService(intent)
    }
}