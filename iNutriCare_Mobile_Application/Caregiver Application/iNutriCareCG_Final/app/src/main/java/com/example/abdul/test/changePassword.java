package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.simple.JSONObject;

import dmax.dialog.SpotsDialog;

/**
 * Created by Vidi on 6/2/2016.
 */
public class changePassword extends AppCompatActivity {
    private Handler hd=new Handler();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
    }
    public void changePass(View v)
    {
        String newPass=((EditText)findViewById(R.id.npassword)).getText().toString();
        String cnewPass=((EditText)findViewById(R.id.confirmNPs)).getText().toString();
        if(!newPass.equals(cnewPass))
        {
            Util.showAlert("Error", "The password and its confirm don't match", changePassword.this);
        }
        else
        {
            final JSONObject obj = new JSONObject();
            try {
                obj.put("cid", 1);
                obj.put("password", newPass);
                obj.put("oldPassword", ((EditText)findViewById(R.id.oldpassword)).getText().toString());

                final String [] response = new String[1];
                final AlertDialog pd;
                pd = new SpotsDialog(changePassword.this, R.style.Custom);
                pd.setCancelable(false);
                pd.show();


                final Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        WebServicesCallers service = new WebServicesCallers();
                        response[0] = service.Post(WebServicesCallers.baseURL+"/iNutriCareWebServices/rest/changepassword", obj);
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
                            Util.showAlert("Alert", "Unable to connect to the server (" + response[0] +")", changePassword.this);
                        }
                        else if(response[0].equals("1"))
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(changePassword.this).create();
                            alertDialog.setTitle("Success");
                            alertDialog.setMessage("Password Changed");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(changePassword.this, MainMenu.class);
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();
                        }
                        else if(response[0].equals("-3"))
                        {
                            Util.showAlert("Alert", "Old Password is Wrong!", changePassword.this);
                        }
                        else
                        {
                            Util.showAlert("Error", "Unknown Error ("+response[0]+")", changePassword.this);
                        }
                    }
                };
            }
            catch(Exception e) {
                Util.showAlert("Severe Error", e.getMessage(), changePassword.this);
                return;
            }
        }
    }
}
