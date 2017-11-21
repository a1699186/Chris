package com.example.abdul.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leocardz.aelv.library.Aelv;
import com.leocardz.aelv.library.AelvCustomAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vidi on 5/13/2016.
 */
public class showPatient extends AppCompatActivity {
    private ArrayList<ListItem> listItems;
    String patient="";
    String family="";
    String healthcare="";
    private PatientInfo patientInfo=new PatientInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpatient);

        Bundle extras= getIntent().getExtras();
        JSONObject patientList=new JSONObject();
        patientList=Util.getJSONObject(extras.getString("patientlist"));
        patientInfo.username=(String)patientList.get("username");
        patientInfo.password=(String)patientList.get("password");
        patientInfo.fName=(String)patientList.get("firstName");
        patientInfo.lName=(String)patientList.get("lastName");
        patientInfo.nName=(String)patientList.get("preferredname");
        patientInfo.DOB=(String)patientList.get("DOB");
        patientInfo.street=(String)patientList.get("street");
        patientInfo.state=(String)patientList.get("state");
        patientInfo.postcode=(String)patientList.get("postcode");
        patientInfo.city=(String)patientList.get("city");
        patientInfo.phoneno=(String)patientList.get("phonenumber");
        patientInfo.kinName=(String)patientList.get("nextkinname");
        patientInfo.relation=(String)patientList.get("relationship");
        patientInfo.kincity=(String)patientList.get("kin_city");
        patientInfo.kinstate=(String)patientList.get("kin_state");
        patientInfo.kinstreet=(String)patientList.get("kin_street");
        patientInfo.kinpostcode=(String)patientList.get("kin_postcode");
        patientInfo.kinphoneno=(String)patientList.get("kin_phone");
        patientInfo.MMSE=(String)patientList.get("MMSE");
        patientInfo.livingstat=(String)patientList.get("livingstatus");
        patientInfo.healthplan=(String)patientList.get("careplan");
        ArrayList<Filter_Object> ArrFilter= new ArrayList<Filter_Object>();
        JSONArray allergyArray = (JSONArray) patientList.get("allergies");
        for (Object allergy : allergyArray) {

            JSONObject obj = (JSONObject) allergy;
            Filter_Object p=new Filter_Object();
            p.id = Long.parseLong(obj.get("aid").toString());
            p.mName = (String) obj.get("name");
            p.mIsSelected=false;
            ArrFilter.add(p);
        }
        patientInfo.allergies=ArrFilter;
        ArrayList<Filter_Object> ArrFilter2= new ArrayList<Filter_Object>();
        JSONArray healthArray = (JSONArray) patientList.get("healthconditions");
        for (Object healthcon : healthArray) {

            JSONObject obj = (JSONObject) healthcon;
            Filter_Object p=new Filter_Object();
            p.id = Long.parseLong(obj.get("hid").toString());
            p.mName = (String) obj.get("name");
            p.mIsSelected=false;
            ArrFilter2.add(p);
        }
        patientInfo.healthcon=ArrFilter2;

        patient="Patient Name\t\t\t: "+patientInfo.fName+" "+patientInfo.lName+" \n" +
                "Date of Birth\t\t\t: "+patientInfo.DOB+" \n"+
        "Phone Number\t\t: "+patientInfo.phoneno+" \n"+
        "Address : \n"+
        "-  Street\t\t\t\t\t: "+patientInfo.street+" \n"+
        "-  City\t\t\t\t\t\t\t: "+patientInfo.city+" \n"+
        "-  State\t\t\t\t\t\t: "+patientInfo.state+" \n"+
        "-  Post Code\t\t: "+patientInfo.postcode+" \n";

        family="Family Name\t\t\t: "+patientInfo.kinName+" \n" +
                "Relationship\t\t\t: "+patientInfo.relation+" \n"+
                "Phone Number\t\t: "+patientInfo.kinphoneno+" \n"+
                "Address\t: \n"+
                "-  Street\t\t\t\t\t: "+patientInfo.kinstreet+" \n"+
                "-  City\t\t\t\t\t\t\t: "+patientInfo.kincity+" \n"+
                "-  State\t\t\t\t\t\t: "+patientInfo.kinstate+" \n"+
                "-  Post Code\t\t: "+patientInfo.kinpostcode+" \n";

        healthcare="MMSE\t\t\t\t\t\t\t\t\t: "+patientInfo.MMSE+" \n" +
                "Living Status\t\t\t\t: "+patientInfo.livingstat+" \n"+
                "Health Care Plan\t: "+patientInfo.healthplan+" \n"+
                "Health Condition\t: \n";

        for(int i=0;i<patientInfo.healthcon.size();i++)
        {
            healthcare=healthcare+Integer.toString(i+1)+". "+patientInfo.healthcon.get(i).mName+" \n";
        }
        healthcare=healthcare+"\nAllergies : \n";
        for(int i=0;i<patientInfo.allergies.size();i++)
        {
            healthcare=healthcare+Integer.toString(i+1)+". "+patientInfo.allergies.get(i).mName+" \n";
        }

        ListView listView = (ListView) findViewById(R.id.list);

        listItems = new ArrayList<ListItem>();
        mockItems();

        ListAdapter adapter = new ListAdapter(this, R.layout.list_item, listItems);

        listView.setAdapter(adapter);

        // Setup
        // Aelv aelv = new Aelv(true, 200, listItems, listView, adapter);
        final Aelv aelv = new Aelv(true, 100, listItems, listView, adapter, new AelvCustomAction() {
            @Override
            public void onEndAnimation(int position) {
                listItems.get(position).setDrawable(listItems.get(position).isOpen() ? R.drawable.up : R.drawable.down);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                aelv.toggle(view, position);
            }
        });
    }

    private void mockItems() {
        final int COLLAPSED_HEIGHT_1 = 200/*, COLLAPSED_HEIGHT_2 = 400, COLLAPSED_HEIGHT_3 = 600;
        final int EXPANDED_HEIGHT_1 = 1000, EXPANDED_HEIGHT_2 = 1800, EXPANDED_HEIGHT_3 = 1000, EXPANDED_HEIGHT_4 = 400*/;

        ListItem listItem = new ListItem("Patient Details",patient);
        // setUp IS REQUIRED
        listItem.setUp(COLLAPSED_HEIGHT_1, -2, false);
        listItems.add(listItem);

        listItem = new ListItem("Family Details",family);
        // setUp IS REQUIRED
        listItem.setUp(COLLAPSED_HEIGHT_1, -2, false);
        listItems.add(listItem);

        listItem = new ListItem("Health Details",healthcare);
        // setUp IS REQUIRED
        listItem.setUp(COLLAPSED_HEIGHT_1, -2, false);
        listItems.add(listItem);

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(showPatient.this,MainPatient.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
