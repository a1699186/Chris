package com.example.abdul.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.simple.JSONObject;

/**
 * Created by Vidi on 5/27/2016.
 */
public class LoadingScreenGraph2 extends AppCompatActivity {
    private Handler hd;
    JSONObject graphList=new JSONObject();
    private int day=0;
    private long pid;
    private int year;
    private int month;
    private int check;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog pd;
        final Bundle extras= getIntent().getExtras();
        pid=extras.getLong("pid");
        year=extras.getInt("year");
        month=extras.getInt("month");
        day=extras.getInt("day");
        check=extras.getInt("check");
        position=extras.getInt("position");
        pd = ProgressDialog.show(this, "Please Wait...", "Generating Graph..", false, true);
        pd.setCanceledOnTouchOutside(false);




        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getDetailMeal/" +year+"/"+month+"/"+position+"/"+ pid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", LoadingScreenGraph2.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        graphList = Util.getJSONObject(response[0]);
                        //Util.showAlert("Alert", "Connection Success" + response[0] , LoadingScreenGraph.this);
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoadingScreenGraph2.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("No Data Found!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(LoadingScreenGraph2.this, LoadingScreenGraph.class);
                                        intent.putExtra("day", day);
                                        intent.putExtra("month",month);
                                        intent.putExtra("year",year);
                                        intent.putExtra("pid",pid);
                                        intent.putExtra("check",check);
                                        startActivity(intent);
                                    }
                                });
                        alertDialog.show();
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
                    if(graphList.toString().length()<5)
                    {
                    }
                    else {
                        Intent intent = new Intent(LoadingScreenGraph2.this, PieChartActivity.class);
                        intent.putExtra("graphlist", graphList.toString());
                        intent.putExtra("day", day);
                        intent.putExtra("month",month);
                        intent.putExtra("year",year);
                        intent.putExtra("pid",pid);
                        intent.putExtra("check",check);
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
        Intent intent = new Intent(LoadingScreenGraph2.this,LoadingScreenGraph.class);
        intent.putExtra("day", day);
        intent.putExtra("month",month);
        intent.putExtra("year",year);
        intent.putExtra("pid",pid);
        intent.putExtra("check",check);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return;
    }
}

