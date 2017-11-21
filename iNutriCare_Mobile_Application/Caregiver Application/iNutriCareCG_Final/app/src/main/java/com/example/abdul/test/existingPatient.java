package com.example.abdul.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Vidi on 5/15/2016.
 */
public class existingPatient extends AppCompatActivity{
    private Handler hd;
    private ListView mListView;
    private ArrayList<Filter_Object> mArrFilter;
    private ScrollView mScrollViewFilter;
    private Filter_Adapter mFilter_Adapter ;
    private FlowLayout mFlowLayoutFilter ;
    private long[] getSelectedid;
    private String[] getSelected;
    
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String[] response = new String[1];


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getexistpatient/"+1);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (response[0] != null) {
                    if (response[0].length() == 3) {
                        Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", existingPatient.this);
                    } else {
                        setContentView(R.layout.activity_existingpatient);
                        if (response[0].length() > 1)// contains JSONData
                        {
                            //Util.showAlert("Alert", response[0], existingPatient.this);
                            displayPatientList(response[0]);

                        }
                    }
                } else {
                    Util.showAlert("Alert", "Error during data fetching", existingPatient.this);
                }
            }
        };
    }

    public void submitExistPatient(View v)
    {
        final JSONObject patientJSON=new JSONObject();
            try {
                JSONArray patient = new JSONArray();
                for (int i = 0; i < getSelectedid.length; i++) {
                    JSONObject patientData = new JSONObject();
                    patientData.put("name", getSelected[i]);
                    patientData.put("pid", getSelectedid[i]);
                    patient.add(patientData);
                }
                patientJSON.put("patients", patient);

                patientJSON.put("cid", 1);
                final String[] response = new String[1];
                final Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        WebServicesCallers service = new WebServicesCallers();
                        response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/addexistpatient/", patientJSON);
                        hd.sendEmptyMessage(0);
                    }
                });
                t1.start();

                hd = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (response[0].length() == 3) {
                            Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", existingPatient.this);
                        } else if (response[0].equals("1")) {
                            Util.showAlert("Success", "Patient Added", existingPatient.this);
                            Intent intent = new Intent(existingPatient.this, MainPatient.class);
                            startActivity(intent);
                        } else {
                            Util.showAlert("Error", "Unknown Error (" + response[0] + ")", existingPatient.this);
                        }
                    }
                };

            } catch (Exception e) {
                Util.showAlert("Severe Error", e.getMessage(), existingPatient.this);
                return;
            }
    }
    
    
    
    private ArrayList<Filter_Object> getDataPatient(String response)
    {
        ArrayList<Filter_Object> ArrFilter= new ArrayList<Filter_Object>();
        JSONObject PatientList = Util.getJSONObject(response);
        ArrayList<Filter_Object> mArrFilter = new ArrayList<Filter_Object>();
        JSONArray PatientArray = (JSONArray) PatientList.get("patients");
        for (Object Patient: PatientArray)
        {
            JSONObject obj = (JSONObject) Patient;
            Filter_Object p = new Filter_Object();
            p.id = (long)obj.get("pid");
            p.mName = (String) obj.get("name");
            p.mIsSelected=false;
            ArrFilter.add(p);
        }
        return ArrFilter;
    }


    private void displayPatientList(String response) {

        mArrFilter = getDataPatient(response);

        Collections.sort(mArrFilter, new Comparator<Filter_Object>() {
            @Override
            public int compare(Filter_Object lhs, Filter_Object rhs) {
                char lhsFirstLetter = TextUtils.isEmpty(lhs.mName) ? ' ' : lhs.mName.charAt(0);
                char rhsFirstLetter = TextUtils.isEmpty(rhs.mName) ? ' ' : rhs.mName.charAt(0);
                int firstLetterComparison = Character.toUpperCase(lhsFirstLetter) - Character.toUpperCase(rhsFirstLetter);
                if (firstLetterComparison == 0)
                    return lhs.mName.compareTo(rhs.mName);
                return firstLetterComparison;
            }
        });



        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mListView = (ListView) findViewById(R.id.listViewFilter);
        mScrollViewFilter = (ScrollView)findViewById(R.id.scrollViewFilter);
        mFlowLayoutFilter = (FlowLayout)findViewById(R.id.flowLayout);

        mFilter_Adapter = new Filter_Adapter(mArrFilter);
        mListView.setAdapter(mFilter_Adapter);
    }
    public void addFilterTag() {
        final ArrayList<Filter_Object> arrFilterSelected = new ArrayList<>();

        mFlowLayoutFilter.removeAllViews();

        int length = mArrFilter.size();
        boolean isSelected = false;
        for (int i = 0; i < length; i++) {
            Filter_Object fil = mArrFilter.get(i);
            if (fil.mIsSelected) {
                isSelected = true;
                arrFilterSelected.add(fil);
            }
        }
        if (isSelected) {
            mScrollViewFilter.setVisibility(View.VISIBLE);
        } else {
            mScrollViewFilter.setVisibility(View.GONE);
        }
        int size = arrFilterSelected.size();
        getSelected = new String[size];
        getSelectedid = new long[size];
        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < size; i++) {
            View view = layoutInflater.inflate(R.layout.filter_tag_edit, null);

            TextView tv = (TextView) view.findViewById(R.id.tvTag);
            LinearLayout linClose = (LinearLayout) view.findViewById(R.id.linClose);
            final Filter_Object filter_object = arrFilterSelected.get(i);
            linClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast(filter_object.name);


                    int innerSize = mArrFilter.size();
                    for (int j = 0; j < innerSize; j++) {
                        Filter_Object mFilter_Object = mArrFilter.get(j);
                        if (mFilter_Object.mName.equalsIgnoreCase(filter_object.mName)) {
                            mFilter_Object.mIsSelected = false;

                        }
                    }
                    addFilterTag();
                    mFilter_Adapter.updateListView(mArrFilter);
                }
            });


            tv.setText(filter_object.mName);
            getSelected[i] = filter_object.mName;
            getSelectedid[i] = filter_object.id;
            int color = getResources().getColor(R.color.themecolor);

            View newView = view;
            newView.setBackgroundColor(color);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 10;
            params.topMargin = 5;
            params.leftMargin = 10;
            params.bottomMargin = 5;

            newView.setLayoutParams(params);

            mFlowLayoutFilter.addView(newView);
        }
    }

    public class Filter_Adapter extends BaseAdapter {
        ArrayList<Filter_Object> arrMenu;

        public Filter_Adapter(ArrayList<Filter_Object> arrOptions) {
            this.arrMenu = arrOptions;
        }

        public void updateListView(ArrayList<Filter_Object> mArray) {
            this.arrMenu = mArray;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.arrMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.filter_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mTtvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.mTvSelected = (TextView) convertView.findViewById(R.id.tvSelected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Filter_Object mService_Object = arrMenu.get(position);
            viewHolder.mTtvName.setText(mService_Object.mName);

            if (mService_Object.mIsSelected) {
                viewHolder.mTvSelected.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mTvSelected.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mService_Object.mIsSelected = !mService_Object.mIsSelected;
                    mScrollViewFilter.setVisibility(View.VISIBLE);

                    addFilterTag();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView mTtvName, mTvSelected;

        }
    }

    public void onBackPressed() {
        Intent setIntent = new Intent(existingPatient.this,MainPatient.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
