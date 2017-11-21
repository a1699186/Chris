package com.example.abdul.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by abdul on 5/12/2016.
 */
public class AlarmReciverGC extends BroadcastReceiver {


    //private Vibrator vibrator;
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire(30000);
        //vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        try {

            String alarmType = intent.getStringExtra("alarm_type");

            Log.i("AlarmReceiverCG", "---=========> Hello I just received alarm of type ("+alarmType+")");
            // IF alarmType = 1 invoke the Background Service to set upcoming meals alarms

            if(alarmType.equals("1"))
            {
                Context con = context;
                Intent intentService = new Intent(context, SetMealsAlarmService.class);
                intentService.putExtra("cid", "1");
                Log.i("AlarmReceiverCG", "---=========> Starting the service ("+alarmType+")");
                con.startService(intentService);
            }
            else // IF alarmType = 2 Show Alarm to the elder :)
            {
                Context con = context;
                Intent intentService = new Intent(context, GetAlertsService.class);
                intentService.putExtra("time", intent.getStringExtra("time"));
                Log.i("AlarmReceiverCG", "---=========> Starting the service ("+alarmType+")");
                context.startService(intentService);
            }
        }
        catch(Exception e)
        {
            Log.i("AlarmReceiverCG:", e.getMessage());
        }
    }
}
