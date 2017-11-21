package com.example.abdul.test;

import java.util.ArrayList;

/**
 * Created by Vidi on 5/19/2016.
 */
public class FoodInfo {
    public long fid;
    public String name;
    public String calories;
    public String totalCarb;
    public String totalProt;
    public String totalFat;
    public ArrayList<Filter_Object> ingredient;


    FoodInfo()
    {

    }
    FoodInfo(long fid, String name, String calories,String totalCarb,String totalFat, String totalProt, ArrayList<Filter_Object> ingredient)
    {
        this.fid = fid;
        this.name=name;
        this.calories=calories;
        this.totalCarb = totalCarb;
        this.totalFat=totalFat;
        this.totalProt=totalProt;
        this.ingredient=ingredient;
    }
}
