package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vidi on 4/25/2016.
 */
public class MainPatient extends AppCompatActivity {

    final PatientSimple selectedPatient = new PatientSimple();
    final PatientSimpleAdapter adapter = new PatientSimpleAdapter();
    private Handler hd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientmenu);
        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getPatients/" + 1);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MainPatient.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        JSONObject patientList = Util.getJSONObject(response[0]);
                        ArrayList<PatientSimple> pList = new ArrayList<PatientSimple>();
                        JSONArray patientArray = (JSONArray) patientList.get("Patients");
                        for (Object patient : patientArray) {

                            JSONObject obj = (JSONObject) patient;
                            PatientSimple p = new PatientSimple();
                            p.pid = Integer.parseInt(obj.get("pid").toString());
                            p.Name = (String) obj.get("firstName")+" "+(String)obj.get("lastName");
                            pList.add(p);
                        }
                        adapter.SetListContext(MainPatient.this, pList);
                    }
                }
                final SwipeMenuListView LV_Patient_List = (SwipeMenuListView)findViewById(R.id.Patientlist);

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        // create "Delete ICON inside Swipe Menu" item
                        SwipeMenuItem DeleteItem = new SwipeMenuItem(
                                getApplicationContext());
                        DeleteItem.setBackground(new ColorDrawable(Color.rgb(250,0,0)));
                        DeleteItem.setWidth(130);
                        DeleteItem.setIcon(R.drawable.ic_delete);
                        SwipeMenuItem ViewItem = new SwipeMenuItem(
                                getApplicationContext());
                        ViewItem.setBackground(new ColorDrawable(Color.rgb(0,220,0)));
                        ViewItem.setWidth(130);
                        ViewItem.setIcon(R.drawable.ic_view_24dp);
                        SwipeMenuItem UpdateItem = new SwipeMenuItem(
                                getApplicationContext());
                        UpdateItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0xAF,0)));
                        UpdateItem.setWidth(130);
                        UpdateItem.setIcon(R.drawable.ic_edit);
                        menu.addMenuItem(ViewItem);
                        menu.addMenuItem(UpdateItem);
                        menu.addMenuItem(DeleteItem);

                    }
                };

                LV_Patient_List.setAdapter(adapter);
                LV_Patient_List.setMenuCreator(creator);
                LV_Patient_List.setOpenInterpolator(new BounceInterpolator());
                LV_Patient_List.setCloseInterpolator(new BounceInterpolator());

                LV_Patient_List.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                PatientSimple a= (PatientSimple) LV_Patient_List.getItemAtPosition(position);
                                Intent intent=new Intent(MainPatient.this, LoadingScreen.class);
                                intent.putExtra("pid",a.pid);
                                intent.putExtra("code",2);
                                startActivity(intent);
                                break;
                            case 1:
                                a= (PatientSimple) LV_Patient_List.getItemAtPosition(position);
                                intent=new Intent(MainPatient.this, LoadingScreen.class);
                                intent.putExtra("pid",a.pid);
                                intent.putExtra("code",1);
                                startActivity(intent);
                                break;
                            case 2:
                                final int pot=position;
                                AlertDialog alertDialog = new AlertDialog.Builder(MainPatient.this).create();
                                alertDialog.setTitle("Confirm");
                                alertDialog.setMessage("You are about to remove the selected patient. Do you want to proceed ?");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                JSONObject delPatient = new JSONObject();
                                                PatientSimple aa = (PatientSimple) LV_Patient_List.getItemAtPosition(pot);
                                                delPatient.put("pid", aa.pid);
                                                delPatient.put("cid", 1);
                                                deletePatient(delPatient);
                                            }

                                        });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                        }

                        return false;
                    }
                });

            }
        };

    }
    public void addPatient(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Which patient do you want to add?");
        builder.setPositiveButton("Create New\n", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainPatient.this, addPatient.class);
                startActivity(intent);
            }

        });

        builder.setNegativeButton("Add Existing", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainPatient.this, existingPatient.class);
                startActivity(intent);
            }
        });
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0x00,0xA6,0xFF));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0x00,0xA6,0xFF));
            }
        });
        alert.show();

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(MainPatient.this,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
    public void deletePatient(JSONObject deletePatient){
        final JSONObject delPatient=deletePatient;
        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/deletepatient/", delPatient);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MainPatient.this);
                } else if (response[0].equals("1")) {
                    Util.showAlert("Success", "Patient Removed", MainPatient.this);
                    Intent intent = new Intent(MainPatient.this, MainPatient.class);
                    startActivity(intent);
                } else {
                    Util.showAlert("Error", "Unknown Error (" + response[0] + ")", MainPatient.this);
                }
            }
        };
    }


}
