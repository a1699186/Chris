package iNutri.care.web.services;
import iNutri.care.web.services.Db;
import iNutri.care.web.services.Meals;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetData {
	
	public ArrayList<Meals> getMealsInfo() throws Exception
	{
	ArrayList<Meals> mealList = new ArrayList<Meals>();
	Db db = new Db();
	db.open("inutricare");
	ResultSet rs = db.query("select mid,concat(firstName,' ',lastName) as name,mealtype , date_format(mealtime,'%d-%m-%y') as mealtime,timetoprepare,date_format(timetoalert,'%d-%m-%y') as timetoalert,comment from meals,patient where meals.pid=patient.pid");
	try
	{
	while(rs.next())
	{
	Meals mealObj = new Meals();
	mealObj.setId(rs.getInt("mid"));
	mealObj.setName(rs.getString("name"));
	mealObj.setMealType(rs.getString("mealtype"));
	mealObj.setMealTime(rs.getString("mealtime"));
	mealObj.setTimeToPrep(rs.getInt("timetoprepare"));
	mealObj.setTimeToAlert(rs.getString("timetoalert"));
	mealObj.setComment(rs.getString("comment"));
	mealList.add(mealObj);
	}
	} catch (SQLException e)
	{
	e.printStackTrace();
	}
	return mealList;
}
}
