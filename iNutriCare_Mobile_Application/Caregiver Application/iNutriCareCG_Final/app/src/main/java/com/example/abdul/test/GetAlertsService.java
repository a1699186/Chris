package com.example.abdul.test;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class GetAlertsService extends IntentService {

    final String [] response = new String[1];
    //private Handler hd;

    public GetAlertsService()
    {
        super("GetAlertsService");
    }
    public void onHandleIntent(Intent intent) {
        // Let it continue running until it is stopped.
        //Looper.prepare();
        Log.i("GetAlertsService:", " -======================> I started");
        //try {
            String time = intent.getStringExtra("time");
            Log.i("GetAlertsService:", " -======================> pid ="+time);

            JSONParser parser = new JSONParser();
            Object parsedJson;

            try {
                //Parse the input string
                parsedJson = parser.parse(time);
            }catch (ParseException e)
            {
                Log.i("GetAlertsService:", e.getMessage());
                return;
            }
            //Finally we get our json string parsed as json object in obj
            final JSONObject obj = (JSONObject) parsedJson;

            //Toast.makeText(getBaseContext(), "Hello Iam running set meals alarm service", Toast.LENGTH_LONG);
            WebServicesCallers service = new WebServicesCallers();
                 Log.i("GAS calling WS", "++++++++++++++++++++++++++>>" + obj.toString());
                response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getAlerts/", obj);

                  Log.i("GAS calling OUTPUT", "++++++++++++++++++++++++++>>" + response[0]);
            if (response[0] != null && !response[0].equals("OK") && !response[0].equals("-2") && !response[0].equals("-3") && !(response[0].length() == 3)) {
                JSONObject sources = Util.getJSONObject(response[0]);
                JSONArray sourcesArray = (JSONArray) sources.get("sources");

                String mealType = obj.get("mealtype").toString();
                if(mealType.equals("BF"))
                    mealType = "BreakFast";
                else if(mealType.equals("LN"))
                    mealType = "Lunch";
                else if(mealType.equals("DN"))
                    mealType = "Dinner";
                else if(mealType.equals("SN"))
                    mealType = "Snack";
                String notyMsg = obj.get("firstName").toString()
                        + ", "+ obj.get("lastName").toString() + ": Didn't use the -fridge -microwave -bin -dinning room for "+mealType+" meal";
                for (Object source : sourcesArray) {
                    try {
                        JSONObject obj2 = (JSONObject) source;
                        if(obj2.get("sender").toString().startsWith("F") || obj2.get("sender").toString().startsWith("f"))
                            notyMsg = notyMsg.replace(" -fridge", "");
                        else if(obj2.get("sender").toString().startsWith("M") || obj2.get("sender").toString().startsWith("m"))
                            notyMsg = notyMsg.replace(" -microwave", "");
                        else if(obj2.get("sender").toString().startsWith("B") || obj2.get("sender").toString().startsWith("b"))
                            notyMsg = notyMsg.replace(" -bin", "");
                        else if(obj2.get("sender").toString().startsWith("D") || obj2.get("sender").toString().startsWith("d"))
                            notyMsg = notyMsg.replace(" -dinning room", "");

                        } catch (Exception e) {
                        Log.i("GetAlertsService:", e.getMessage()+"JSON PARSE: "+ response[0] );
                    }
                }

                Intent intent2 = new Intent(getBaseContext(), Track.class);
                intent2.putExtra("time", obj.toString());
                //intent.putExtra("mid", obj.get("mid").toString());
                PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), Integer.parseInt(obj.get("mid").toString()), intent2, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n  = new Notification.Builder(getBaseContext())
                        .setContentTitle("Meal Alert")
                        //.setContentText(notyMsg)
                        .setLights(Color.RED, 500, 500)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new Notification.BigTextStyle().bigText(notyMsg))
                        .setContentIntent(pIntent).build();

                n.defaults |= Notification.DEFAULT_VIBRATE;
                n.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.parseInt(obj.get("mid").toString()), n);

            }
        }
}