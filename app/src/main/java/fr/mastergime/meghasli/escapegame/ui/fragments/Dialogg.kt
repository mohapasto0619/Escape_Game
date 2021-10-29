package fr.mastergime.meghasli.escapegame.ui.fragments
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import fr.mastergime.meghasli.escapegame.R

class Dialogg {


   fun dialogAlert (context : Context) {

        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.dialog_NFC)
            .setPositiveButton(R.string.settings,
                DialogInterface.OnClickListener { dialog, id ->
                    startActivity( context,Intent("android.settings.NFC_SETTINGS"),null);
                })
            .setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        builder.setCancelable(false)

        val alert = builder.create()
        alert.show()

    }



}


