package com.example.abdul.test;

import java.util.ArrayList;

/**
 * Created by Vidi on 5/13/2016.
 */
public class PatientInfo {
    public long pid;
    public String username;
    public String password;
    public String fName;
    public String lName;
    public String nName;
    public String DOB;
    public String street;
    public String city;
    public String state;
    public String postcode;
    public String phoneno;
    public String kinName;
    public String relation;
    public String kinstreet;
    public String kincity;
    public String kinstate;
    public String kinpostcode;
    public String kinphoneno;
    public String MMSE;
    public String healthplan;
    public String livingstat;
    public ArrayList<Filter_Object> allergies;
    public ArrayList<Filter_Object> healthcon;


    PatientInfo()
    {

    }
    PatientInfo(long pid, String fName, String lName,String username,String password, String nName, String DOB, String street, String postcode, String city, String state, String phoneno,String kinName,String relation,String kinstreet,String kincity,String kinstate,String kinpostcode,String kinphoneno,String MMSE,String healthplan,String livingstat, ArrayList<Filter_Object> allergies,ArrayList<Filter_Object> healthcon)
    {
        this.pid = pid;
        this.username=username;
        this.password=password;
        this.fName = fName;
        this.lName=lName;
        this.nName=nName;
        this.DOB=DOB;
        this.state=state;
        this.street=street;
        this.postcode=postcode;
        this.city=city;
        this.phoneno=phoneno;
        this.kinName=kinName;
        this.relation=relation;
        this.kincity=kincity;
        this.kinstreet=kinstreet;
        this.kinstate=kinstate;
        this.kinphoneno=kinphoneno;
        this.kinpostcode=kinpostcode;
        this.MMSE=MMSE;
        this.livingstat=livingstat;
        this.healthplan=healthplan;
        this.allergies=allergies;
        this.healthcon=healthcon;
    }
}
