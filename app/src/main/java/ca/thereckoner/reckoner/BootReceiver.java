package ca.thereckoner.reckoner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import ca.thereckoner.reckoner.View.NotificationService;

/**
 * Created by Varun Venkataramanan on 2016-12-31.
 *
 * Receives intent on phone startup. Starts a looping alarm to deliver notifications and sends the intent.
 */

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver.class";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Notif set to 15 minute interval");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Alarm manager will perform repeated task
        Intent notifIntent = new Intent(context, NotificationService.class); //Notification Intent

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notifIntent, 0); //Run the intent
        am.cancel(pendingIntent);

        //Start the alarm manager for repeated interval
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }
}
