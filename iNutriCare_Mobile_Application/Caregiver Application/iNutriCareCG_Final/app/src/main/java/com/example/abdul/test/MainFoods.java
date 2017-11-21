package com.example.abdul.test;

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
public class MainFoods extends AppCompatActivity {
    final FoodSimple selectedFood = new FoodSimple();
    final FoodSimpleAdapter adapter = new FoodSimpleAdapter();
    private Handler hd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foodmenu);
        final String[] response = new String[1];
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getFoodsList/");
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MainFoods.this);
                } else {
                    if (response[0].length() > 3)// contains JSONData
                    {
                        JSONObject foodList = Util.getJSONObject(response[0]);
                        ArrayList<FoodSimple> pList = new ArrayList<FoodSimple>();
                        JSONArray foodArray = (JSONArray) foodList.get("foods");
                        for (Object food : foodArray) {

                            JSONObject obj = (JSONObject) food;
                            FoodSimple p = new FoodSimple();
                            p.fid = Integer.parseInt(obj.get("fid").toString());
                            p.Name = (String) obj.get("name");
                            pList.add(p);
                        }
                        adapter.SetListContext(MainFoods.this, pList);
                    }
                }
                final SwipeMenuListView LV_Patient_List = (SwipeMenuListView)findViewById(R.id.Food_list);

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        // create "Delete ICON inside Swipe Menu" item
                        SwipeMenuItem ViewItem = new SwipeMenuItem(
                                getApplicationContext());
                        ViewItem.setBackground(new ColorDrawable(Color.rgb(0,220,0)));
                        ViewItem.setWidth(130);
                        ViewItem.setIcon(R.drawable.ic_view_24dp);
                        menu.addMenuItem(ViewItem);

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
                                FoodSimple a= (FoodSimple) LV_Patient_List.getItemAtPosition(position);
                                Intent intent=new Intent(MainFoods.this, LoadingScreenFoods.class);
                                intent.putExtra("fid",a.fid);
                                startActivity(intent);
                                break;
                        }

                        return false;
                    }
                });

            }
        };

    }
    public void addFood(View v)
    {
        Intent intent = new Intent(MainFoods.this, AddFood.class);
        startActivity(intent);
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(MainFoods.this,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }

}