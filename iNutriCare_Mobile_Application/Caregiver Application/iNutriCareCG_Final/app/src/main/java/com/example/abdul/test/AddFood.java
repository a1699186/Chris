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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by Vidi on 5/18/2016.
 */
public class AddFood extends AppCompatActivity {


    final ArrayList<FoodSimple> selectedFoodList = new ArrayList<FoodSimple>();
    final FoodSimpleAdapter SelectedFoodlistAdapter = new FoodSimpleAdapter();
    final FoodSimpleAdapter adapter2 = new FoodSimpleAdapter();
    private Handler hd;
    private String cid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        //--------------- ADD ING ITEM plus Image Button
        ImageButton bt2 = (ImageButton) findViewById(R.id.AF_add_IngItems);
        assert bt2 != null;
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------- Get food item applicable for selectedPatient using pid by calling getFood web service
                if(adapter2.isNull()) {
                    final String[] response = new String[1];
                    final AlertDialog pd;
                    pd = new SpotsDialog(AddFood.this, R.style.Custom);
                    pd.setCancelable(false);
                    pd.show();


                    final Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            WebServicesCallers service = new WebServicesCallers();
                            response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getIngredients/");
                            hd.sendEmptyMessage(0);
                        }
                    });
                    t1.start();

                    hd = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            pd.dismiss();

                            if (response[0].length() == 3) {
                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", AddFood.this);
                            } else {
                                if (response[0].length() > 1)// contains JSONData
                                {
                                    JSONObject foodList = Util.getJSONObject(response[0]);
                                    ArrayList<FoodSimple> fList = new ArrayList<FoodSimple>();
                                    JSONArray foodArray = (JSONArray) foodList.get("ingredients");
                                    for (Object ingredient : foodArray) {

                                        JSONObject obj = (JSONObject) ingredient;
                                        FoodSimple f = new FoodSimple();
                                        f.fid = (long) obj.get("iid");
                                        f.Name = (String) obj.get("name");
                                        fList.add(f);
                                    }
                                    adapter2.SetListContext(AddFood.this, fList);
                                }
                            }

                            DialogPlus dialog = DialogPlus.newDialog(AddFood.this)
                                    .setAdapter(adapter2)
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                            selectedFoodList.add((FoodSimple) item);
                                            SelectedFoodlistAdapter.SetListContext(AddFood.this, selectedFoodList);
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
                    //adapter2.SetListContext(AddFood.this, fList);
                    DialogPlus dialog = DialogPlus.newDialog(AddFood.this)
                            .setAdapter(adapter2)
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    selectedFoodList.add((FoodSimple) item);
                                    SelectedFoodlistAdapter.SetListContext(AddFood.this, selectedFoodList);
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

        SwipeMenuListView LV_Food_List = (SwipeMenuListView)findViewById(R.id.AF_Ing_list);

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
                        SelectedFoodlistAdapter.SetListContext(AddFood.this, selectedFoodList);
                        SelectedFoodlistAdapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }
        });


        //---------------Submit Button
        Button sButton = (Button) findViewById(R.id.AF_Submit);
        assert sButton != null;
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((EditText)findViewById(R.id.AF_Calories)).getText().length() == 0
                        || ((EditText)findViewById(R.id.AF_Carb)).getText().length() == 0
                        || ((EditText)findViewById(R.id.AF_Protein)).getText().length() == 0
                        || ((EditText)findViewById(R.id.AF_Fat)).getText().length() == 0
                        || ((EditText)findViewById(R.id.AF_FoodName)).getText().length() == 0)
                {
                    Util.showAlert("Warning", "You need to fill all information!", AddFood.this);
                    return;
                }
                else if(selectedFoodList.isEmpty())
                {
                    Util.showAlert("Warning", "Add Ingredients", AddFood.this);
                    return;
                }

                final JSONObject obj = new JSONObject();
                try {
                    obj.put("name", ((EditText)findViewById(R.id.AF_FoodName)).getText().toString());
                    obj.put("energy", ((EditText)findViewById(R.id.AF_Calories)).getText().toString());
                    obj.put("totalProt", ((EditText)findViewById(R.id.AF_Protein)).getText().toString());
                    obj.put("totalCarb", ((EditText)findViewById(R.id.AF_Carb)).getText().toString());
                    obj.put("totalFat", ((EditText)findViewById(R.id.AF_Fat)).getText().toString());

                    JSONArray foodIds = new JSONArray();
                    for(FoodSimple item: selectedFoodList)
                        foodIds.add(item.fid);

                    obj.put("Ingredients", foodIds);



                    final String [] response = new String[1];
                    final AlertDialog pd;
                    pd = new SpotsDialog(AddFood.this, R.style.Custom);
                    pd.setCancelable(false);
                    pd.show();


                    final Thread t1 = new Thread(new Runnable() {
                        public void run() {
                            WebServicesCallers service = new WebServicesCallers();
                            response[0] = service.Post(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/addfood", obj);
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
                                Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", AddFood.this);
                            }
                            else if(response[0].equals("1"))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(AddFood.this).create();
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Food Added");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(AddFood.this, MainFoods.class);
                                                startActivity(intent);
                                            }
                                        });
                                alertDialog.show();
                            }
                            else
                            {
                                Util.showAlert("Error", "Unknown Error ("+response[0].toString()+")", AddFood.this);
                            }
                        }
                    };



                    //Util.showAlert("OUT JSON", obj.toString(), AddFood.this);
                }
                catch(Exception e) {
                    Util.showAlert("Severe Error", e.getMessage(), AddFood.this);
                    return;
                }
            }
        });




    }
    public void onBackPressed() {
        Intent setIntent = new Intent(AddFood.this,MainFoods.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
