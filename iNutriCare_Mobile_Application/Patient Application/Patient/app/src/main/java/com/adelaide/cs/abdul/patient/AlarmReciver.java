package com.adelaide.cs.abdul.patient;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by abdul on 5/12/2016.
 */
public class AlarmReciver extends BroadcastReceiver {


    //private Vibrator vibrator;
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire(30000);
        //vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        try {

            String alarmType = intent.getStringExtra("alarm_type");
            Toast.makeText(context, "Hello I just received alarm of type ("+alarmType+")", Toast.LENGTH_LONG);
            Log.i("AlarmReceiver", "---=========> Hello I just received alarm of type ("+alarmType+")");
            // IF alarmType = 1 invoke the Background Service to set upcoming meals alarms
            if(alarmType.equals("1"))
            {
                Intent intentService = new Intent(context, SetMealsAlarmServiceP.class);
                intentService.putExtra("pid", intent.getStringExtra("pid"));
                Log.i("AlarmReceiver", "---=========> Starting the service ("+alarmType+")");
                context.startService(intentService);
            }
            else // IF alarmType = 2 Show Alarm to the elder :)
            {
                Intent alarmViewIntent = new Intent(context, AlarmView.class);
                alarmViewIntent.putExtra("mid", intent.getStringExtra("mid"));
                alarmViewIntent.putExtra("meal_type", intent.getStringExtra("meal_type"));
                alarmViewIntent.putExtra("meal_date_time", intent.getStringExtra("meal_date_time"));
                alarmViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarmViewIntent);
            }
        }
        catch(Exception e)
        {
            Log.i("AlarmReciver:", e.getMessage());
        }
    }
}
