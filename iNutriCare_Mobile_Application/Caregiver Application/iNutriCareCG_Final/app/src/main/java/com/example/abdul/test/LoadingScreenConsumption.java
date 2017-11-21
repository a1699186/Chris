package com.example.abdul.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.simple.JSONObject;

/**
 * Created by Vidi on 5/20/2016.
 */
public class LoadingScreenConsumption extends AppCompatActivity {
    private Handler hd;
    JSONObject foodList=new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog pd;
        final Bundle extras= getIntent().getExtras();
        pd = ProgressDialog.show(this, "Please Wait...", "Loading Meal Information..", false, true);
        pd.setCanceledOnTouchOutside(false);
        final long mid=extras.getLong("mid");
        final String mealtype=extras.getString("mtype");
        final long pid=extras.getLong("pid");
        final String name=extras.getString("name");


        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getfoodfrommeals/" + mid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", LoadingScreenConsumption.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        foodList = Util.getJSONObject(response[0]);
                        //Util.showAlert("Alert", "Connection Success" + response[0] + ")", LoadingScreenConsumption.this);
                    }
                }
            }
        };
        Thread t = new Thread() {
            @Override
            public void run() {
                try
                {
                    sleep(10000) ; //Delay of 5 seconds

                    Intent intent = new Intent(LoadingScreenConsumption.this, updateConsumption.class);
                    intent.putExtra("foodlist", foodList.toString());
                    intent.putExtra("mid",mid);
                    intent.putExtra("mtype",mealtype);
                    intent.putExtra("pid",pid);
                    intent.putExtra("name",name);
                    startActivity(intent);

                }
                catch (Exception e) {
                }
            }
        };
        t.start();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();
                //Start the Next Activity here...

            }
        };
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(LoadingScreenConsumption.this,MainConsumption.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
