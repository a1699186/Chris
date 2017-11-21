package com.adelaide.cs.abdul.patient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import dmax.dialog.SpotsDialog;

public class MealView extends AppCompatActivity {

    private Handler hd;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_view);


        final String [] response = new String[1];
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MealView.this, "Please wait", "Retrieving Meal Data", false);
        //ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/getMealDetails/"+getIntent().getStringExtra("mid").toString());
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ringProgressDialog.dismiss();

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MealView.this);
                    finish();
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        if(response[0]!=null) {
                            JSONObject meal = Util.getJSONObject(response[0]);
                            JSONArray foodsArray = (JSONArray) meal.get("foods");

                            //Meal Type
                            String mealtype = "";
                            switch(meal.get("meal_type").toString())
                            {
                                case "BF":
                                    mealtype = "Break Fast Meal";
                                    break;
                                case "LN":
                                    mealtype = "Lunch Meal";
                                    break;
                                case "DN":
                                    mealtype = "Dinner Meal";
                                    break;
                                case "SN":
                                    mealtype = "Snack Meal";
                                    break;
                            }

                            ((TextView)findViewById(R.id.MV_comment)).setText(meal.get("comment").toString());
                            ((TextView)findViewById(R.id.MV_MealType)).setText(mealtype);
                            ((TextView)findViewById(R.id.MV_prepareTime)).setText(meal.get("prepare_time").toString());

                            /*
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:textColor="#0067a5"
                            android:textSize="20dp"
                            android:layout_marginTop="10dp"
                            android:textStyle="bold"
                             */

                            LinearLayout foodList = (LinearLayout)findViewById(R.id.MV_foodList);
                            for (Object food : foodsArray) {
                                try {
                                    JSONObject obj = (JSONObject) food;
                                    TextView item = new TextView(MealView.this);
                                    item.setText(obj.get("name").toString());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(0,15,0,0);
                                    item.setTextColor(Color.parseColor("#0067a5"));
                                    item.setTextSize(20);
                                    item.setTypeface(null, Typeface.BOLD);
                                    foodList.addView(item, params);

                                } catch (Exception e) {
                                    Util.showAlert("JSON Error", "Unknown Error (" + e.getMessage() + ")", MealView.this);
                                }
                            }
                        }
                    }
                    else
                    {
                        Util.showAlert("Error", "Unknown Error (" + response[0] + ")", MealView.this);
                    }
                }
            }
        };
    }
}
