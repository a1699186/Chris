package com.adelaide.cs.abdul.patient;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(Util.fileExistance("myInutriCare.pid", getBaseContext()))
        {
            try {
                Intent next = new Intent(getBaseContext(), MyMeals.class);
                FileInputStream _file = openFileInput("myInutriCare.pid");

                InputStreamReader isr = new InputStreamReader(_file);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                next.putExtra("pid", sb.toString());
                startActivity(next);

                finish();
                return;
            }
            catch(Exception e)
            {
                ;
            }
        }


        setContentView(R.layout.activity_main);
        Button submit = (Button)findViewById(R.id.MA_Submit);
        assert submit != null;

        //startService(new Intent(getBaseContext(), SetMealsAlarmService.class));

        submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(((EditText)findViewById(R.id.editText)).getText().length() <= 0)
                                            {
                                                Util.showAlert("Error", "Please insert your User ID obtained from your care giver", MainActivity.this);
                                                return;
                                            }
                                            else
                                            {
                                                try {
                                                    FileOutputStream fOut = openFileOutput("myInutriCare.pid", Context.MODE_PRIVATE);
                                                    //fOut = openFileOutput(fileName, Context.MODE_PRIVATE);
                                                    fOut.write(((EditText)findViewById(R.id.editText)).getText().toString().getBytes());
                                                    fOut.close();

                                                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                                    //calendar.set(Calendar.HOUR_OF_DAY, 1); // 5 means 5AM
                                                    Intent intentAlarm = new Intent(getBaseContext(), AlarmReciver.class);
                                                    intentAlarm.putExtra("pid", ((EditText)findViewById(R.id.editText)).getText().toString());
                                                    intentAlarm.putExtra("alarm_type", "1");
                                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                                            20000L, PendingIntent.getBroadcast(getBaseContext(),1,  intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
                                                    Intent myMealsIntent = new Intent(MainActivity.this, MyMeals.class);
                                                    startActivity(myMealsIntent);
                                                    finish();
                                                }
                                                catch(Exception e)
                                                {
                                                    Util.showAlert("Error", e.getMessage(), MainActivity.this);
                                                    return;
                                                }
                                            }
                                        }
                                    });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i("MainActivity ", "----------------------- Started");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i("MainActivity  ", "----------------------- Paused");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i("MainActivity ", "----------------------- Resumed");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i("MainActivity ", "----------------------- Stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("MainActivity  ", "----------------------- Destroyed");
    }


}
