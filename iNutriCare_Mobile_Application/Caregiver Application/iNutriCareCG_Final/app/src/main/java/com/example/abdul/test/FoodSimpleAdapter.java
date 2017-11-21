package com.example.abdul.test;

/**
 * Created by abdul on 5/5/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FoodSimpleAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<FoodSimple> foods;

    public FoodSimpleAdapter() {

    }

    public void SetListContext(Context context, ArrayList<FoodSimple> fList)
    {
        layoutInflater = LayoutInflater.from(context);
        foods = fList;
    }

    public boolean isNull()
    {
        return (foods == null);
    }
    @Override
    public int getCount() {
        if(foods == null)
            return 0;
        return foods.size();
    }

    @Override
    public FoodSimple getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  foods.get(position).fid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        viewHolder.textView.setText(foods.get(position).Name);
        viewHolder.imageView.setImageResource(R.drawable.ic_food_item);

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}