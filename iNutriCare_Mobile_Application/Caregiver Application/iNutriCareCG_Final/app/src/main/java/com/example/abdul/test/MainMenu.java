package com.example.abdul.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Created by Vidi on 4/25/2016.
 */
public class MainMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        ImageButton bt1 = (ImageButton) findViewById(R.id.imageButton);
        assert bt1 != null;
        if (bt1 != null)
            bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, MainNewsFeed.class);
                    startActivity(intent);
                }
            });

        ImageButton bt2 = (ImageButton) findViewById(R.id.imageButton2);
        assert bt2 != null;
        if (bt2 != null)
            bt2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, MainPatient.class);
                    startActivity(intent);
                }
            });

        ImageButton bt3 = (ImageButton) findViewById(R.id.imageButton3);
        assert bt3 != null;
        if (bt3 != null)
            bt3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, ViewMeals.class);
                    startActivity(intent);
                }
            });

        ImageButton bt4 = (ImageButton) findViewById(R.id.imageButton4);
        assert bt4 != null;
        if (bt4 != null)
            bt4.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, MainGraph.class);
                    startActivity(intent);
                }
            });

        ImageButton bt5 = (ImageButton) findViewById(R.id.imageButton5);
        assert bt5 != null;
        if (bt5 != null)
            bt5.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, MainConsumption.class);
                    startActivity(intent);
                }
            });

        ImageButton bt6 = (ImageButton) findViewById(R.id.imageButton6);
        assert bt6 != null;
        if (bt6 != null)
            bt6.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, MainFoods.class);
                    startActivity(intent);
                }
            });
        ImageButton bt7 = (ImageButton) findViewById(R.id.imageButton7);
        assert bt7 != null;
        if (bt7 != null)
            bt7.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenu.this, PID.class);
                    startActivity(intent);
                }
            });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.changePassword:
                Intent intent = new Intent(MainMenu.this, changePassword.class);
                startActivity(intent);
                break;
            case R.id.logout:
                AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("You are about to log out. Do you want to proceed ?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog dialog2= Dialog.class.cast(dialog);
                                dialog.dismiss();
                                patient pat=(patient)dialog2.getContext().getApplicationContext();
                                pat.reset();
                                Intent intent = new Intent(MainMenu.this, MainActivity.class);
                                startActivity(intent);
                            }

                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
        }
        return true;
    }
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("You are about to log out. Do you want to proceed ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog dialog2= Dialog.class.cast(dialog);
                        dialog.dismiss();
                        patient pat=(patient)dialog2.getContext().getApplicationContext();
                        pat.reset();
                        Intent intent = new Intent(MainMenu.this, MainActivity.class);
                        startActivity(intent);
                    }

                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
