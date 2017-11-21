package com.example.abdul.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class PID extends AppCompatActivity {

    final PIDSimpleAdapter patientListAdapter = new PIDSimpleAdapter();

    private Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pid);

        final String [] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(PID.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/patientList/"+1);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();

                if(response[0].length() == 3)
                {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", PID.this);
                }
                else
                {
                    if(response[0].length() > 1)// contains JSONData
                    {
                        JSONObject patientList = Util.getJSONObject(response[0]);
                        ArrayList<PatientSimple> pList = new ArrayList<PatientSimple>();
                        JSONArray patientArray = (JSONArray) patientList.get("patients");
                        for (Object patient: patientArray) {

                            JSONObject obj = (JSONObject) patient;
                            PatientSimple p = new PatientSimple();
                            p.pid = Long.parseLong((String)obj.get("pid"));
                            p.Name = (String) obj.get("fullName");

                            pList.add(p);
                        }
                        patientListAdapter.SetListContext(PID.this, pList);
                        ((ListView)findViewById(R.id.p_listView)).setAdapter(patientListAdapter);
                    }
                }
            }
        };
    }
}
