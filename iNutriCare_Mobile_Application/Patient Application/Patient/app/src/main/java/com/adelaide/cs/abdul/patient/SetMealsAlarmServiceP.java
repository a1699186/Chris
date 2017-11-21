package com.adelaide.cs.abdul.patient;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;


public class SetMealsAlarmServiceP extends IntentService {

    final String [] response = new String[1];

    public SetMealsAlarmServiceP()
    {
        super("SetMealsAlarmServiceP");
    }

    protected void onHandleIntent(Intent intent) {
        // Let it continue running until it is stopped.
        //Looper.prepare();
        Log.i("setMealsServices:", " -======================> I started");
        //try {
            final String pid = intent.getStringExtra("pid");
            Log.i("setMealsServices:", " -======================> pid ="+pid);

            //Toast.makeText(getBaseContext(), "Hello Iam running set meals alarm service", Toast.LENGTH_LONG);
            WebServicesCallers service = new WebServicesCallers();
            response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getMealsTimes/" + pid);

            if (response[0] != null && !(response[0].equals("-1")) && !(response.length == 3)) {
                JSONObject meals = Util.getJSONObject(response[0]);
                JSONArray mealsArray = (JSONArray) meals.get("meals");

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                int alarmID = 2;
                for (Object meal : mealsArray) {
                    try {
                        JSONObject obj = (JSONObject) meal;
                        Intent intentAlarm = new Intent(getBaseContext(), AlarmReciver.class);
                        intentAlarm.putExtra("mid", obj.get("mid").toString());
                        intentAlarm.putExtra("meal_type", obj.get("meal_type").toString());
                        intentAlarm.putExtra("meal_date_time", obj.get("meal_date_time").toString());
                        intentAlarm.putExtra("alarm_type", "2");
                        alarmID++;
                        Log.i("setMealsServices:", " -======================> AlarmID = "+obj.get("mid").toString()+" ,"+obj.toString());
                        alarmManager.cancel(PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(obj.get("mid").toString()), intentAlarm, PendingIntent.FLAG_ONE_SHOT));
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Util.convertMySqlDate(obj.get("meal_date_time").toString()).getTime(),
                                PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(obj.get("mid").toString()), intentAlarm, PendingIntent.FLAG_ONE_SHOT));
                    } catch (Exception e) {
                        Log.i("setMealsServices:", e.getMessage()+"JSON PARSE: "+ response[0] );
                    }
                }
            }

    }

}