package iNutri.care.web.services;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson; 
import iNutri.care.web.services.GetData;
import iNutri.care.web.services.Meals;
 
@Path("/")
public class test
{
@GET
@Path("/meals")
@Consumes(MediaType.APPLICATION_JSON)
public String meals()
{
String meals = null;
ArrayList<Meals> mealList = new ArrayList<Meals>();
try
{
mealList = new GetData().getMealsInfo();
Gson gson = new Gson();
meals = gson.toJson(mealList);
} catch (Exception e)
{
e.printStackTrace();
}
return meals;
}
}