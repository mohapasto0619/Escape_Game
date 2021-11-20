package fr.mastergime.meghasli.escapegame.Notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

import fr.mastergime.meghasli.escapegame.R
import java.util.*


const val NOTIFICATION_UPDATE_DONE_ID = 1
const val NOTIFICATION_START_UPDATE_ID = 2

fun NotificationManager.sendNotificationUpdateDone(appContext: Context,titre :String, texte : String) {
    val notification = NotificationCompat.Builder(
        appContext,
        appContext.getString(R.string.notification_channel_id)
    ).setSmallIcon(R.drawable.ic_baseline_bluetooth_24)
    .setContentTitle(titre)
    .setContentText(texte)
    .build()
    notify(NOTIFICATION_UPDATE_DONE_ID,notification)
}

fun NotificationManager.createChannel(context:Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.d("NOTIFICATION","VERSION CODE")
        val notificationChannel = NotificationChannel(
            context.getString(R.string.notification_channel_id),
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply{
            setShowBadge(true)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            //description = context.getString(R.string.notification_channel_description)
        }
        createNotificationChannel(notificationChannel)
        Log.d("NOTIFICATION","createChannel")
    }
}

object NotificationUtils {
    fun createForegroundInfo (appContext : Context):Notification{
        val intent1 = Intent()
        Log.d("LEVEL_DATA","avant GET_SIGNAL_STRENGTH")
        intent1?.action ="ACTION_SNOOZE";
        intent1?.putExtra( "LEVEL_DATA","stop");

        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(appContext, 0, intent1, 0)

        return NotificationCompat
            .Builder(
                appContext,
                appContext.getString(R.string.notification_channel_id))
            .setContentTitle(appContext.getString(R.string.app_name)
            )
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_delete,
                appContext.getString(R.string.button_notification_foreground),
                snoozePendingIntent
            )
            .setContentText(appContext.getString (R.string.title_notification_foreground))
            .setSmallIcon(R.drawable.ic_baseline_bluetooth_24)
            .build()

        /**
         * TODO
         * Ajouter l'action et appeler  stopSelf() ou Context.stopService()
         * -------------------------
         * services can use their stopSelf(int) method to ensure the service is not stopped
         * until started intents have been processed.
         *      Intent myIntent = new Intent(MainActivity.this, PlaySongService.class);
                this.stopService(myIntent);
        see : https://devstory.net/10421/android-service
        add action to stop service :
        https://developer.android.com/training/notify-user/build-notification#Actions
         */
    }

}