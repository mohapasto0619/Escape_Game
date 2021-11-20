package fr.mastersid.azam.demonstrationblutescape.ThreadServer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import fr.mastergime.meghasli.escapegame.ThreadServer.MyBluetoothService

import java.io.IOException
import java.io.OutputStream
import java.util.*

private const val TAG = "MY_APP_DEBUG_TAG"

class ThreadServerService constructor(val mContext : Context){
    companion object{
        var aString = "sessiontoto"
        var RAN_UUID = UUID.nameUUIDFromBytes(aString.toByteArray()).toString()
        const val NAME = "BLUETOOTH"
        //const val RAN_UUID = "2c31da3c-6c87-4519-b537-a70891b0bf99"
        // count a number of a connected device
        var count = 0
        //val count1 = MutableLiveData<String>()
    }

    private var MY_UUID: UUID = UUID.fromString(RAN_UUID)

    val listSocket = mutableListOf<BluetoothSocket>()

    var mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        mBluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(MyBluetoothService.NAME, MY_UUID)
    }

    /*fun accept(){
        //acceptThread?.cancel()
        AcceptThread()
        start()
        Log.d("workManager","2. start")
    }*/

    inner class AcceptThread : Thread() {

        override fun run() {
            Log.d("UUID_CLIENT", MyBluetoothService.RAN_UUID)
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.d("Server_side", "Server listening")
                    count++
                    mmServerSocket?.accept()
                } catch (e: Exception) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }finally {
                    Log.d("Server_side", "Server have just accepted one device, go back to listen ,$count")
                }
                socket?.also {
                    // add socket to a list
                    manageMyConnectedSocketServer(it)
                    //mmServerSocket?.close()
                }
                try {
                    val intent1 = Intent()
                    Log.d("ServiceStart_","avant GET_SIGNAL_STRENGTH")
                    intent1?.action ="GET_SIGNAL_STRENGTH";
                    intent1?.putExtra( "LEVEL_DATA","un client N° $count vient de se connceter");
                    mContext?.sendBroadcast(intent1)
                    val mmOutStream: OutputStream = socket!!.outputStream
                    mmOutStream.write("Bien connecté au serveur".encodeToByteArray())
                } catch (e: IOException) {
                    // exception qu'on va pas avoir
                    Log.e(TAG, "Error occurred when sending data", e)
                } catch (e: NullPointerException) {
                    // Quand socket est null, ce
                        // pourra executer ce bloc quand le serveur ferme le service
                            // fermeture de service declenche la fermeture de l'objet puis
                                // l'arret de thread
                    val intent_exc = Intent()
                    intent_exc?.action ="GET_NullPointerException";
                    intent_exc?.putExtra( "LEVEL_DATA","1");
                } catch (e: Exception) {
                    // pourra se declencher dans les autres cas
                    Log.e(TAG, "NullPointerException", e)
                }
            }
        }
    }
    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            // je ferme les sockets
            mmServerSocket?.close()
            for(socket in listSocket){
                socket.close()
            }
        } catch (e: IOException) {
            // il pourra pas etre le cas, car on transfere pas les fichiers
            Log.e(TAG, "Could not close the connect socket", e)
        }catch (e: Exception) {
            Log.e(TAG, "toutes les exceptions", e)
        }
    }

    // Ajouter les sockets dans la liste.
    private fun manageMyConnectedSocketServer(socket: BluetoothSocket){
        listSocket.add(socket)
    }

    fun sendToDevice(bytesToSend: ByteArray){
        send(bytesToSend)
    }


    fun send(bytes: ByteArray) {
        listSocket.forEach { socket ->
            val mmOutStream: OutputStream = socket!!.outputStream
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
                //handler.toastIfError("Error occurred when sending data")
            }
        }
    }
}