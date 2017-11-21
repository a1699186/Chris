package com.example.abdul.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abdul on 5/7/2016.
 */
public class MealsAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<MealsSimple> meals;

    public MealsAdapter() {

    }

    public void SetListContext(Context context, ArrayList<MealsSimple> meals)
    {
        layoutInflater = LayoutInflater.from(context);
        this.meals = meals;
    }

    @Override
    public int getCount() {
        if(meals == null)
            return 0;
        return meals.size();
    }

    @Override
    public MealsSimple getItem(int position) {
        return meals.get(position);
    }

    public int remove(int position) {

        if(meals.remove(position)!= null)
            return 1;
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return  meals.get(position).mid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.vm_meals_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.text_meal_type = (TextView) view.findViewById(R.id.VM_mli_meal_type);
            //viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            viewHolder.text_meal_dt = (TextView) view.findViewById(R.id.VM_mli_meal_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        viewHolder.text_meal_type.setText(meals.get(position).Type);
        viewHolder.text_meal_dt.setText(meals.get(position).DateTime);
        //viewHolder.imageView.setImageResource(R.drawable.ic_person_black_24dp);

        return view;
    }

    static class ViewHolder {
        TextView text_meal_type;
        TextView text_meal_dt;
        ImageView imageView;
    }
}
