package fr.mastergime.meghasli.escapegame.model
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Base64
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import java.lang.StringIndexOutOfBoundsException as StringIndexOutOfBoundsException1

@AndroidEntryPoint
class MyHostApduService  @Inject constructor() : HostApduService () {

    companion object {

        var SUCCESS: String = "0000"
        val AID = "D2760000850101"
        lateinit var ent : ByteArray
    }

    override fun processCommandApdu(apdu: ByteArray?, extras: Bundle?): ByteArray? {

        if (apdu != null) {
            val hexCommandApdu = Utils.toHex(apdu)
            try {
                if (hexCommandApdu.substring(10, 24) == AID) {
                    ent = SUCCESS.toByteArray()
                }
            } catch (e : StringIndexOutOfBoundsException1){

            }
        } else {
            null
        }
        return ent
    }

    override fun onDeactivated(p0: Int) {

    }
}
