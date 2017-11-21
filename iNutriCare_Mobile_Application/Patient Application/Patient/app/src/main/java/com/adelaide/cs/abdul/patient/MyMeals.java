package com.adelaide.cs.abdul.patient;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class MyMeals extends AppCompatActivity {

    private Handler hd;
    final private MealsAdapter mealsAdapter = new MealsAdapter();
    final private ArrayList<MealsSimple> mealsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meals);


        //--------set main alarm to be fired always at 5:00 AM daily
        try {


            FileInputStream _file = openFileInput("myInutriCare.pid");

            InputStreamReader isr = new InputStreamReader(_file);
            BufferedReader bufferedReader = new BufferedReader(isr);
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            //Toast.makeText(MyMeals.this, WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/getMealsTimes/"+sb.toString(), Toast.LENGTH_LONG).show();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //calendar.set(Calendar.HOUR_OF_DAY, 2);

            Intent intentAlarm = new Intent(getBaseContext(), AlarmReciver.class);
            intentAlarm.putExtra("pid", sb.toString());

                /*Alarm_Type = 1 if it is the alarm that invokes the SetMealsAlarmService
                  Alarm_Type = 2 if it is the alarm that show the Alarm View to the elder patient on screen
                 */
            intentAlarm.putExtra("alarm_type", "1");
            alarmManager.cancel(PendingIntent.getBroadcast(getBaseContext(), 1, intentAlarm, 0));
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    20000L, PendingIntent.getBroadcast(getBaseContext(), 1, intentAlarm, 0));

            final String response[] = new String[1];
            final Thread t1 = new Thread(new Runnable() {
            public void run() {
                    //Log.i(getApplicationContext().toString(),"=========================Before calling web service");
                    WebServicesCallers service = new WebServicesCallers();
                    response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/getMealsTimes/"+sb.toString());
                //Log.i(getApplicationContext().toString(),"=========================After calling calling web service ("+response[0]);
                    hd.sendEmptyMessage(0);
                }
             });

            //final AlertDialog pd;
            //pd = new SpotsDialog(MyMeals.this, R.style.Custom);
            //pd.setCancelable(false);
            //pd.show();
            final ProgressDialog ringProgressDialog = ProgressDialog.show(MyMeals.this, "Please wait", "Downloading Upcoming Meals", false);
            //ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();
            t1.start();

            hd = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ringProgressDialog.dismiss();
                    if(response[0].length() == 3)
                    {
                        Util.showAlert("Error", "Cannot connect to server ("+response[0]+")", MyMeals.this);
                        return;
                    }
                    else if(response[0]!=null) {
                        JSONObject meals = Util.getJSONObject(response[0]);
                        JSONArray mealsArray = (JSONArray) meals.get("meals");

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
                        SimpleDateFormat mySqlDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        for (Object meal : mealsArray) {
                            try {
                                JSONObject obj = (JSONObject) meal;
                                MealsSimple ml = new MealsSimple();
                                ml.mid = Long.parseLong(obj.get("mid").toString());
                                //ml.Type = obj.get("meal_type").toString();

                                String mealtype = "";
                                switch(obj.get("meal_type").toString())
                                {
                                    case "BF":
                                        mealtype = "Break Fast";
                                        break;
                                    case "LN":
                                        mealtype = "Lunch";
                                        break;
                                    case "DN":
                                        mealtype = "Dinner";
                                        break;
                                    case "SN":
                                        mealtype = "Snack";
                                        break;
                                }
                                ml.Type = mealtype;
                                ml.DateTime = formatter.format(mySqlDate.parse((String) obj.get("meal_date_time"))).toString();
                                //formatter.format(mySqlDate.parse((String) obj.get("meal_date_time"))).toString();
                                mealsList.add(ml);

                            } catch (Exception e) {
                                Util.showAlert("Error", "Parsing Error " + e.getMessage(), MyMeals.this);
                            }
                        }
                        mealsAdapter.SetListContext(MyMeals.this, mealsList);
                        //((SwipeMenuListView)findViewById(R.id.MM_Meals_list)).setAdapter(mealsAdapter);
                        mealsAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Util.showAlert("Network Error", "You don't have internet connection", MyMeals.this);
                        return;
                    }
                }
            };

            SwipeMenuListView LVS_meal_list = (SwipeMenuListView)findViewById(R.id.MM_Meals_list);
            assert LVS_meal_list != null;
            LVS_meal_list.setAdapter(mealsAdapter);
            //LVS_meal_list.setMenuCreator(creator);
            LVS_meal_list.setOpenInterpolator(new BounceInterpolator());
            LVS_meal_list.setCloseInterpolator(new BounceInterpolator());

            LVS_meal_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent viewMealIntent = new Intent(MyMeals.this, MealView.class);
                    viewMealIntent.putExtra("mid", mealsList.get(position).mid+"");
                    startActivity(viewMealIntent);
                }
            });
        }
        catch (Exception e)
        {
            Util.showAlert("Alert", "PID file Not found", MyMeals.this);
            Intent Intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(Intent);
            finish();
        }
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/


        /*((TextView)findViewById(R.id.textView4)).setText("Welcome " + getIntent().getStringExtra("pid"));


        ((Button)findViewById(R.id.MM_SetAlarm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        //JSONObject obj = (JSONObject) meal;
                        Intent intentAlarm = new Intent(getBaseContext(), AlarmReciver.class);
                        intentAlarm.putExtra("mid", "1");
                        intentAlarm.putExtra("meal_type", "LN");
                        intentAlarm.putExtra("meal_date_time", new Date().toString());
                        intentAlarm.putExtra("alarm_type", "2");
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000,
                                PendingIntent.getBroadcast(getBaseContext(), 100, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
            }
        });*/
    }
}
