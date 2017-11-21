package com.adelaide.cs.abdul.patient;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmView extends AppCompatActivity {




    final MediaPlayer sounds[] = new MediaPlayer[2];

    @Override
    protected void onDestroy()
    {
       // Toast.makeText(AlarmView.this, "Desttroy", Toast.LENGTH_LONG).show();
        super.onDestroy();
        sounds[0].release();
        sounds[1].release();
    }

    /*@Override
    protected void onStop()
    {
        Toast.makeText(AlarmView.this, "Stop", Toast.LENGTH_LONG).show();
        super.onStop();
        sounds[0].release();
        sounds[1].release();
    }

    @Override
    protected void onPause()
    {
        Toast.makeText(AlarmView.this, "Pause", Toast.LENGTH_LONG).show();
        super.onPause();
        sounds[0].release();
        sounds[1].release();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i("AlarmView:  ", "Called*********************************************************************************");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            //final boolean stop[] = new boolean[1];
            sounds[0] = MediaPlayer.create(AlarmView.this, R.raw.classic_phone);
            final Vibrator vibrat = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

            String mealtype = "";
            switch(getIntent().getStringExtra("meal_type").toString())
            {
                case "BF":
                    mealtype = "Break Fast";
                    sounds[1] = MediaPlayer.create(AlarmView.this, R.raw.bf);
                    break;
                case "LN":
                    mealtype = "Lunch";
                    sounds[1] = MediaPlayer.create(AlarmView.this, R.raw.ln);
                    break;
                case "DN":
                    mealtype = "Dinner";
                    sounds[1] = MediaPlayer.create(AlarmView.this, R.raw.dn);
                    break;
                case "SN":
                    mealtype = "Snack";
                    sounds[1] = MediaPlayer.create(AlarmView.this, R.raw.sn);
                    break;
            }
            //sounds[0].setWakeMode(AlarmView.this, PowerManager.PARTIAL_WAKE_LOCK);
            //sounds[1].setWakeMode(AlarmView.this, PowerManager.PARTIAL_WAKE_LOCK);
            ((TextView) findViewById(R.id.AV_mealName)).setText(mealtype);
            //Toast.makeText(this, "BreakFast Time: Hurry to your kitchen please", Toast.LENGTH_LONG).show();
            try {

                sounds[0].prepare();
                sounds[1].prepare();
            } catch (Exception e) {
                //Toast.makeText(AlarmView.this, "Caused by prepare "+e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Button submit = (Button)findViewById(R.id.AV_button);
            assert submit != null;

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sounds[0].release();
                    sounds[1].release();
                    //Toast.makeText(AlarmView.this, "Alarm Stopped thank you", Toast.LENGTH_LONG).show();

                    Intent mealView = new Intent(getBaseContext(), MealView.class);
                    mealView.putExtra("mid", getIntent().getStringExtra("mid").toString());
                    startActivity(mealView);

                    finish();
                }
            });
            sounds[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    vibrat.vibrate(300);
                    sounds[1].start();
                }
            });
            sounds[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    vibrat.vibrate(1000);
                    sounds[0].start();
                }
            });
            //mPlayer.setLooping(true);
            vibrat.vibrate(500);
            vibrat.vibrate(500);

            sounds[1].start();
        }
        catch(Exception e)
        {
            Toast.makeText(AlarmView.this, "Start "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        Intent setIntent = new Intent(AlarmView.this,MyMeals.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(setIntent);
        finish();
        return;
    }
}
