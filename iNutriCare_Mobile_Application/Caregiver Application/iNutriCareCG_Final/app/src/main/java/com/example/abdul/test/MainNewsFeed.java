package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by Vidi on 5/27/2016.
 */
public class MainNewsFeed extends AppCompatActivity {

    final NewsFeedAdapter newsListAdapter = new NewsFeedAdapter();
    final News_Feed selectedNews = new News_Feed();
    private Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        //-----------Get List of news using web service

        final String[] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(MainNewsFeed.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();


        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Get(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getNewsFeed/");
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", MainNewsFeed.this);
                } else {
                    if (response[0].length() > 1)// contains JSONData
                    {
                        JSONObject newsList = Util.getJSONObject(response[0]);
                        ArrayList<News_Feed> pList = new ArrayList<News_Feed>();
                        JSONArray newsArray = (JSONArray) newsList.get("news");
                        for (Object news : newsArray) {

                            JSONObject obj = (JSONObject) news;
                            News_Feed p = new News_Feed();
                            p.id = Long.parseLong((String) obj.get("id"));
                            p.mName = (String) obj.get("fullName");
                            p.information= "[sender : "+obj.get("sender")+"] \n[receiver : "+obj.get("receiver")+"] \n"+obj.get("message")+"\n\n"+obj.get("msgTime").toString();
                            pList.add(p);
                        }
                        newsListAdapter.SetListContext(MainNewsFeed.this, pList);
                        ListView LV_News = (ListView)findViewById(R.id.listViewNF);
                        LV_News.setAdapter(newsListAdapter);
                    }
                }
            }
        };

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(MainNewsFeed.this,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}