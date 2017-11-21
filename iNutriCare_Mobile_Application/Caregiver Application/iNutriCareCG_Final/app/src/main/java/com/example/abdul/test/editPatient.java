package com.example.abdul.test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Vidi on 5/13/2016.
 */
public class editPatient extends AppCompatActivity {

    private DatePickerDialog DOB_datePicker;
    private SimpleDateFormat dateFormatter;
    private ListView mListView;
    private ArrayList<Filter_Object> mArrFilter;
    private ScrollView mScrollViewFilter;
    private Filter_Adapter mFilter_Adapter;
    private FlowLayout mFlowLayoutFilter;
    private String[] getSelected;
    private String[] getSelected2;
    private int check;
    private long[] getSelectedid;
    private long[] getSelectedid2;
    private String MMSE = "";
    private String livingStat = "";
    private String CPlan = "";
    private Handler hd;
    private Handler hd2;
    final JSONObject patientJSON = new JSONObject();
    private PatientInfo patientInfo=new PatientInfo();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras= getIntent().getExtras();
        JSONObject patientList=new JSONObject();
        patientList=Util.getJSONObject(extras.getString("patientlist"));
        patientInfo.pid=extras.getLong("pid");
        patientInfo.username=(String)patientList.get("username");
        patientInfo.password=(String)patientList.get("password");
        patientInfo.fName=(String)patientList.get("firstName");
        patientInfo.lName=(String)patientList.get("lastName");
        patientInfo.nName=(String)patientList.get("preferredname");
        patientInfo.DOB=(String)patientList.get("DOB");
        patientInfo.street=(String)patientList.get("street");
        patientInfo.state=(String)patientList.get("state");
        patientInfo.postcode=(String)patientList.get("postcode");
        patientInfo.city=(String)patientList.get("city");
        patientInfo.phoneno=(String)patientList.get("phonenumber");
        patientInfo.kinName=(String)patientList.get("nextkinname");
        patientInfo.relation=(String)patientList.get("relationship");
        patientInfo.kincity=(String)patientList.get("kin_city");
        patientInfo.kinstate=(String)patientList.get("kin_state");
        patientInfo.kinstreet=(String)patientList.get("kin_street");
        patientInfo.kinpostcode=(String)patientList.get("kin_postcode");
        patientInfo.kinphoneno=(String)patientList.get("kin_phone");
        patientInfo.MMSE=(String)patientList.get("MMSE");
        patientInfo.livingstat=(String)patientList.get("livingstatus");
        patientInfo.healthplan=(String)patientList.get("careplan");
        ArrayList<Filter_Object> ArrFilter= new ArrayList<Filter_Object>();
        JSONArray allergyArray = (JSONArray) patientList.get("allergies");
        for (Object allergy : allergyArray) {

            JSONObject obj = (JSONObject) allergy;
            Filter_Object p=new Filter_Object();
            p.id = Long.parseLong(obj.get("aid").toString());
            p.mName = (String) obj.get("name");
            p.mIsSelected=false;
            ArrFilter.add(p);
        }
        patientInfo.allergies=ArrFilter;
        ArrayList<Filter_Object> ArrFilter2= new ArrayList<Filter_Object>();
        JSONArray healthArray = (JSONArray) patientList.get("healthconditions");
        for (Object healthcon : healthArray) {

            JSONObject obj = (JSONObject) healthcon;
            Filter_Object p=new Filter_Object();
            p.id = Long.parseLong(obj.get("hid").toString());
            p.mName = (String) obj.get("name");
            p.mIsSelected=false;
            ArrFilter2.add(p);
        }
        patientInfo.healthcon=ArrFilter2;



