package iNutri.care.web.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import iNutri.care.web.services.Meals;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
public class Client2 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		getMeal();
	}
	
	public static void addPatient()
	{
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://localhost:8080/iNutriCareWebServices/rest/addpatient");
			
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("cid", 1);
			jsonInput.put("fName", "Jack");
			jsonInput.put("lName", "Smith");
			jsonInput.put("LOD", "Mild"); //LOD is level of dementia  ----------!
			jsonInput.put("DOB", "12/09/1955");
			jsonInput.put("username", "patient2");
			jsonInput.put("password", "12345");
			jsonInput.put("NationalID", "1993723");
			/*
			 * JSON will look like
			 * {"cid":"1", "fName":"Jack", "lName":"Smith", "LOD":"Mild", "DOB":"12/09/1955", "username":"patient2", "password":"12345", "NationalID":"1993723"}
			 */
			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(jsonInput.toString()));
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			System.out.println("Output from Server .... \n");
			String output = response.readEntity(String.class);
			System.out.println(output);
			
		  } catch (Exception e) {
			e.printStackTrace();
		  }
	}
	
	public static void register()
	{
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://localhost:8080/iNutriCareWebServices/rest/register");
			
			JSONObject jsonInput = new JSONObject();
			
			jsonInput.put("fName", "Jackson");
			jsonInput.put("lName", "John");
			jsonInput.put("type", "care giver type 1");
			jsonInput.put("DOB", "12/09/1982");
			jsonInput.put("username", "caregiver1");
			jsonInput.put("password", "12345");
			jsonInput.put("NationalID", "1003723");
			jsonInput.put("organization", "Adelaide Royal Hospital");

			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(jsonInput.toString()));
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			System.out.println("Output from Server .... \n");
			String output = response.readEntity(String.class);
			System.out.println(output);
			
		  } catch (Exception e) {
			e.printStackTrace();
		  }
	}
	public static void getMeal() throws Exception
	{
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://localhost:8080/iNutriCareWebServices/rest/getHealthConditions");

			Response response = target.request(MediaType.APPLICATION_JSON).get();
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			System.out.println("Output from Server .... \n");
			String output = response.readEntity(String.class);
			System.out.println(output);
			
		  } catch (Exception e) {
			e.printStackTrace();
		  }
	}
}
