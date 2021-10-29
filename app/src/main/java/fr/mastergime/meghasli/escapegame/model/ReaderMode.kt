package fr.mastergime.meghasli.escapegame.model

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.util.Log


class ReaderMode : NfcAdapter.ReaderCallback  {

    companion object {
        var message = ""
    }

    fun getNfcMessage():String{
        return message

    }

    override fun onTagDiscovered(tag: Tag?) {

        // Get the Tag as an NDEF tag Technology

        val isoDep: IsoDep? = IsoDep.get(tag)
        val mNdef: Ndef? = Ndef.get(tag)


        if (isoDep != null) {

            isoDep.connect()


            val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007D2760000850101"))



            message = "SUCCESS"

            Log.d("tagg","valeur =" + String(response))


            isoDep.close()

        } else {
            if (mNdef != null) {
                mNdef.connect()
                val mNdefMessage = mNdef.ndefMessage
                val msg = mNdefMessage.records[0].toUri().toString()



                /*     GlobalScope.launch(Dispatchers.Main) {
                         withContext(Dispatchers.Main){

                         }
                     }

                 */
                mNdef.close()
            } else{
                message = "FAILED"
            }
        }


    }



}