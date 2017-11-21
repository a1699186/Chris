package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by Vidi on 5/20/2016.
 */
public class updateConsumption extends AppCompatActivity{

    private String[] arrName;
    private int[] arrId;
    private String[] arrCalories;
    Handler hd;
    long mid;
    String mtype;
    long pid;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateconsumption2);
        Bundle extras= getIntent().getExtras();
        JSONObject foodList=new JSONObject();
        foodList=Util.getJSONObject(extras.getString("foodlist"));
        mid=extras.getLong("mid");
        mtype=extras.getString("mtype");
        pid=extras.getLong("pid");
        name=extras.getString("name");
        if(mtype.equals("BF"))
        {
            final TextView tv=((TextView)findViewById(R.id.textUC));
            tv.setText("Breakfast Consumption");
        }
        if(mtype.equals("LN"))
        {
            final TextView tv=((TextView)findViewById(R.id.textUC));
            tv.setText("Lunch Consumption");
        }
        if(mtype.equals("DN"))
        {
            final TextView tv=((TextView)findViewById(R.id.textUC));
            tv.setText("Dinner Consumption");
        }
        if(mtype.equals("SN"))
        {
            final TextView tv=((TextView)findViewById(R.id.textUC));
            tv.setText("Snack Consumption");
        }
        JSONArray foodArray = (JSONArray) foodList.get("foods");
        arrName=new String[foodArray.size()];
        arrId=new int[arrName.length];
        for(int i=0;i<arrId.length;i++)
        {
            arrId[i]=0;
        }
        int counter=0;
        for (Object food : foodArray) {

            JSONObject obj = (JSONObject) food;
            arrId[counter] = Integer.parseInt(obj.get("fid").toString());
            arrName[counter] = (String) obj.get("name");
            counter++;
        }

        arrCalories = new String[arrName.length];

        final MyListAdapter myListAdapter = new MyListAdapter();
        final ListView listView = (ListView) findViewById(R.id.listViewMain);
        listView.setAdapter(myListAdapter);
        Button a=(Button)findViewById(R.id.UC_button);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject consumptionJSON=new JSONObject();
                
                JSONArray food = new JSONArray();
                for (int i = 0; i < arrId.length; i++) {
                    JSONObject foodData = new JSONObject();
                    foodData.put("fid", arrId[i]);
                    foodData.put("calories", arrCalories[i]);
                    food.add(foodData);
                }
                consumptionJSON.put("foods", food);
                consumptionJSON.put("mid", mid);
                final String[] response = new String[1];
                final Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        WebServicesCallers service = new WebServicesCallers();
                        response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/updateConsumption/", consumptionJSON);
                        hd.sendEmptyMessage(0);
                    }
                });
                t1.start();

                hd = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (response[0].length() == 3) {
                            Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", updateConsumption.this);
                        } else if (response[0].equals("1")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(updateConsumption.this).create();
                            alertDialog.setTitle("Success");
                            alertDialog.setMessage("New Consumption Information Added");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(updateConsumption.this, MainConsumption.class);
                                            intent.putExtra("pid",pid);
                                            intent.putExtra("name",name);
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            Util.showAlert("Error", "Unknown Error (" + response[0] + ")", updateConsumption.this);
                        }
                    }
                };
            }
        });
    }
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(arrName != null && arrName.length != 0){
                return arrName.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrName[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = updateConsumption.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lyt_listview_list, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.editText1 = (EditText) convertView.findViewById(R.id.editText1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;
            holder.textView1.setText(arrName[position]);
            //holder.editText1.setText(arrCalories[position]);
            holder.editText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.editText1.setText("");
                    //holder.editText1.setTextColor(0x000);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.editText1.setText("");
                    holder.editText1.setTextColor(0x000);
                    holder.editText1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.editText1.setText("");
                            //holder.editText1.setTextColor(0x000);
                        }
                    });
                }
            });
            holder.editText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    arrCalories[holder.ref] = arg0.toString();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }


    }
    public void onBackPressed() {
        Intent setIntent = new Intent(updateConsumption.this,MainConsumption.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
    public void addFood(View v) {
        Intent intent = new Intent(updateConsumption.this, addAdditionalFood.class);
        intent.putExtra("mid", mid);
        intent.putExtra("mtype", mtype);
        intent.putExtra("pid",pid);
        intent.putExtra("name",name);
        startActivity(intent);
    }
    public void clearText(View v)
    {
        EditText a=(EditText) findViewById(R.id.editText1);
        a.setText("");
        a.setTextColor(0x000);
    }
}
