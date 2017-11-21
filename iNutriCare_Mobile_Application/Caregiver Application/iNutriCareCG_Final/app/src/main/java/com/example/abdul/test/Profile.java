package com.example.abdul.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. get passed intent
        Intent intent = getIntent();

        // 2. get message value from intent
        String fname = intent.getStringExtra("fName");
        String lname = intent.getStringExtra("lName");


        // 3. show message on textView
        ((TextView)findViewById(R.id.textView2)).setText(fname +", "+lname);

    }
}


