package com.example.abdul.test;


//

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
import android.widget.SpinnerAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class Edit_Meal extends AppCompatActivity {

    final PatientSimple selectedPatient = new PatientSimple();
    final ArrayList<FoodSimple> selectedFoodList = new ArrayList<FoodSimple>();
    final FoodSimpleAdapter SelectedFoodlistAdapter = new FoodSimpleAdapter();
    final FoodSimpleAdapter adapter2 = new FoodSimpleAdapter();
    private Handler hd;
    private String mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__meal);

        //Get pid, patient name and meal id from the calling Activity ViewMeals
        selectedPatient.pid = getIntent().getLongExtra("pid", 0);
        selectedPatient.Name = getIntent().getStringExtra("name");
        mid = getIntent().getLongExtra("mid", 0)+"";

//Just extra checking
        if(selectedPatient.pid <= 0 || mid.length() <= 0) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.EM_patientName)).setText(selectedPatient.Name);
        //--------- Call getMeal webservice and get it is data then populate the UI and selectedFoodListAdapter with the meal data

        final String[] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(Edit_Meal.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getMeal/" + mid);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", Edit_Meal.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        JSONObject mealData = Util.getJSONObject(response[0]);
                        SimpleDateFormat normalDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                        ((EditText)findViewById(R.id.EM_Meal_Date_Time)).setText(normalDateTimeFormat.format(Util.convertMySqlDate(mealData.get("meal_date_time").toString())));
                        ((EditText)findViewById(R.id.EM_Prepare_Time)).setText(Util.getTimeHHmm(mealData.get("prepare_time").toString()));
                        ((EditText)findViewById(R.id.EM_Alert_Time)).setText(Util.getTimeHHmm(mealData.get("alert_time").toString()));
                        ((EditText)findViewById(R.id.EM_Comment)).setText(mealData.get("comment").toString());

                        // Here we get the meal type string from the mealData and select it in the drop down menu
                        Spinner mealTypes = (Spinner)findViewById(R.id.EM_Meal_Types);
                        int itemPosition;
                        SpinnerAdapter mealTypes_dropMenu = mealTypes.getAdapter();
                        for(itemPosition = 0; itemPosition < mealTypes_dropMenu.getCount(); itemPosition++)
                            if(mealTypes_dropMenu.getItem(itemPosition).toString().equals(mealData.get("meal_type").toString())) {
                                mealTypes.setSelection(itemPosition);
                                break;
                            }

                        ArrayList<FoodSimple> fList = new ArrayList<FoodSimple>();
                        JSONArray foodArray = (JSONArray) mealData.get("foods");
                        for (Object food : foodArray) {

                            JSONObject obj = (JSONObject) food;
                            FoodSimple f = new FoodSimple();
                            f.fid = Long.parseLong((String) obj.get("fid"));
                            f.Name = (String) obj.get("name");
                            selectedFoodList.add(f);
                        }
                        SelectedFoodlistAdapter.SetListContext(Edit_Meal.this, selectedFoodList);
                        SelectedFoodlistAdapter.notifyDataSetChanged();
                    }
                }
            }
        };


        //------- End of data populating


        //--------------- ADD FOOD ITEM plus Image Button
        ImageButton bt2 = (ImageButton) findViewById(R.id.EM_add_FoodItems);
        assert bt2 != null;
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------- Get food item for the pid by calling getFood web service
                if(adapter2.isNull()) {
                    final String[] response = new String[1];
                    final AlertDialog pd;
                    pd = new SpotsDialog(Edit_Meal.this, R.style.Custom);
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
                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", Edit_Meal.this);
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
                                    adapter2.SetListContext(Edit_Meal.this, fList);
                                }


                                DialogPlus dialog = DialogPlus.newDialog(Edit_Meal.this)
                                        .setAdapter(adapter2)
                                        .setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                                selectedFoodList.add((FoodSimple) item);
                                                SelectedFoodlistAdapter.SetListContext(Edit_Meal.this, selectedFoodList);
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
                    };
                }
                else {
                    //adapter2.SetListContext(AddMeal.this, fList);
                    DialogPlus dialog = DialogPlus.newDialog(Edit_Meal.this)
                            .setAdapter(adapter2)
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    selectedFoodList.add((FoodSimple) item);
                                    SelectedFoodlistAdapter.SetListContext(Edit_Meal.this, selectedFoodList);
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

        SwipeMenuListView LV_Food_List = (SwipeMenuListView)findViewById(R.id.EM_Food_list);

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
                        SelectedFoodlistAdapter.SetListContext(Edit_Meal.this, selectedFoodList);
                        SelectedFoodlistAdapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }
        });


        //---------------Submit Button
        //- Get all Meal's data from the UI plus the food items associated to this meal from food item
        //  swipeMenuListView form a json object with all those data and send them to editMeal webservice
        //  Show appropriate alert messages to the UI

        Button sButton = (Button) findViewById(R.id.EM_Submit);
        assert sButton != null;
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPatient.pid == 0)
                {
                    Util.showAlert("Error", "Select a patient", Edit_Meal.this);
                    return;
                }

                if(((Spinner)findViewById(R.id.EM_Meal_Types)).getSelectedItemPosition() == 0)
                {
                    Util.showAlert("Warning", "Select Meal Type", Edit_Meal.this);
                    return;
                }
                else if(((EditText)findViewById(R.id.EM_Prepare_Time)).getText().length() == 0
                        || ((EditText)findViewById(R.id.EM_Alert_Time)).getText().length() == 0
                        || ((EditText)findViewById(R.id.EM_Meal_Date_Time)).getText().length() == 0)
                {
                    Util.showAlert("Warning", "Set All the times", Edit_Meal.this);
                    return;
                }
                else if(selectedFoodList.isEmpty())
                {
                    Util.showAlert("Warning", "Add Food Items", Edit_Meal.this);
                    return;
                }

                final JSONObject obj = new JSONObject();
                try {
                    obj.put("mid", mid);
                    obj.put("mealtype", ((Spinner)findViewById(R.id.EM_Meal_Types)).getSelectedItem().toString());
                    obj.put("meal_time", ((EditText)findViewById(R.id.EM_Meal_Date_Time)).getText().toString()+":00");
                    obj.put("prepare_time", ((EditText)findViewById(R.id.EM_Prepare_Time)).getText().toString()+":00");
                    obj.put("alert_time", ((EditText)findViewById(R.id.EM_Alert_Time)).getText().toString()+":00");
                    obj.put("comment", ((EditText)findViewById(R.id.EM_Comment)).getText().toString());

                    JSONArray foodIds = new JSONArray();
                    for(FoodSimple item: selectedFoodList)
                        foodIds.add(item.fid);

                    obj.put("foods", foodIds);


                    final String [] response = new String[1];
                    final AlertDialog pd;
                    pd = new SpotsDialog(Edit_Meal.this, R.style.Custom);
                    pd.setCancelable(false);
                    pd.show();


                    final Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            WebServicesCallers service = new WebServicesCallers();
                            response[0] = service.Post(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/editmeal", obj);
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
                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", Edit_Meal.this);
                            }
                            else if(response[0].equals("1"))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(Edit_Meal.this).create();
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Meal Edited");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(Edit_Meal.this, ViewMeals.class);
                                                startActivity(intent);
                                            }
                                        });
                                alertDialog.show();
                            }
                            else
                            {
                                Util.showAlert("Error", "Unknown Error ("+response[0]+")", Edit_Meal.this);
                            }
                        }
                    };



                    //Util.showAlert("OUT JSON", obj.toString(), AddMeal.this);
                }
                catch(Exception e) {
                    Util.showAlert("Severe Error", e.getMessage(), Edit_Meal.this);
                    return;
                }
            }
        });

        //--------------END OF SUBMIT BUTTON CODE


        //- from here it is the initializations of the UI date and time fields
        // all the data should be from the meal record which came from the webservice
        final EditText meal_date_time = (EditText) findViewById(R.id.EM_Meal_Date_Time);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
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



        final EditText prepare_time = (EditText) findViewById(R.id.EM_Prepare_Time);

        final SimpleDateFormat dateFormatter2 = new SimpleDateFormat("HH:mm", Locale.US);

        assert prepare_time != null;
        prepare_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {

                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                prepare_time.setText(dateFormatter2.format(date));
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

        final EditText alert_time = (EditText) findViewById(R.id.EM_Alert_Time);

        assert alert_time != null;
        alert_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {

                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                alert_time.setText(dateFormatter2.format(date));
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
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(Edit_Meal.this,ViewMeals.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}

