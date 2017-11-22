package com.example.abdul.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by abdul on 4/22/2016.
 */
public class Util {

    public static void showAlert(String title, String msg, Activity act)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        //alertDialog.
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void showAlert_GoPreviousActivity(String title, String msg, final Activity act)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        //alertDialog.
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        act.finish();
                    }
                });
        alertDialog.show();
    }
    public static JSONObject getJSONObject(String jsonString)
    {
        JSONParser parser = new JSONParser();
        Object parsedJson;
        try {
            parsedJson = parser.parse(jsonString);
            JSONObject obj = (JSONObject) parsedJson;
            return obj;

        }
        catch(ParseException e)
        {
            ;
        }
        return new JSONObject();
    }
    public static Date convertMySqlDate(String date)
    {
        SimpleDateFormat mySqlDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date result = new Date();
        try{
            result = mySqlDate.parse(date);
        }
        catch(java.text.ParseException e)
        {
            return result;
        }
        return result;
    }

    public static String getTimeHHmm(String time)
    {
        try{
            SimpleDateFormat dateFormatter2 = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat formParse = new SimpleDateFormat("HH:mm:ss", Locale.US);

            return dateFormatter2.format(formParse.parse(time));
        }
        catch(java.text.ParseException e)
        {
            ;
        }
        return "";
    }


}