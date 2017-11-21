package com.example.abdul.test;

import com.leocardz.aelv.library.AelvListItem;

public class ListItem extends AelvListItem {

    private String text1;
    private String text2;
    private int drawable;

    public ListItem(String text1, String text2) {
        super();
        this.text1 = text1;
        this.text2 = text2;
        this.drawable = R.drawable.down;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text) {
        this.text2 = text2;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

}
