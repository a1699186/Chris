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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class AddMeal extends AppCompatActivity {

    final PatientSimple selectedPatient = new PatientSimple();
    final ArrayList<FoodSimple> selectedFoodList = new ArrayList<FoodSimple>();
    final FoodSimpleAdapter SelectedFoodlistAdapter = new FoodSimpleAdapter();
    final PatientSimpleAdapter adapter = new PatientSimpleAdapter();
    final FoodSimpleAdapter adapter2 = new FoodSimpleAdapter();
    private Handler hd;
    private Handler hd2;
    private String cid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        boolean check=getIntent().hasExtra("name");
        String name=((patient)this.getApplication()).getName();
        long id=((patient)this.getApplication()).getId();
        if(name!=null)
        {
            Bundle extras=getIntent().getExtras();
            selectedPatient.pid=id;
            selectedPatient.Name=name;
            ((TextView)findViewById(R.id.AM_patientName)).setText(selectedPatient.Name);
        }
        Button bt1 = (Button) findViewById(R.id.bt_patients_List);
        assert bt1 != null;

        //-----------Get List of patients for the current pid using web service

        final String [] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(AddMeal.this, R.style.Custom);
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
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", AddMeal.this);
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
                        adapter.SetListContext(AddMeal.this, pList);
                    }
                }
            }
        };

       // adapter.SetListContext(AddMeal.this, pList);

        //------------------ Select Patient Button


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialog = DialogPlus.newDialog(AddMeal.this)
                        .setAdapter(adapter)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                //Toast.makeText(AddMeal.this, "Hi: " + ((PatientSimple)item).Name, Toast.LENGTH_LONG).show();

                                if(((PatientSimple)item).pid != selectedPatient.pid)// if not the same patient selected
                                {
                                    ((TextView)findViewById(R.id.AM_patientName)).setText(((PatientSimple)item).Name);
                                    selectedPatient.pid = ((PatientSimple) item).pid;
                                    selectedPatient.Name = ((PatientSimple) item).Name;
                                    patient pat=(patient)view.getContext().getApplicationContext();
                                    pat.setName(selectedPatient.Name);
                                    pat.setId(selectedPatient.pid);
                                    //Delete the selected food items

                                    selectedFoodList.clear();
                                    SelectedFoodlistAdapter.SetListContext(AddMeal.this, selectedFoodList);
                                    SelectedFoodlistAdapter.notifyDataSetChanged();

                                    //Make the food list in adapter2 null because
                                    //the patient selected changed
                                    //So it poll food data from WS again
                                    adapter2.SetListContext(AddMeal.this, null);
                                }

                                //selectedPatient = (PatientSimple)item;
                                dialog.dismiss();
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
        //--------------- ADD FOOD ITEM plus Image Button
        ImageButton bt2 = (ImageButton) findViewById(R.id.AM_add_FoodItems);
        assert bt2 != null;
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedPatient.pid == 0)
                {
                    Util.showAlert("Error", "Select a patient", AddMeal.this);
                    return;
                }
                //----------- Get food item applicable for selectedPatient using pid by calling getFood web service
                if(adapter2.isNull()) {
                    final String[] response = new String[1];
                    final AlertDialog pd;
                    pd = new SpotsDialog(AddMeal.this, R.style.Custom);
                    pd.setCancelable(false);
                    pd.show();


                    final Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            WebServicesCallers service = new WebServicesCallers();
                            response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getFoods/" + selectedPatient.pid);
                            hd.sendEmptyMessage(0);
                        }
                    });
                    t1.start();

                    hd = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            pd.dismiss();

                            if (response[0].length() == 3) {
                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", AddMeal.this);
                            } else {
                                if (response[0].length() > 1)// contains JSONData
                                {
                                    JSONObject foodList = Util.getJSONObject(response[0]);
                                    ArrayList<FoodSimple> fList = new ArrayList<FoodSimple>();
                                    JSONArray foodArray = (JSONArray) foodList.get("foods");
                                    for (Object patient : foodArray) {

                                        JSONObject obj = (JSONObject) patient;
                                        FoodSimple f = new FoodSimple();
                                        f.fid = Long.parseLong((String) obj.get("fid"));
                                        f.Name = (String) obj.get("name");
                                        fList.add(f);
                                    }
                                    adapter2.SetListContext(AddMeal.this, fList);
                                }
                            }

                            DialogPlus dialog = DialogPlus.newDialog(AddMeal.this)
                                    .setAdapter(adapter2)
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                            selectedFoodList.add((FoodSimple) item);
                                            SelectedFoodlistAdapter.SetListContext(AddMeal.this, selectedFoodList);
                                            SelectedFoodlistAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setGravity(Gravity.BOTTOM)
                                    .setMargin(90, 30, 90, 30)
                                    .setHeader(R.layout.dialog_plus_food_list_header)
                                    .setCancelable(true)
                                    .create();
                            dialog.show();
                        }
                    };
                }
                else {
                    //adapter2.SetListContext(AddMeal.this, fList);
                    DialogPlus dialog = DialogPlus.newDialog(AddMeal.this)
                            .setAdapter(adapter2)
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    selectedFoodList.add((FoodSimple) item);
                                    SelectedFoodlistAdapter.SetListContext(AddMeal.this, selectedFoodList);
                                    SelectedFoodlistAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            })
                            .setGravity(Gravity.BOTTOM)
                            .setMargin(90, 30, 90, 30)
                            .setHeader(R.layout.dialog_plus_food_list_header)
                            .setCancelable(true)
                            .create();
                    dialog.show();
                }
            }
        });
        //-------------
        //------------- Selected Food ListView (Swipe Menu)

        SwipeMenuListView LV_Food_List = (SwipeMenuListView)findViewById(R.id.AM_Food_list);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "Delete ICON inside Swipe Menu" item
                SwipeMenuItem DeleteItem = new SwipeMenuItem(
                        getApplicationContext());
                DeleteItem.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x1B, 0x17)));
                DeleteItem.setWidth(180);
                DeleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(DeleteItem);
            }
        };

        LV_Food_List.setAdapter(SelectedFoodlistAdapter);
        LV_Food_List.setMenuCreator(creator);
        LV_Food_List.setOpenInterpolator(new BounceInterpolator());
        LV_Food_List.setCloseInterpolator(new BounceInterpolator());

        LV_Food_List.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    //Delete Icon Clicked
                    case 0:
                        selectedFoodList.remove(position);
                        SelectedFoodlistAdapter.SetListContext(AddMeal.this, selectedFoodList);
                        SelectedFoodlistAdapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }
        });


        //---------------Submit Button
        Button sButton = (Button) findViewById(R.id.AM_Submit);
        assert sButton != null;
        sButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedPatient.pid == 0)
                    {
                        Util.showAlert("Error", "Select a patient", AddMeal.this);
                        return;
                    }

                    if(((Spinner)findViewById(R.id.AM_Meal_Types)).getSelectedItemPosition() == 0)
                    {
                        Util.showAlert("Warning", "Select Meal Type", AddMeal.this);
                        return;
                    }
                    else if(((EditText)findViewById(R.id.AM_Prepare_Time)).getText().length() == 0
                            || ((EditText)findViewById(R.id.AM_Alert_Time)).getText().length() == 0
                            || ((EditText)findViewById(R.id.AM_Meal_Date_Time)).getText().length() == 0)
                    {
                        Util.showAlert("Warning", "Set All the times", AddMeal.this);
                        return;
                    }
                    else if(selectedFoodList.isEmpty())
                    {
                        Util.showAlert("Warning", "Add Food Items", AddMeal.this);
                        return;
                    }

                    final JSONObject obj = new JSONObject();
                    try {
                        obj.put("pid", selectedPatient.pid);
                        obj.put("mealtype", ((Spinner)findViewById(R.id.AM_Meal_Types)).getSelectedItem().toString());
                        obj.put("meal_time", ((EditText)findViewById(R.id.AM_Meal_Date_Time)).getText().toString()+":00");
                        obj.put("prepare_time", ((EditText)findViewById(R.id.AM_Prepare_Time)).getText().toString()+":00");
                        obj.put("alert_time", ((EditText)findViewById(R.id.AM_Alert_Time)).getText().toString()+":00");
                        obj.put("comment", ((EditText)findViewById(R.id.AM_Comment)).getText().toString());

                        JSONArray foodIds = new JSONArray();
                        for(FoodSimple item: selectedFoodList)
                            foodIds.add(item.fid);

                        obj.put("foods", foodIds);

                        final String [] response = new String[1];
                        final AlertDialog pd;
                        pd = new SpotsDialog(AddMeal.this, R.style.Custom);
                        pd.setCancelable(false);
                        pd.show();


                        final Thread t1 = new Thread(new Runnable() {
                            public void run() {
                                WebServicesCallers service = new WebServicesCallers();
                                response[0] = service.Post(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/addmeal", obj);
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
                                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", AddMeal.this);
                                }
                                else if(response[0].equals("1"))
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(AddMeal.this).create();
                                    alertDialog.setTitle("Success");
                                    alertDialog.setMessage("Meal Added");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(AddMeal.this, ViewMeals.class);
                                                    intent.putExtra("pid",selectedPatient.pid);
                                                    intent.putExtra("Name",selectedPatient.Name);
                                                    startActivity(intent);
                                                }
                                            });
                                    alertDialog.show();
                                }
                                else
                                {
                                    Util.showAlert("Error", "Unknown Error ("+response[0]+")", AddMeal.this);
                                }
                            }
                        };



                        //Util.showAlert("OUT JSON", obj.toString(), AddMeal.this);
                    }
                    catch(Exception e) {
                        Util.showAlert("Severe Error", e.getMessage(), AddMeal.this);
                        return;
                    }
                }
        });

        //--------------END OF SUBMIT BUTTON CODE
        final EditText meal_date_time = (EditText) findViewById(R.id.AM_Meal_Date_Time);
        final EditText prepare_time = (EditText) findViewById(R.id.AM_Prepare_Time);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        final SimpleDateFormat dateFormatter2 = new SimpleDateFormat("HH:mm", Locale.US);
        assert meal_date_time != null;
        meal_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {

                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                meal_date_time.setText(dateFormatter.format(date));
                                Date d=new Date();
                                if(meal_date_time.getText().length()>0) {
                                    try {
                                        d = dateFormatter.parse(meal_date_time.getText().toString());
                                        Calendar cal=Calendar.getInstance();
                                        cal.setTime(d);
                                        cal.add(Calendar.HOUR,23-cal.getTime().getHours());
                                        cal.add(Calendar.MINUTE, 60-cal.getTime().getMinutes()+15);
                                        d=cal.getTime();
                                        prepare_time.setText(dateFormatter2.format(d));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onDateTimeCancel()
                            {
                                // Overriding onDateTimeCancel() is optional.
                            }
                        })
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

        final EditText alert_time = (EditText) findViewById(R.id.AM_Alert_Time);

        final SimpleDateFormat dateFormatter3 = new SimpleDateFormat("HH:mm", Locale.US);
        assert prepare_time != null;
        prepare_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                try {
                    d = dateFormatter.parse(meal_date_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal=Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.HOUR,23-cal.getTime().getHours());
                cal.add(Calendar.MINUTE, 60-cal.getTime().getMinutes()+15);
                d=cal.getTime();
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                prepare_time.setText(dateFormatter2.format(date));
                                Date d2 = new Date();
                                try {
                                    d2 = dateFormatter2.parse(prepare_time.getText().toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar cal=Calendar.getInstance();
                                cal.setTime(d2);
                                cal.add(Calendar.MINUTE, -12);
                                d2=cal.getTime();
                                alert_time.setText(dateFormatter3.format(d2));
                            }

                            @Override
                            public void onDateTimeCancel()
                            {
                                // Overriding onDateTimeCancel() is optional.
                            }
                        })
                        .setInitialDate(d)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });


        assert alert_time != null;
        alert_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                try {
                    d = dateFormatter2.parse(prepare_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal=Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, -12);
                d=cal.getTime();
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {

                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                alert_time.setText(dateFormatter3.format(date));
                            }

                            @Override
                            public void onDateTimeCancel()
                            {
                                // Overriding onDateTimeCancel() is optional.
                            }
                        })
                        .setInitialDate(d)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(AddMeal.this,ViewMeals.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
