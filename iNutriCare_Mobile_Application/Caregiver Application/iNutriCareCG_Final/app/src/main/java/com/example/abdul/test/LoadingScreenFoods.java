package com.example.abdul.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.simple.JSONObject;

/**
 * Created by Vidi on 5/19/2016.
 */
public class LoadingScreenFoods extends AppCompatActivity {
    private Handler hd;
    JSONObject foodList=new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog pd;
        final Bundle extras= getIntent().getExtras();
        pd = ProgressDialog.show(this, "Please Wait...", "Loading Food Information..", false, true);
        pd.setCanceledOnTouchOutside(false);
        final long fid=extras.getLong("fid");



        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getfood/" + fid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", LoadingScreenFoods.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        foodList = Util.getJSONObject(response[0]);
                        //Util.showAlert("Alert", "Connection Success" + response[0] + ")", LoadingScreenFoods.this);
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

                        Intent intent = new Intent(LoadingScreenFoods.this, ShowFoods.class);
                        intent.putExtra("foodlist", foodList.toString());
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
        Intent setIntent = new Intent(LoadingScreenFoods.this,MainFoods.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
