package com.example.abdul.test;

import android.app.Application;

/**
 * Created by Vidi on 6/2/2016.
 */
public class patient extends Application{
    private String name;
    private long id;

    public String getName(){
        return name;
    }
    public long getId(){
        return id;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public void setId(long id)
    {
        this.id=id;
    }
    public void reset()
    {
        name=null;
        id=0;
    }
}
