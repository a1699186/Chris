package com.example.abdul.test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dmax.dialog.SpotsDialog;



public class Register extends AppCompatActivity {

    private DatePickerDialog DOB_datePicker;
    private SimpleDateFormat dateFormatter;
    private Handler hd;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        EditText et = (EditText) findViewById(R.id.editText6);
        Button but = (Button) findViewById(R.id.button_1);

        but.setOnClickListener(new OnClickListener() {

            //@Override
            public void onClick(View v) {

                if(((EditText)findViewById(R.id.editText1)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.editText2)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.editText6)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.username2)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.password)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.editText4)).getText().toString().length() == 0
                    || ((EditText)findViewById(R.id.editText5)).getText().toString().length() == 0) {
                    Util.showAlert("Error", "Fill all the data", Register.this);
                    return;
                }

                if(((Spinner)findViewById(R.id.dropdown)).getSelectedItem().toString().equals("Select a role"))
                {
                    Util.showAlert("Error", "Select a role", Register.this);
                    return;
                }

                if(!((EditText)findViewById(R.id.password)).getText().toString().equals(((EditText)findViewById(R.id.confirmPs)).getText().toString()))
                {
                    Util.showAlert("Error", "The password and its confirm don't match", Register.this);
                    return;
                }

                final WebServicesCallers service = new WebServicesCallers();
                final JSONObject jsonInput = new JSONObject();

                jsonInput.put("fName", ((EditText)findViewById(R.id.editText1)).getText().toString());
                jsonInput.put("lName", ((EditText)findViewById(R.id.editText2)).getText().toString());
                jsonInput.put("role",  ((Spinner)findViewById(R.id.dropdown)).getSelectedItem().toString());
                jsonInput.put("DOB",   ((EditText)findViewById(R.id.editText6)).getText().toString());
                jsonInput.put("username", ((EditText)findViewById(R.id.username2)).getText().toString());
                jsonInput.put("password", ((EditText)findViewById(R.id.password)).getText().toString());
                jsonInput.put("preferredname", ((EditText)findViewById(R.id.editText4)).getText().toString());
                jsonInput.put("site", ((EditText)findViewById(R.id.editText5)).getText().toString());

                final String [] response = new String[1];
                final AlertDialog pd;
                pd = new SpotsDialog(Register.this, R.style.Custom);
                pd.setCancelable(false);
                pd.show();

                final Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        response[0] = service.Post(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/register", jsonInput);
                        hd.sendEmptyMessage(0);
                    }
                });
                t1.start();

                hd = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        pd.dismiss();

                        if(response[0].length() > 2)
                        {
                            Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", Register.this);
                        }
                        else if(response[0].equals("-3"))
                        {
                            Util.showAlert("Error", "The username already exists", Register.this);
                            EditText et = ((EditText)findViewById(R.id.username2));
                            et.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                        else if(response[0].equals("1")) {
                            Util.showAlert_GoPreviousActivity("Success", "Registration Completed", Register.this);
                        }
                        else
                        {
                            Util.showAlert("Error", "Unknown Error (" + response[0] + ")", Register.this);
                        }
                    }
                };
            }
        });
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DOB_datePicker = new DatePickerDialog(Register.this, new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        EditText et = (EditText) findViewById(R.id.editText6);
                        et.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                DOB_datePicker.show();
            }
        });
    }
}