        setContentView(R.layout.activity_addpatient);
        ((TextView) findViewById(R.id.usrnamepat)).setText(patientInfo.username);
       ((EditText) findViewById(R.id.passpat)).setText(patientInfo.password);
        ((EditText) findViewById(R.id.confpasspat)).setText(patientInfo.password);
        ((EditText) findViewById(R.id.fnamepat)).setText(patientInfo.fName);
        ((EditText) findViewById(R.id.lnamepat)).setText(patientInfo.lName);
        ((EditText) findViewById(R.id.nnamepat)).setText(patientInfo.nName);
        ((EditText) findViewById(R.id.dobpat)).setText(patientInfo.DOB);
        ((EditText) findViewById(R.id.streetpat)).setText(patientInfo.street);
        ((EditText) findViewById(R.id.statepat)).setText(patientInfo.state);
        ((EditText) findViewById(R.id.pcodepat)).setText(patientInfo.postcode);
        ((EditText) findViewById(R.id.citypat)).setText(patientInfo.city);
        ((EditText) findViewById(R.id.phonepat)).setText(patientInfo.phoneno);
        TextView et = (TextView) findViewById(R.id.dobpat);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DOB_datePicker = new DatePickerDialog(editPatient.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        EditText et = (EditText) findViewById(R.id.dobpat);
                        et.setText(dateFormatter.format(newDate.getTime()));
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                DOB_datePicker.show();
            }
        });
    }

    public void ShowForm2(View v) {




        if (((EditText) findViewById(R.id.usrnamepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.passpat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.confpasspat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.fnamepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.lnamepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.nnamepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.dobpat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.streetpat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.statepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.pcodepat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.citypat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.phonepat)).getText().toString().length() == 0) {
            Util.showAlert("Error", "Fill all the data", editPatient.this);
        } else if (!((EditText) findViewById(R.id.passpat)).getText().toString().equals(((EditText) findViewById(R.id.confpasspat)).getText().toString())) {
            Util.showAlert("Error", "The password and its confirm don't match", editPatient.this);
            return;
        }
        else {
            final String[] response = new String[1];
            final JSONObject usernameJSON=new JSONObject();
            usernameJSON.put("username", ((EditText) findViewById(R.id.usrnamepat)).getText().toString());
            usernameJSON.put("pid",patientInfo.pid);
            final Thread t1 = new Thread(new Runnable() {
                public void run() {
                    WebServicesCallers service = new WebServicesCallers();
                    response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/checkusername/", usernameJSON);
                    hd.sendEmptyMessage(0);
                }
            });
            t1.start();

            hd = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (response[0].length() == 3) {
                        Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", editPatient.this);
                    } else if (response[0].equals("2")) {
                        Util.showAlert("Alert", "Username already exists", editPatient.this);
                    } else if (response[0].equals("1")) {
                        try {
                            patientJSON.put("username", ((EditText) findViewById(R.id.usrnamepat)).getText().toString());
                            patientJSON.put("password", ((EditText) findViewById(R.id.passpat)).getText().toString());
                            patientJSON.put("firstName", ((EditText) findViewById(R.id.fnamepat)).getText().toString());
                            patientJSON.put("lastName", ((EditText) findViewById(R.id.lnamepat)).getText().toString());
                            patientJSON.put("preferredname", ((EditText) findViewById(R.id.nnamepat)).getText().toString());
                            patientJSON.put("DOB", ((EditText) findViewById(R.id.dobpat)).getText().toString());
                            patientJSON.put("street", ((EditText) findViewById(R.id.streetpat)).getText().toString());
                            patientJSON.put("postcode", ((EditText) findViewById(R.id.pcodepat)).getText().toString());
                            patientJSON.put("state", ((EditText) findViewById(R.id.statepat)).getText().toString());
                            patientJSON.put("city", ((EditText) findViewById(R.id.citypat)).getText().toString());
                            patientJSON.put("phonenumber", ((EditText) findViewById(R.id.phonepat)).getText().toString());
                            setContentView(R.layout.activity_addpatient2);

                            ((EditText) findViewById(R.id.kinname)).setText(patientInfo.kinName);
                            ((EditText) findViewById(R.id.relationkin)).setText(patientInfo.relation);
                            ((EditText) findViewById(R.id.streetkin)).setText(patientInfo.kinstreet);
                            ((EditText) findViewById(R.id.pcodekin)).setText(patientInfo.kinpostcode);
                            ((EditText) findViewById(R.id.statekin)).setText(patientInfo.kinstate);
                            ((EditText) findViewById(R.id.citykin)).setText(patientInfo.kincity);
                            ((EditText) findViewById(R.id.phonekin)).setText(patientInfo.kinphoneno);

                        }
                        catch(Exception e) {
                            Util.showAlert("Severe Error", e.getMessage(), editPatient.this);
                            return;
                        }
                    }
                    else {
                        Util.showAlert("Error", "Unknown Error (" + response[0] + ")", editPatient.this);
                    }
                }
            };

        }
    }

    public void ShowForm3(View v) {
        if (((EditText) findViewById(R.id.kinname)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.relationkin)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.streetkin)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.statekin)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.pcodekin)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.citykin)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.phonekin)).getText().toString().length() == 0) {
            Util.showAlert("Error", "Fill all the data", editPatient.this);
        } else {
            try {
                patientJSON.put("nextkinname", ((EditText) findViewById(R.id.kinname)).getText().toString());
                patientJSON.put("relationship", ((EditText) findViewById(R.id.relationkin)).getText().toString());
                patientJSON.put("kin_street", ((EditText) findViewById(R.id.streetkin)).getText().toString());
                patientJSON.put("kin_postcode", ((EditText) findViewById(R.id.pcodekin)).getText().toString());
                patientJSON.put("kin_state", ((EditText) findViewById(R.id.statekin)).getText().toString());
                patientJSON.put("kin_city", ((EditText) findViewById(R.id.citykin)).getText().toString());
                patientJSON.put("kin_phone", ((EditText) findViewById(R.id.phonekin)).getText().toString());
                setContentView(R.layout.activity_addpatient3);

                ((EditText) findViewById(R.id.mmse)).setText(patientInfo.MMSE);
                ((EditText) findViewById(R.id.livestat)).setText(patientInfo.livingstat);
                ((EditText) findViewById(R.id.careplan)).setText(patientInfo.healthplan);


                TextView txt1 = (TextView) findViewById(R.id.healthcon);
                String text = "";
                getSelected=new String[patientInfo.healthcon.size()];
                getSelectedid=new long[patientInfo.healthcon.size()];
                    for (int i = 0; i < patientInfo.healthcon.size(); i++) {
                        if (i ==patientInfo.healthcon.size() - 1)
                            text = text + patientInfo.healthcon.get(i).mName;
                        else
                            text = text + patientInfo.healthcon.get(i).mName + ",\n";
                        getSelected[i]=patientInfo.healthcon.get(i).mName;
                        getSelectedid[i]=patientInfo.healthcon.get(i).id;
                    }
                txt1.setText(text);

                TextView txt2 = (TextView) findViewById(R.id.allergies);
                String text2 = "";
                getSelected2=new String[patientInfo.allergies.size()];
                getSelectedid2=new long[patientInfo.allergies.size()];
                for (int i = 0; i < patientInfo.allergies.size(); i++) {
                    if (i ==patientInfo.allergies.size() - 1)
                        text2 = text2 + patientInfo.allergies.get(i).mName;
                    else
                        text2 = text2 + patientInfo.allergies.get(i).mName + ",\n";
                    getSelected2[i]=patientInfo.allergies.get(i).mName;
                    getSelectedid2[i]=patientInfo.allergies.get(i).id;
                }
                txt2.setText(text2);

            } catch (Exception e) {
                Util.showAlert("Severe Error", e.getMessage(), editPatient.this);
                return;
            }

        }
    }

    public void submitpatient(View v) {
        if (((EditText) findViewById(R.id.mmse)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.livestat)).getText().toString().length() == 0
                || ((EditText) findViewById(R.id.careplan)).getText().toString().length() == 0) {
            Util.showAlert("Error", "Fill all the data", editPatient.this);
        } else {
            try {
                patientJSON.put("MMSE", ((EditText) findViewById(R.id.mmse)).getText().toString());
                patientJSON.put("livingstatus", ((EditText) findViewById(R.id.livestat)).getText().toString());
                patientJSON.put("careplan", ((EditText) findViewById(R.id.careplan)).getText().toString());


                JSONArray health = new JSONArray();
                for (int i = 0; i < getSelectedid.length; i++) {
                    JSONObject healthData = new JSONObject();
                    healthData.put("name", getSelected[i]);
                    healthData.put("hid", getSelectedid[i]);
                    health.add(healthData);
                }
                patientJSON.put("healthCondition", health);

                JSONArray allergy = new JSONArray();
                for (int i = 0; i < getSelectedid2.length; i++) {
                    JSONObject allergyData = new JSONObject();
                    allergyData.put("name", getSelected2[i]);
                    allergyData.put("aid", getSelectedid2[i]);
                    allergy.add(allergyData);
                }
                patientJSON.put("allergies", allergy);
                patientJSON.put("cid", 1);
                patientJSON.put("pid",patientInfo.pid);
                final String[] response = new String[1];
                final Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        WebServicesCallers service = new WebServicesCallers();
                        response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/editpatient/", patientJSON);
                        hd.sendEmptyMessage(0);
                    }
                });
                t1.start();

                hd = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (response[0].length() == 3) {
                            Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", editPatient.this);
                        } else if (response[0].equals("1")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(editPatient.this).create();
                            alertDialog.setTitle("Success");
                            alertDialog.setMessage("Patient Edited");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(editPatient.this, MainPatient.class);
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();

                        } else {
                            Util.showAlert("Error", "Unknown Error (" + response[0] + ")", editPatient.this);
                        }
                    }
                };

            } catch (Exception e) {
                Util.showAlert("Severe Error", e.getMessage(), editPatient.this);
                return;
            }
        }
    }


    public void ShowHealthCon(View v) {
        TextView txtMMSE = (TextView) findViewById(R.id.mmse);
        MMSE = txtMMSE.getText().toString();
        TextView txtlive = (TextView) findViewById(R.id.livestat);
        livingStat = txtlive.getText().toString();
        TextView txtcplan = (TextView) findViewById(R.id.careplan);
        CPlan = txtcplan.getText().toString();

        final String[] response = new String[1];


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getHealthConditions/");
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (response[0] != null) {
                    if (response[0].length() == 3) {
                        Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", editPatient.this);
                    } else {
                        setContentView(R.layout.activity_healthcon);
                        check = 1;
                        if (response[0].length() > 1)// contains JSONData
                        {
                            //Util.showAlert("Alert", response[0], editPatient.this);
                            displayHealthList(response[0]);

                        }
                    }
                } else {
                    Util.showAlert("Alert", "Error during data fetching", editPatient.this);
                }
            }
        };
    }

    public void ShowAllergies(View v) {
        TextView txtMMSE = (TextView) findViewById(R.id.mmse);
        MMSE = txtMMSE.getText().toString();
        TextView txtlive = (TextView) findViewById(R.id.livestat);
        livingStat = txtlive.getText().toString();
        TextView txtcplan = (TextView) findViewById(R.id.careplan);
        CPlan = txtcplan.getText().toString();

        final String[] response = new String[1];


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getAllergies/");
                hd2.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (response[0] != null) {
                    if (response[0].length() == 3) {
                        Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", editPatient.this);
                    } else {
                        setContentView(R.layout.activity_allergies);
                        check = 2;
                        if (response[0].length() > 1)// contains JSONData
                        {
                            //Util.showAlert("Alert", response[0], editPatient.this);
                            displayAllergiesList(response[0]);

                        }
                    }
                } else {
                    Util.showAlert("Alert", "Error during data fetching", editPatient.this);
                }
            }
        };


    }

    public void AddHealthCon(View v) {
        setContentView(R.layout.activity_addpatient3);
        TextView txt1 = (TextView) findViewById(R.id.healthcon);
        String text = "";
        if (getSelected != null) {
            for (int i = 0; i < getSelected.length; i++) {
                if (i == getSelected.length - 1)
                    text = text + getSelected[i];
                else
                    text = text + getSelected[i] + ",\n";
            }
        }
        txt1.setText(text);

        TextView txt2 = (TextView) findViewById(R.id.allergies);
        String text2 = "";
        if (getSelected2 != null) {
            for (int i = 0; i < getSelected2.length; i++) {
                if (i == getSelected2.length - 1)
                    text2 = text2 + getSelected2[i];
                else
                    text2 = text2 + getSelected2[i] + ",\n";
            }
        }
        txt2.setText(text2);

        TextView txtMMSE = (TextView) findViewById(R.id.mmse);
        txtMMSE.setText(MMSE);
        TextView txtlive = (TextView) findViewById(R.id.livestat);
        txtlive.setText(livingStat);
        TextView txtcplan = (TextView) findViewById(R.id.careplan);
        txtcplan.setText(CPlan);
    }

    public void AddAllergies(View v) {
        setContentView(R.layout.activity_addpatient3);
        TextView txt1 = (TextView) findViewById(R.id.healthcon);
        String text = "";
        if (getSelected != null) {
            for (int i = 0; i < getSelected.length; i++) {
                if (i == getSelected.length - 1)
                    text = text + getSelected[i];
                else
                    text = text + getSelected[i] + ",\n";
            }
        }
        txt1.setText(text);

        TextView txt2 = (TextView) findViewById(R.id.allergies);
        String text2 = "";
        if (getSelected2 != null) {
            for (int i = 0; i < getSelected2.length; i++) {
                if (i == getSelected2.length - 1)
                    text2 = text2 + getSelected2[i];
                else
                    text2 = text2 + getSelected2[i] + ",\n";
            }
        }
        txt2.setText(text2);
        TextView txtMMSE = (TextView) findViewById(R.id.mmse);
        txtMMSE.setText(MMSE);
        TextView txtlive = (TextView) findViewById(R.id.livestat);
        txtlive.setText(livingStat);
        TextView txtcplan = (TextView) findViewById(R.id.careplan);
        txtcplan.setText(CPlan);
    }

    private ArrayList<Filter_Object> getDataAllergy(String response) {
        ArrayList<Filter_Object> ArrFilter = new ArrayList<Filter_Object>();
        JSONObject AllergyList = Util.getJSONObject(response);
        ArrayList<Filter_Object> mArrFilter = new ArrayList<Filter_Object>();
        JSONArray AllergyArray = (JSONArray) AllergyList.get("allergies");
        for (Object Allergy : AllergyArray) {
            JSONObject obj = (JSONObject) Allergy;
            Filter_Object p = new Filter_Object();
            p.id = (long) obj.get("aid");
            p.mName = (String) obj.get("name");
            p.mIsSelected = false;
            ArrFilter.add(p);
        }
        return ArrFilter;
    }


    private void displayAllergiesList(String response) {

        mArrFilter = getDataAllergy(response);

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
        mScrollViewFilter = (ScrollView) findViewById(R.id.scrollViewFilter);
        mFlowLayoutFilter = (FlowLayout) findViewById(R.id.flowLayout);

        mFilter_Adapter = new Filter_Adapter(mArrFilter);
        mListView.setAdapter(mFilter_Adapter);
    }

    private ArrayList<Filter_Object> getDataHealth(String response) {
        ArrayList<Filter_Object> ArrFilter = new ArrayList<Filter_Object>();
        JSONObject HealthList = Util.getJSONObject(response);
        ArrayList<Filter_Object> mArrFilter = new ArrayList<Filter_Object>();
        JSONArray HealthArray = (JSONArray) HealthList.get("HealthConditions");
        for (Object Health : HealthArray) {
            JSONObject obj = (JSONObject) Health;
            Filter_Object p = new Filter_Object();
            p.id = (long) obj.get("id");
            p.mName = (String) obj.get("name");
            p.mIsSelected = false;
            ArrFilter.add(p);
        }
        return ArrFilter;
    }


    private void displayHealthList(String response) {


        mArrFilter = getDataHealth(response);

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
        mScrollViewFilter = (ScrollView) findViewById(R.id.scrollViewFilter);
        mFlowLayoutFilter = (FlowLayout) findViewById(R.id.flowLayout);

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
        if (check == 1) {
            getSelected = new String[size];
            getSelectedid = new long[size];
        } else if (check == 2) {
            getSelected2 = new String[size];
            getSelectedid2 = new long[size];
        }
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
            if (check == 1) {
                getSelected[i] = filter_object.mName;
                getSelectedid[i] = filter_object.id;
            } else if (check == 2) {
                getSelected2[i] = filter_object.mName;
                getSelectedid2[i] = filter_object.id;
            }
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
        Intent setIntent = new Intent(editPatient.this,MainPatient.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
