package com.example.abdul.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vidi on 5/19/2016.
 */
public class ShowFoods extends AppCompatActivity {
    private Filter_Adapter mFilter_Adapter ;
    private ListView mListView;
    private FoodInfo foodInfo=new FoodInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food);

        Bundle extras = getIntent().getExtras();
        JSONObject foodList = new JSONObject();

        foodList = Util.getJSONObject(extras.getString("foodlist"));
        foodInfo.name = (String) foodList.get("name");
        foodInfo.calories = (String) foodList.get("energy");
        foodInfo.totalCarb = (String) foodList.get("totalCarb");
        foodInfo.totalProt = (String) foodList.get("totalProt");
        foodInfo.totalFat = (String) foodList.get("totalFat");

        ArrayList<Filter_Object> ArrFilter = new ArrayList<Filter_Object>();
        JSONArray ingArray = (JSONArray) foodList.get("ingredients");
        for (Object ingredient : ingArray) {

            JSONObject obj = (JSONObject) ingredient;
            Filter_Object p = new Filter_Object();
            p.id = Long.parseLong(obj.get("iid").toString());
            p.mName = (String) obj.get("name");
            p.mIsSelected = false;
            ArrFilter.add(p);
        }
        foodInfo.ingredient = ArrFilter;

        ((TextView)findViewById(R.id.Food_name)).setText(foodInfo.name);
        ((TextView)findViewById(R.id.Food_Cal)).setText(foodInfo.calories);
        ((TextView)findViewById(R.id.totalCarb)).setText(foodInfo.totalCarb);
        ((TextView)findViewById(R.id.totalProt)).setText(foodInfo.totalProt);
        ((TextView)findViewById(R.id.totalFat)).setText(foodInfo.totalFat);
        mListView = (ListView) findViewById(R.id.VF_list_Ing);
        mFilter_Adapter = new Filter_Adapter(foodInfo.ingredient);
        mListView.setAdapter(mFilter_Adapter);
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
                convertView = getLayoutInflater().inflate(R.layout.filter_list_ing, null);
                viewHolder = new ViewHolder();
                viewHolder.mTtvName = (TextView) convertView.findViewById(R.id.tvName);
                //viewHolder.mTvSelected = (TextView) convertView.findViewById(R.id.tvSelected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Filter_Object mService_Object = arrMenu.get(position);
            viewHolder.mTtvName.setText(mService_Object.mName);
            return convertView;
        }

        public class ViewHolder {
            TextView mTtvName, mTvSelected;

        }

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(ShowFoods.this,MainFoods.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
