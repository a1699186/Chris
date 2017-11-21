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
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ViewMeals extends AppCompatActivity {

    final PatientSimpleAdapter patientListAdapter = new PatientSimpleAdapter();
    final PatientSimple selectedPatient = new PatientSimple();
    final MealsAdapter mealsListAdapter = new MealsAdapter();
    private Handler hd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meals);

        String name=((patient)this.getApplication()).getName();
        long id=((patient)this.getApplication()).getId();
        selectedPatient.pid=0;


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                SwipeMenuItem EditItem = new SwipeMenuItem(
                        getApplicationContext());
                EditItem.setBackground(new ColorDrawable(Color.rgb(0xB6, 0xB6, 0xB4)));
                EditItem.setWidth(180);
                EditItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(EditItem);

                // create "Delete ICON inside Swipe Menu" item
                SwipeMenuItem DeleteItem = new SwipeMenuItem(
                        getApplicationContext());
                DeleteItem.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x1B, 0x17)));
                DeleteItem.setWidth(180);
                DeleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(DeleteItem);
            }
        };




        if(name!=null)
        {
            selectedPatient.pid=id;
            selectedPatient.Name=name;
            ((TextView)findViewById(R.id.VM_patient_name)).setText(selectedPatient.Name);
        }
        //-----------Get List of patients for the current pid using web service


        final String [] response = new String[1];
        final String [] responsex= new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(ViewMeals.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/patientList/"+1);
                responsex[0]= service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/futuremeals/"+selectedPatient.pid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();
                //Util.showAlert("Error", "Unknown Error ("+responsex[0] +" " +selectedPatient.pid +")", ViewMeals.this);
                if(response[0].length() == 3)
                {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", ViewMeals.this);
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
                        patientListAdapter.SetListContext(ViewMeals.this, pList);
                    }
                }
                if(responsex[0].length() == 3)
                {
                    Util.showAlert("Alert", "Unable to connect to the server (" + responsex[0] +")", ViewMeals.this);
                }
                else if(responsex[0].length() == 2)
                {
                    Util.showAlert("Error", "Unknown Error (" + responsex[0] +")", ViewMeals.this);
                }
                else if(responsex[0].equals("0"))
                {
                    return;
                }
                else
                {
                    ArrayList<MealsSimple> li = new ArrayList<MealsSimple>();

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yy, hh:mm");
                    SimpleDateFormat mySqlDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    JSONObject mealstList = Util.getJSONObject(responsex[0]);
                    ArrayList<MealsSimple> mList = new ArrayList<MealsSimple>();
                    JSONArray mealsArray = (JSONArray) mealstList.get("meals");
                    for (Object meal: mealsArray) {

                        JSONObject obj = (JSONObject) meal;
                        MealsSimple m = new MealsSimple();
                        m.mid = Long.parseLong((String)obj.get("mid"));
                        m.Type = (String) obj.get("type");
                        try {
                            m.DateTime = formatter.format(mySqlDate.parse((String) obj.get("date"))).toString();
                        }
                        catch(ParseException e)
                        {
                            Util.showAlert("Debugging", (String) obj.get("date"), ViewMeals.this);
                        }
                        mList.add(m);
                    }
                    mealsListAdapter.SetListContext(ViewMeals.this, mList);
                    mealsListAdapter.notifyDataSetChanged();
                }
            }
        };
        SwipeMenuListView LVS_meal_list = (SwipeMenuListView)findViewById(R.id.VM_meals_list_swipe);

        LVS_meal_list.setAdapter(mealsListAdapter);
        LVS_meal_list.setMenuCreator(creator);
        LVS_meal_list.setOpenInterpolator(new BounceInterpolator());
        LVS_meal_list.setCloseInterpolator(new BounceInterpolator());
        //adapter.SetListContext(AddMeal.this, pList);

        //------------------ Select Patient Button

        Button bt1 = (Button)findViewById(R.id.VM_bt_patients_List);
        assert bt1 != null;
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialog = DialogPlus.newDialog(ViewMeals.this)
                        .setAdapter(patientListAdapter)
                        //When a patient selected bring his meals list
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                //Toast.makeText(AddMeal.this, "Hi: " + ((PatientSimple)item).Name, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                if(((PatientSimple)item).pid != selectedPatient.pid)// if not the same patient selected
                                {
                                    ((TextView)findViewById(R.id.VM_patient_name)).setText(((PatientSimple)item).Name);
                                    selectedPatient.pid = ((PatientSimple) item).pid;
                                    selectedPatient.Name = ((PatientSimple) item).Name;
                                    patient pat=(patient)view.getContext().getApplicationContext();
                                    pat.setName(selectedPatient.Name);
                                    pat.setId(selectedPatient.pid);
                                    /*
                                    Populate the meal swipe list view with data
                                    Call Web Service
                                    and fill meals_list_adapter
                                     */

                                    final String [] response = new String[1];
                                    final AlertDialog pd;
                                    pd = new SpotsDialog(ViewMeals.this, R.style.Custom);
                                    pd.setCancelable(false);
                                    pd.show();

                                    //final long mealID = mealsListAdapter.getItem(pos).mid;
                                    final Thread t1 = new Thread(new Runnable() {
                                        public void run() {
                                            WebServicesCallers service = new WebServicesCallers();
                                            response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/futuremeals/"+selectedPatient.pid);
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
                                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", ViewMeals.this);
                                            }
                                            else if(response[0].length() == 2)
                                            {
                                                Util.showAlert("Error", "Unknown Error (" + response[0] +")", ViewMeals.this);
                                            }
                                            else if(response[0].equals("0"))
                                            {
                                                return;
                                            }
                                            else
                                            {
                                                ArrayList<MealsSimple> li = new ArrayList<MealsSimple>();

                                                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yy, hh:mm");
                                                SimpleDateFormat mySqlDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                                JSONObject mealstList = Util.getJSONObject(response[0]);
                                                ArrayList<MealsSimple> mList = new ArrayList<MealsSimple>();
                                                JSONArray mealsArray = (JSONArray) mealstList.get("meals");
                                                for (Object meal: mealsArray) {

                                                    JSONObject obj = (JSONObject) meal;
                                                    MealsSimple m = new MealsSimple();
                                                    m.mid = Long.parseLong((String)obj.get("mid"));
                                                    m.Type = (String) obj.get("type");
                                                    try {
                                                        m.DateTime = formatter.format(mySqlDate.parse((String) obj.get("date"))).toString();
                                                    }
                                                    catch(ParseException e)
                                                    {
                                                        Util.showAlert("Debugging", (String) obj.get("date"), ViewMeals.this);
                                                    }
                                                    mList.add(m);
                                                }
                                                mealsListAdapter.SetListContext(ViewMeals.this, mList);
                                                mealsListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    };
                                }
                            }
                        })
                        .setGravity(Gravity.TOP)
                        .setMargin(90, 30, 90, 30)
                        .setHeader(R.layout.dialog_plus_patients_list_header)
                        .setCancelable(true)
                        .create();
                dialog.show();
            }
        });


        LVS_meal_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int pos = position;
                switch (index) {
                    //Edit Icon Clicked
                    case 0:
                        Intent intent = new Intent(ViewMeals.this, Edit_Meal.class);

                        /// 2. put key/value data
                        //Util.showAlert("pInfo", selectedPatient.pid + " , " + selectedPatient.Name + " , " + mealsListAdapter.getItem(pos).mid, ViewMeals.this);
                        intent.putExtra("pid", selectedPatient.pid);
                        intent.putExtra("name", selectedPatient.Name);
                        intent.putExtra("mid", mealsListAdapter.getItem(pos).mid);
                        startActivity(intent);
                        break;

                    //Delete Icon Clicked
                    case 1:
                        AlertDialog alertDialog = new AlertDialog.Builder(ViewMeals.this).create();
                        alertDialog.setTitle("Confirm");
                        alertDialog.setMessage("You are about to delete the selected meal. Do you want to proceed ?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //dialog.cancel();

                                        final String [] response = new String[1];
                                        final AlertDialog pd;
                                        pd = new SpotsDialog(ViewMeals.this, R.style.Custom);
                                        pd.setCancelable(false);
                                        pd.show();

                                        final long mealID = mealsListAdapter.getItem(pos).mid;
                                        final Thread t1 = new Thread(new Runnable() {
                                            public void run() {
                                                WebServicesCallers service = new WebServicesCallers();
                                                response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/deleteMeal/"+mealID);
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
                                                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", ViewMeals.this);
                                                }
                                                else
                                                {
                                                    if(response[0].equals("1"))// contains JSONData
                                                    {
                                                        mealsListAdapter.remove(pos);
                                                        mealsListAdapter.notifyDataSetChanged();
                                                    }
                                                    else
                                                    {
                                                        Util.showAlert("Error", "Unknown Error (" + response[0] +")", ViewMeals.this);
                                                    }
                                                }
                                            }
                                        };
                                    }
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                }
                return false;
            }
        });
    }
    public void addMeal(View v)
    {
        Intent intent = new Intent(ViewMeals.this, AddMeal.class);
        startActivity(intent);
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(ViewMeals.this,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
