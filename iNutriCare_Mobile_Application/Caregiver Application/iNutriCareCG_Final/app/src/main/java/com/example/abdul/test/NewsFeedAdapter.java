package com.example.abdul.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vidi on 5/27/2016.
 */
public class NewsFeedAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<News_Feed> news;

    public NewsFeedAdapter() {

    }

    public void SetListContext(Context context, ArrayList<News_Feed> nList)
    {
        layoutInflater = LayoutInflater.from(context);
        news = nList;
    }

    @Override
    public int getCount() {

        if(news == null)
            return 0;
        return news.size();
    }

    @Override
    public News_Feed getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  news.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text);
            viewHolder.textView2 = (TextView) view.findViewById(R.id.text2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        viewHolder.textView.setText(news.get(position).mName);
        viewHolder.textView2.setText(news.get(position).information);

        return view;
    }

    static class ViewHolder {
        TextView textView;
        TextView textView2;
    }
}
