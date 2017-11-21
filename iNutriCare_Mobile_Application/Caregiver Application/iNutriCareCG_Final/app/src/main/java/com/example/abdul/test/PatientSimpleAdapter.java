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

public class PatientSimpleAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<PatientSimple> patients;

    public PatientSimpleAdapter() {

    }

    public void SetListContext(Context context, ArrayList<PatientSimple> pList)
    {
        layoutInflater = LayoutInflater.from(context);
        patients = pList;
    }

    @Override
    public int getCount() {

        if(patients == null)
            return 0;
        return patients.size();
    }

    @Override
    public PatientSimple getItem(int position) {
        return patients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  patients.get(position).pid;
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
        viewHolder.textView.setText(patients.get(position).Name);
        viewHolder.imageView.setImageResource(R.drawable.ic_person_black_24dp);

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}