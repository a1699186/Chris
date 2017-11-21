package com.example.abdul.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class Track extends AppCompatActivity {


    final NewsFeedAdapter newsListAdapter = new NewsFeedAdapter();
    final News_Feed selectedNews = new News_Feed();
    private Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        final String[] response = new String[1];
        final AlertDialog pd;
        pd = new SpotsDialog(Track.this, R.style.Custom);
        pd.setCancelable(false);
        pd.show();

        String time = getIntent().getStringExtra("time");
        Log.i("Track :", " -======================> time ="+time);

        JSONParser parser = new JSONParser();
        Object parsedJson = null;

        try {
            //Parse the input string
            parsedJson = parser.parse(time);
        }catch (Exception e)
        {
            Log.i("GetAlertsService:", e.getMessage());
            return;
        }
        final JSONObject obj = (JSONObject) parsedJson;

        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                WebServicesCallers service = new WebServicesCallers();
                response[0] = service.Post(WebServicesCallers.baseURL + "/iNutriCareWebServices/rest/getNewsFeed/", obj);
                hd.sendEmptyMessage(0);
            }
        });
        t1.start();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();

                if (response[0].length() == 3) {
                    Util.showAlert("Alert", "Unable to connect to the server (" + response[0] + ")", Track.this);
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
                            p.information= "[sender : "+obj.get("sender")+"] [receiver : "+obj.get("receiver")+"] \n"+obj.get("message");
                            pList.add(p);
                        }
                        newsListAdapter.SetListContext(Track.this, pList);
                        ListView LV_News = (ListView)findViewById(R.id.listViewTR);
                        LV_News.setAdapter(newsListAdapter);
                       // newsListAdapter.notifyDataSetChanged();
                        if(pList.size() == 0)
                            ((TextView) findViewById(R.id.TR_textView)).setText("No interaction");
                    }
                }
            }
        };

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(Track.this ,MainMenu.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
