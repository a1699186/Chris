package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * Created by Vidi on 5/25/2016.
 */
public class MainGraph extends AppCompatActivity {

    final PatientSimpleAdapter patientListAdapter = new PatientSimpleAdapter();
    final PatientSimple selectedPatient = new PatientSimple();
    final MealsAdapter mealsListAdapter = new MealsAdapter();
    private Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        RadioButton monthButton=(RadioButton)findViewById(R.id.radio_a);
        monthButton.setChecked(true);
        String name=((patient)this.getApplication()).getName();
        long id=((patient)this.getApplication()).getId();
        if(name!=null)
        {
            Bundle extras=getIntent().getExtras();
            selectedPatient.pid=id;
            selectedPatient.Name=name;
            ((TextView)findViewById(R.id.VG_patient_name)).setText(selectedPatient.Name);
        }


        final String[] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(MainGraph.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/patientList/" + 1);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MainGraph.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        JSONObject patientList = Util.getJSONObject(response[0]);
                        ArrayList<PatientSimple> pList = new ArrayList<PatientSimple>();
                        JSONArray patientArray = (JSONArray) patientList.get("patients");
                        for (Object patient : patientArray) {

                            JSONObject obj = (JSONObject) patient;
                            PatientSimple p = new PatientSimple();
                            p.pid = Long.parseLong((String) obj.get("pid"));
                            p.Name = (String) obj.get("fullName");

                            pList.add(p);
                        }
                        patientListAdapter.SetListContext(MainGraph.this, pList);
                    }
                }
            }
        };
        Button bt1 = (Button)findViewById(R.id.VG_bt_patients_List);
        assert bt1 != null;
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialog = DialogPlus.newDialog(MainGraph.this)
                        .setAdapter(patientListAdapter)
                        //When a patient selected bring his meals list
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                //Toast.makeText(AddMeal.this, "Hi: " + ((PatientSimple)item).Name, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                if(((PatientSimple)item).pid != selectedPatient.pid)// if not the same patient selected
                                {
                                    ((TextView)findViewById(R.id.VG_patient_name)).setText(((PatientSimple)item).Name);
                                    selectedPatient.pid = ((PatientSimple) item).pid;
                                    selectedPatient.Name = ((PatientSimple) item).Name;
                                    patient pat=(patient)view.getContext().getApplicationContext();
                                    pat.setName(selectedPatient.Name);
                                    pat.setId(selectedPatient.pid);
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
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        Spinner a=(Spinner)findViewById(R.id.VG_month);
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_a:
                if (checked) {
                    a.setEnabled(true);
                    break;
                }
            case R.id.radio_b:
                if (checked) {
                    a.setEnabled(false);
                    break;
                }
        }
    }
    public void submitGraph(View v)
    {
        int day=12;
        int month=0;
        RadioButton mon=(RadioButton)findViewById(R.id.radio_a);
        TextView tv=(TextView) findViewById(R.id.VG_patient_name);
        Spinner a=(Spinner)findViewById(R.id.VG_month);
        Spinner b=(Spinner)findViewById(R.id.VG_year);
        int year = Integer.parseInt(b.getSelectedItem().toString());
        if(b.getSelectedItem().toString().length()>4 || tv.getText().length()==0)
        {
            Util.showAlert("Warning", "Please Provide All Information", MainGraph.this);
        }
        else {

            Intent setIntent= new Intent(MainGraph.this, LoadingScreenGraph.class);
            if (mon.isChecked()) {
                if(a.getSelectedItem().toString().length()>3 || b.getSelectedItem().toString().length()>4 || tv.getText().length()==0)
                {
                    Util.showAlert("Warning", "Please Provide All Information", MainGraph.this);
                }
                else {
                    String monthName = a.getSelectedItem().toString();
                    Date date = null;//put your month name here
                    try {
                        date = new SimpleDateFormat("MMM").parse(monthName);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    month = date.getMonth();
                    cal.set(year, month, day);
                    day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    month++;
                    Spinner c=(Spinner)findViewById(R.id.VG_graph);

                    if(c.getSelectedItem().toString().equals("Meal Consumption")) {
                        setIntent.putExtra("check",1);
                    }
                    else
                    {
                        setIntent.putExtra("check",2);
                    }

                }
            }
            else
            {
                Spinner c=(Spinner)findViewById(R.id.VG_graph);
                if(c.getSelectedItem().toString().equals("Meal Consumption")) {
                    setIntent.putExtra("check",1);
                }
                else
                {
                    setIntent.putExtra("check",2);
                }
            }
            setIntent.putExtra("day", day);
            setIntent.putExtra("month", month);
            setIntent.putExtra("year", year);
            setIntent.putExtra("pid", selectedPatient.pid);
            //Util.showAlert("warning",day+" "+month+" "+year,MainGraph.this);
            startActivity(setIntent);
        }
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(MainGraph.this,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
