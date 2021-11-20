package fr.mastergime.meghasli.escapegame.services

import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import fr.mastergime.meghasli.escapegame.Notifications.NOTIFICATION_START_UPDATE_ID
import fr.mastergime.meghasli.escapegame.Notifications.NotificationUtils
import fr.mastergime.meghasli.escapegame.Notifications.sendNotificationUpdateDone
import fr.mastergime.meghasli.escapegame.model.Session
import fr.mastergime.meghasli.escapegame.model.User
import fr.mastersid.azam.demonstrationblutescape.*

import fr.mastersid.azam.demonstrationblutescape.ThreadServer.ThreadServerService
import kotlinx.coroutines.*

import kotlinx.coroutines.tasks.await
import java.lang.Exception
import android.bluetooth.BluetoothAdapter

import android.R.string.no
import android.R.string.no
import fr.mastergime.meghasli.escapegame.R


class BluetoothService : Service(){

    lateinit var mThreadServerService : ThreadServerService
    lateinit var mAcceptThread: ThreadServerService.AcceptThread
    // solution pour ne pas afficher une notification dans le demarrage de serveur
    var incremen = 1
    /*****
     * partie firebase*****/
    lateinit var user: User
    lateinit var session : Session
    //var message = ""
    /*****
     * fin firebase*****/
    /**/
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent_service: Intent?, flags: Int, startId: Int): Int {
        Log.d("Server_side","service bien lancé$incremen")
        mThreadServerService = ThreadServerService(baseContext)
        startForeground(NOTIFICATION_START_UPDATE_ID,NotificationUtils.createForegroundInfo(applicationContext))
        mAcceptThread = mThreadServerService.AcceptThread()
        mAcceptThread.start()
        Log.d("Server_side","je lance thread serveur")

        // solution pour ne pas executer la notification pour la premiere fois

        //if(mAcceptThread.isAlive){}
        var session :String? = intent_service?.getStringExtra("Session")

        Log.d("Service_side","session name $session")

        val notificationManager = ContextCompat.getSystemService(
            baseContext,
            NotificationManager::class.java
        )

        try {
            /*****ecouter le changement sur le doc enigme 1*******/
            FirebaseFirestore.getInstance()
                .collection("Sessions").document(session!!).collection("enigmes").document("enigme1")
                .addSnapshotListener { dataFirebase, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val state = dataFirebase?.data?.getValue("state") as Boolean
                        if(state) {
                            mThreadServerService.sendToDevice(
                                "Escape Game : Enigme Numéro 1 resolue.".encodeToByteArray()
                            )
                            /*ne pas executer pour la premiere fois*/
                            notificationManager?.sendNotificationUpdateDone(
                                baseContext,
                                "Escape Game","Enigme Numéro 1 resolue."
                            )
                        }
                        Log.d("server_firebase","$state "+ "${state::class.simpleName}")
                    }
                }
            /*****ecouter le changement sur le doc enigme 2*******/
            FirebaseFirestore.getInstance()
                .collection("Sessions").document(session!!).collection("enigmes").document("enigme2")
                .addSnapshotListener { dataFirebase, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val state = dataFirebase?.data?.getValue("state") as Boolean
                        if(state) {
                            mThreadServerService.sendToDevice(
                                "Escape Game : Enigme Numéro 2 resolue.".encodeToByteArray()
                            )
                            /*ne pas executer pour la premiere fois*/

                            notificationManager?.sendNotificationUpdateDone(
                                baseContext,
                                "Escape Game", "Enigme Numéro 2 resolue."
                            )
                        }
                        Log.d("server_firebase","$state")
                    }
                }
            /*****ecouter le changement sur le doc enigme 3*******/
            FirebaseFirestore.getInstance()
                .collection("Sessions").document(session!!).collection("enigmes").document("enigme3")
                .addSnapshotListener { dataFirebase, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val state = dataFirebase?.data?.getValue("state") as Boolean
                        if(state) {
                            mThreadServerService.sendToDevice(
                                "Escape Game : Enigme Numéro 3 resolue.".encodeToByteArray()
                            )
                            /*ne pas executer pour la premiere fois*/

                            notificationManager?.sendNotificationUpdateDone(
                                baseContext,
                                "Escape Game", "Enigme Numéro 3 resolue."
                            )
                        }
                        Log.d("server_firebase","$state")
                    }
                }
            /*****ecouter le changement sur le doc enigme 4*******/
            FirebaseFirestore.getInstance()
                .collection("Sessions").document(session!!).collection("enigmes").document("enigme4")
                .addSnapshotListener { dataFirebase, err ->
                    GlobalScope.launch(Dispatchers.Main) {
                        val state = dataFirebase?.data?.getValue("state") as Boolean
                        if(state){
                            mThreadServerService.sendToDevice(
                                "Escape Game : Enigme Numéro 4 resolue."
                                    .encodeToByteArray()
                            )
                            /*ne pas executer pour la premiere fois*/

                            notificationManager?.sendNotificationUpdateDone(
                                baseContext,
                                "Escape Game","Enigme Numéro 4 resolue."
                            )
                        }
                        Log.d("server_firebase","$state")
                    }
                }
        } catch (e: NoSuchElementException) {
        }catch (e: Exception) {

        }


        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String = intent.action.toString()
                //Log.d("TAG_TEST", action)
                Log.d("ServiceStart_","avant when action")
                when(action) {
                    // la premiere connexion
                    "GET_SIGNAL_STRENGTH" -> {
                        val level = intent.getStringExtra("LEVEL_DATA")
                        Toast.makeText(baseContext, "$level", Toast.LENGTH_SHORT).show()
                        if(level != ""){
                            notificationManager?.sendNotificationUpdateDone(context,"Escape Game",level!!)
                        }
                    }
                    // quand le socket sera fermé ce code est executé
                    "ACTION_SNOOZE" ->
                    {
                        try{
                            mThreadServerService.sendToDevice(
                                    "Serveur Bluetooth fermé : serveur fermé .".encodeToByteArray()
                            )
                            // Closes the connect socket and causes the thread to finish.
                            mThreadServerService.cancel()
                            // le thread si il n'a plus de travail à faire il se ferme,
                            // il sera peut-être deja dans cette étape fermé
                            mAcceptThread.interrupt()
                            // arreter le service
                            stopService(intent_service)
                            // unregisterReceiver from receiver
                            unregisterReceiver(this)
                        }catch (ex : Exception){
                            // quand on quitte l'appli par bouton, notification foreground est tjrs présente, le clique dessus declenche cette exception
                            Toast.makeText(baseContext, "un probléme survenu", Toast.LENGTH_SHORT).show()
                            //arreter le service dans tous les cas ou le bouton de notification est appuyé
                            //stopService(intent_service)
                        }
                    }
                    // user fait une action sur le bluetooth (activer ou desactiver)
                    /****bluetooth receiver****/
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        Log.d("service_Bluetooth", "ACTION_CONNECTION_STATE_CHANGED1")
                        var state :Int = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        Log.d("service_Bluetooth", "ACTION_CONNECTION_STATE_CHANGED2")
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {
                                try{
                                    mThreadServerService.sendToDevice(
                                        "Serveur Bluetooth fermé : Bluetooth desactivé ".encodeToByteArray()
                                    )
                                    // Closes the connect socket and causes the thread to finish.
                                    mThreadServerService.cancel()
                                    // le thread si il n'a plus de travail à faire il se ferme,
                                    // il sera peut-être deja dans cette étape fermé
                                    mAcceptThread.interrupt()
                                    // arreter le service
                                    stopService(intent_service)
                                    // unregisterReceiver from receiver
                                    unregisterReceiver(this)
                                    Log.d("service_Bluetooth", "STATE_OFF")
                                }catch (ex : Exception){
                                }
                            }
                            BluetoothAdapter.STATE_TURNING_OFF -> {
                                try{
                                    mThreadServerService.sendToDevice(
                                        "Serveur Bluetooth fermé : Bluetooth desactivé ".encodeToByteArray()
                                    )
                                    Log.d("service_Bluetooth", "STATE_TURNING_OFF")
                                    // Closes the connect socket and causes the thread to finish.
                                    mThreadServerService.cancel()
                                    // le thread si il n'a plus de travail à faire il se ferme,
                                    // il sera peut-être deja dans cette étape fermé
                                    mAcceptThread.interrupt()
                                    // arreter le service
                                    stopService(intent_service)
                                    // unregisterReceiver from receiver
                                    unregisterReceiver(this)
                                }catch (ex : Exception){
                                }
                            }
                            BluetoothAdapter.STATE_ON -> {
                                Log.d("service_Bluetooth", "STATE_ON")
                            }
                            BluetoothAdapter.STATE_TURNING_ON -> {
                                Log.d("service_Bluetooth", "STATE_TURNING_ON")
                            }
                            //BluetoothAdapter.STATE_ON ->  Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                            //BluetoothAdapter.STATE_TURNING_ON -> Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON")
                        }
                    }
                    "GET_NullPointerException" ->
                    {
                        val level = intent.getStringExtra("LEVEL_DATA")
                        if(level=="1"){
                            try{
                                mThreadServerService.sendToDevice(
                                    "Serveur Bluetooth fermé : Un probléme est survenu ".encodeToByteArray()
                                )
                                Log.d("service_Bluetooth", "STATE_TURNING_OFF")
                                // Closes the connect socket and causes the thread to finish.
                                mThreadServerService.cancel()
                                // le thread si il n'a plus de travail à faire il se ferme,
                                // il sera peut-être deja dans cette étape fermé
                                mAcceptThread.interrupt()
                                // arreter le service
                                stopService(intent_service)
                                // unregisterReceiver from receiver
                                unregisterReceiver(this)
                            }catch (ex : Exception){
                            }
                        }
                    }
                }
            }
        }
        /****end bluetooth receiver****/
        try{
            // exception peut etre lever quand le unregisterReceiver() n'est pas appelé pour
                // le receiver on a ajouté baseContext?.unregisterReceiver(this)
                    // le service peut être appelé qu'une seule fois
            registerReceiver(receiver, IntentFilter("GET_SIGNAL_STRENGTH"))
            // utilisateur appuie sur fermé la notification/serveur
            registerReceiver(receiver, IntentFilter("ACTION_SNOOZE"))
            // NullPointerException est declenché par le serveur
            registerReceiver(receiver, IntentFilter("GET_NullPointerException"))
        }catch(ex : Exception){
            Toast.makeText(baseContext, baseContext?.getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
        /********ecouter le chanagement activer/desactiver bluetooth*********/
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        //
        super.onDestroy()
    }

}