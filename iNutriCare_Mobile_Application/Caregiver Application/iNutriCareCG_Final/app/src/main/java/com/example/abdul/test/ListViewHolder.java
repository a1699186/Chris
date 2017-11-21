package com.example.abdul.test;

import android.widget.TextView;

import com.leocardz.aelv.library.AelvListViewHolder;


public class ListViewHolder extends AelvListViewHolder {
    private TextView textView;
    private TextView textView2;

    public ListViewHolder(TextView textView,TextView textView2) {
        super();
        this.textView = textView;
        this.textView2 = textView2;
    }

    public TextView getTextView1() {
        return textView;
    }

    public void setTextView1(TextView textView) {
        this.textView = textView;
    }

    public TextView getTextView2() {
        return textView2;
    }

    public void setTextView2(TextView textView2) {
        this.textView2 = textView2;
    }
}
