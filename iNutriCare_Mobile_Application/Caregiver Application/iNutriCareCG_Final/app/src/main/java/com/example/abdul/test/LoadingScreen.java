package com.example.abdul.test;

/**
 * Created by Vidi on 5/15/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.simple.JSONObject;


public class LoadingScreen extends AppCompatActivity {
    private PatientInfo patientInfo=new PatientInfo();
    private Handler hd;
    JSONObject patientList=new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog pd;
        final Bundle extras= getIntent().getExtras();
        pd = ProgressDialog.show(this, "Please Wait...", "Loading Patient Information..", false, true);
        pd.setCanceledOnTouchOutside(false);
        patientInfo.pid=extras.getLong("pid");



        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getpatient/" + patientInfo.pid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", LoadingScreen.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        patientList = Util.getJSONObject(response[0]);
                        //Util.showAlert("Alert", "Connection Success" + response[0] + ")", LoadingScreen.this);
                    }
                }
            }
        };
        Thread t = new Thread() {
            @Override
            public void run() {
                try
                {
                    sleep(10000) ; //Delay of 10 seconds
                    if(extras.getInt("code")==1) {
                        Intent intent = new Intent(LoadingScreen.this, editPatient.class);
                        intent.putExtra("patientlist", patientList.toString());
                        intent.putExtra("pid", patientInfo.pid);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(LoadingScreen.this, showPatient.class);
                        intent.putExtra("patientlist", patientList.toString());
                        startActivity(intent);
                    }

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
        Intent setIntent = new Intent(LoadingScreen.this,MainPatient.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}


