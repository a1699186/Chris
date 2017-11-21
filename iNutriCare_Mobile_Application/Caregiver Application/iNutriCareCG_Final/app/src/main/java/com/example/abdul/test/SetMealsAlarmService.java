package com.example.abdul.test;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Calendar;


public class SetMealsAlarmService extends IntentService {

    final String [] response = new String[1];
    //private Handler hd;

    public SetMealsAlarmService()
    {
        super("SetMealsAlarmService");
    }

    protected void onHandleIntent(Intent intent) {
        // Let it continue running until it is stopped.
        //Looper.prepare();
        Log.i("setMealsAlarm:", " -======================> I started");
        //try {
            final String cid = intent.getStringExtra("cid");
            Log.i("setMealsAlarm:", " -======================> pid ="+cid);
           // final Thread t1 = new Thread(new Runnable() {
             //public void run() {
            //Toast.makeText(getBaseContext(), "Hello Iam running set meals alarm service", Toast.LENGTH_LONG);
            WebServicesCallers service = new WebServicesCallers();
            response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getAllMealsTimes/" + cid);
            //hd.sendEmptyMessage(0);
               // }
             //});
        //t1.start();
            //Log.i("setMealsServices:", " -======================> response ="+response[0]);
          //  hd = new Handler() {
            //  @Override
            //public void handleMessage(Message msg) {
            if (response[0] != null && !response[0].equals("0") && !(response[0].length() == 3) && !(response[0].length() == 2)) {
                JSONObject times = Util.getJSONObject(response[0]);
                JSONArray timesArray = (JSONArray) times.get("times");

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //int alarmID = 2;
                for (Object time : timesArray) {
                    try {
                        JSONObject obj = (JSONObject) time;
                        Intent intentAlarm = new Intent(getBaseContext(), AlarmReciverGC.class);
                        intentAlarm.putExtra("time", obj.toString());
                        intentAlarm.putExtra("alarm_type", "2");
                        Log.i("setMealsAlarm:", " -======================> AlarmID = "+obj.get("mid").toString()+" ,"+obj.toString());
                        Calendar to = Calendar.getInstance();
                        to.setTime(Util.convertMySqlDate(obj.get("meal_time").toString()));

                        String splitPrepareTime[] = obj.get("prepare_time").toString().split(":");

                        to.add(Calendar.HOUR, Integer.parseInt(splitPrepareTime[0]));
                        to.add(Calendar.MINUTE, Integer.parseInt(splitPrepareTime[1]));
                        to.add(Calendar.SECOND, Integer.parseInt(splitPrepareTime[2]));
                        //to.add(Calendar.MINUTE, 3);

                        alarmManager.cancel(PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(obj.get("mid").toString()), intentAlarm, PendingIntent.FLAG_ONE_SHOT));
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, to.getTimeInMillis(),
                                PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(obj.get("mid").toString()), intentAlarm, PendingIntent.FLAG_ONE_SHOT));
                    } catch (Exception e) {
                        Log.i("setMealsAlarm:", e.getMessage()+"JSON PARSE: "+ response[0] );
                    }
                }
            }

        Log.i("setMealsAlarm:", " passed response ="+response[0]);
       // }
            //catch(Exception e2)
            //{
              //  Log.i("setMealsServices:", e2.getMessage()+"Other: "+response[0]);
            //}
         //   };
    }

}