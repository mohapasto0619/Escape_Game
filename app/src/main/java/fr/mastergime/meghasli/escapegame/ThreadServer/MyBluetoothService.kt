package fr.mastergime.meghasli.escapegame.ThreadServer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.Provides
import fr.mastergime.meghasli.escapegame.viewmodels.BluetoothViewModel
import fr.mastersid.azam.demonstrationblutescape.ThreadServer.ThreadServerService
import kotlinx.coroutines.delay
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread


private const val TAG = "MY_APP_DEBUG_TAG"


class MyBluetoothService(
    private val mBluetoothViewModel : BluetoothViewModel,
    private val mContext : Context
) {

    companion object{
        var aString = "sessiontoto"
        var RAN_UUID = UUID.nameUUIDFromBytes(aString.toByteArray()).toString()
        const val NAME = "BLUETOOTH"
        //const val RAN_UUID = "2c31da3c-6c87-4519-b537-a70891b0bf99"
        //generate unique UUID from a session.
    }
    // count a number of a connected device
    var numberDeviceConnected  =0

    private var MY_UUID: UUID = UUID.fromString(RAN_UUID)

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    //private var openmmSocket: BluetoothSocket? = null

    public var connectThread: ConnectThread? = null
    //lateinit var acceptThread: AcceptThread

    val listSocket = mutableListOf<BluetoothSocket>()

    /*fun accept(){
        //acceptThread?.cancel()
        acceptThread = AcceptThread()
        acceptThread?.start()
        Log.d("workManager","2. start")
    }*/

    /*private fun isConnectedToDevice(device: BluetoothDevice): Boolean{
        if(connectThread?.device?.address.equals(device.address) && openmmSocket != null){
            Log.d("client side",connectThread?.device?.address+"-"+device.address)
            return true
        }
        return false
    }*/

    private fun connect(device: BluetoothDevice){
        connectThread?.cancel()
        connectThread = ConnectThread(device)
        connectThread?.start()
    }

    /*
    private fun closeSocket() {
        try {
            openmmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
    */

    fun tryConnect(device : BluetoothDevice) {
        /*if(isConnectedToDevice(device)) {
            Log.d("ok","okok")
        }else {
            connect(device)
            thread(start = true) {
                while (!isConnectedToDevice(device)) {
                    Thread.sleep(100)
                }
            }
        }*/
        connect(device)
    }



    /*private fun manageMyConnectedSocketServer(socket: BluetoothSocket){
        listSocket.add(socket)
    }*/

    private fun manageMyConnectedSocketClient(socket: BluetoothSocket){
        //closeSocket()
        //openmmSocket = socket
        val connectedThread = ConnectedThread(socket)
        connectedThread.start()
    }

    /*fun sendToDevice(bytesToSend: ByteArray){
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
    }*/
    /*
    inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            Log.d("UUID_CLIENT", RAN_UUID)
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.d("Server_side", "Server listening")
                    //handler.showSentData("i'am waiting ...$numberDeviceConnected")
                    mBluetoothViewModel.setNotificationServer("i'am waiting ...$numberDeviceConnected")
                    numberDeviceConnected++
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }finally {
                    Log.d("Server_side", "Server have just accepted one device, go back to listen")
                }
                socket?.also {
                    // add socket to a list
                    manageMyConnectedSocketServer(it)
                    //mmServerSocket?.close()
                    //shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
*/
    inner class ConnectThread(device: BluetoothDevice) : Thread() {

        //val device: BluetoothDevice = device

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            Log.d("UUID_serveur", RAN_UUID)
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                try {
                    //Log.d(TAG,"trying to")
                    socket.connect()
                    Log.d(TAG,"Aucun serveur en écoute !!")
                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocketClient(socket)
                } catch (e: IOException) {
                    Log.e(TAG, "Could not connect to socket", e)
                    //handler.toastIfError("cannot connect to socket")
                    try{
                        val intent_su = Intent()
                        Log.d("ServiceStart_","EXCEPTION_SERVEUR_UNAVALAIBLE")
                        intent_su?.action ="EXCEPTION_SERVEUR_UNAVALAIBLE";
                        intent_su?.putExtra( "CODE_SERVEUR_UNAVALAIBLE","1");
                        mContext?.sendBroadcast(intent_su)
                    }catch(Ex : Exception){
                        //Log.e(TAG, "Could not connect to socket", e)
                    }

                }
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                //Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream

        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    //Log.d("Client_side","Client listening")
                    mmInStream.read(mmBuffer)
                    //Log.d("Client_side","Client have just received a message, go back to listen")
                } catch (e: IOException) {
                    //Log.d(TAG, "Input stream was disconnected", e)
                    break
                }
                // Send the obtained bytes to the UI activity.
                //handler.showSentData(mmBuffer.decodeToString(0, numBytes))
                //mBluetoothViewModel.setNotification(mmBuffer.decodeToString(0, numBytes))
                val intent1 = Intent()
                //Log.d("ServiceStart_","MESSAGE_FROM_SERVER")
                intent1?.action ="MESSAGE_FROM_SERVER";
                intent1?.putExtra( "MESSAGE_SERVEUR_TO_CLIENT",mmBuffer.decodeToString(0, numBytes)+"");
                mContext?.sendBroadcast(intent1)
            }
        }
        // Call this from the main activity to send data to the remote device.
        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                //Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}