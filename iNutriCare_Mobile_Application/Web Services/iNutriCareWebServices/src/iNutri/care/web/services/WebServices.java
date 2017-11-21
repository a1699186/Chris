package iNutri.care.web.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@Path("/")
public class WebServices {

	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/test")
    public String test(){
		return "Hi Working";
	}

	  //Done by Abdulaziz
	  @POST
	  @Path("/register")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String register(String jsonInput) {
		  
		  JSONParser parser = new JSONParser();
		  Object parsedJson;
		  int result = 1;
		  Db database = new Db();
		  System.err.println(jsonInput);
		try {
			parsedJson = parser.parse(jsonInput);
			JSONObject obj = (JSONObject) parsedJson;
			database.open("iNutriCare");
			
			ResultSet r = database.query("select count(*) from caregiver where username='"+obj.get("username")+"'");
			int countUsername = 0;
			while(r.next())
			{
				countUsername = r.getInt(1);
			}

			if(countUsername == 0)
			
			result = database.updateQuery("insert into caregiver (username, password, firstName, lastName, DOB, role, site,preferredname)"
					+ " values ('"+obj.get("username")+"'," // fetching the username from json obj
					+ "'"+obj.get("password")+"',"          // fetching the password from json obj and so on ...
					+ "'"+obj.get("fName")+"',"
					+ "'"+obj.get("lName")+"',"
					+ "'"+obj.get("DOB")+"',"
					+ "'"+obj.get("role")+"',"
					+ "'"+obj.get("site")+"',"
					+ "'"+obj.get("preferredname")+"')");
			else 
			{
				database.close();
				return "-3"; //username existed
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			database.close();
			return "-2"; //Invalid Input format "Invalid JSON input"
		} catch(Exception e)
		{
			database.close();
			return "-1"; //Database level Error
		}
		//Here we close the connection to the database
		database.close();
	    return result+"";
	  }
	  

		  				//Done by Yichao
					  	@SuppressWarnings("unchecked")
						@GET
						@Path("/getMealDetails/{mid}")
						//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
						public String getMealDetails(@PathParam("mid")String mid){
							
					    	try {
					    	    Thread.sleep(2000);                 //1000 milliseconds is one second.
					    	} catch(InterruptedException ex) {
					    	    Thread.currentThread().interrupt();
					    	}
					    	
							Db database = new Db();
							Db database2 = new Db();
							System.err.println(mid);
							ResultSet rs;
							ResultSet rs2;
							ResultSet rs3;
							JSONObject root = new JSONObject();
							JSONArray foodList = new JSONArray();
							JSONObject error = new JSONObject();
							try{
								database.open("iNutriCare");
								database2.open("iNutriCare");
								
								rs = database.query("select * from meal where mid ='"+mid+"'");
							
								while(rs.next())
								{
									root.put("meal_type", rs.getString("mealtype"));
									root.put("prepare_time", rs.getString("prepare_time"));
									root.put("comment", rs.getString("comment"));
									
								}

								rs2 = database.query("SELECT * FROM rel_food_consumption where mid ='"+mid+"'");

								while(rs2.next())
								{
										rs3 = database2.query("SELECT * FROM fooditem where fid ='"+rs2.getString("fid")+"'");
										while(rs3.next())
										{
										JSONObject food = new JSONObject();
										food.put("fid", rs3.getString("fid"));
										food.put("name", rs3.getString("name"));
										foodList.add(food);
										}
								}
								root.put("foods", foodList);

								}
							catch(Exception e)
							{
									error.put("error", e.getMessage());
									database.close();
									database2.close();
									return error.toString(); //Database level Error
								}
								//Here we close the connection to the database
								database.close();
								database2.close();
								//System.out.println("3");
							    return root.toString();
							    }
					  	
					    @GET
						@Path("/getMealsTimes/{pid}")
						//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
						public String getMealsTimes(@PathParam("pid")String pid){
							
					    	try {
					    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
					    	} catch(InterruptedException ex) {
					    	    Thread.currentThread().interrupt();
					    	}
					    	System.err.println(pid);
							Db database = new Db();
							System.err.println(pid);
							JSONObject Mealstime = new JSONObject();
							JSONArray MealArray = new JSONArray();
							JSONObject error = new JSONObject();
							try{
								
								//connect to iNutriCare database in mySql Server
								database.open("iNutriCare");
											
								ResultSet result = database.query("SELECT * FROM meal where DATE(meal_time) = CURRENT_DATE() and TIME(meal_time) > current_time() AND pid='"+pid+"'");
								
								while(result.next())
									{
										JSONObject mealTimeObj = new JSONObject();
										mealTimeObj.put("mid", result.getString("mid"));
										mealTimeObj.put("meal_type", result.getString("mealtype"));
										mealTimeObj.put("meal_date_time", result.getString("meal_time"));
										MealArray.add(mealTimeObj);						
									}
								Mealstime.put("meals", MealArray);
								
								}
								catch(Exception e)
									{
										return "-1"; //Database level Error
									}
									//Here we close the connection to the database
									database.close();
									//System.out.println("3");
									System.err.println(Mealstime.toString());
								    return Mealstime.toString();
								    }
					    
					    //Done by Abdulaziz
					    @SuppressWarnings("unchecked")
						@GET
						@Path("/getAllMealsTimes/{cid}")
					    public String getAllMealsTimes(@PathParam("cid")String cid){
							
					    	try {
					    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
					    	} catch(InterruptedException ex) {
					    	    Thread.currentThread().interrupt();
					    	}
					    	
							Db database = new Db();
							System.err.println(cid);
							try{

								
								//connect to iNutriCare database in mySql Server
								database.open("iNutriCare");
											
								ResultSet rs = database.query("SELECT p.pid, p.firstName, p.lastName, m.mid, m.prepare_time, m.meal_time, m.mealtype FROM caregiver as cg, rel_patient_caregiver " 
										+" as rel, patient as p, meal as m "
										+" where cg.cid = '"+cid+"' and cg.cid = rel.cid and rel.pid = p.pid"
										+" and DATE(m.meal_time) = CURRENT_DATE() and TIME(m.meal_time) > current_time()"
										+" AND m.pid = p.pid");
								
								JSONObject root = new JSONObject();
								JSONArray timesList = new JSONArray();
								//rs.first();
								while(rs.next())
								{
									JSONObject time = new JSONObject();
									time.put("pid", rs.getString(1));
									time.put("firstName", rs.getString(2));
									time.put("lastName", rs.getString(3));
									time.put("mid", rs.getString(4));
									time.put("prepare_time", rs.getString(5));
									time.put("meal_time", rs.getString(6));
									time.put("mealtype", rs.getString(7));
									
									timesList.add(time);
								}
								root.put("times", timesList);
								System.err.println(root.toString());
								database.close();
								if(timesList.size() == 0)
									return "0";
								return root.toString();
								
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								database.close();
								return "-2"; //Invalid Input format "Invalid JSON input"
							} catch(Exception e)
							{
								database.close();
								System.err.println(e.getMessage());
								return "-3"; //Database level Error
							}
						  }
					    
					      //Done by Abdulaziz
					      @SuppressWarnings("unchecked")
					      @POST
						  @Path("/getAlerts")
						  @Consumes(MediaType.APPLICATION_JSON)
						  public String getAlerts(String jsonInput) 
						  {
						  	  JSONParser parser = new JSONParser();
							  Object parsedJson;
							  
							  Db database = new Db();
							  
							try {
								parsedJson = parser.parse(jsonInput);
								JSONObject obj = (JSONObject) parsedJson;
								database.open("iNutriCare");
								
								System.err.println(jsonInput);
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

							    Date from = new Date();

							    from = formatter.parse(obj.get("meal_time").toString());
	
								
							    Calendar to = Calendar.getInstance();
							    to.setTime(from);
							    
							    String splitPrepareTime[] = obj.get("prepare_time").toString().split(":");

								to.add(Calendar.HOUR, Integer.parseInt(splitPrepareTime[0]));
								to.add(Calendar.MINUTE, Integer.parseInt(splitPrepareTime[1]));
								to.add(Calendar.SECOND, Integer.parseInt(splitPrepareTime[2]));
								to.add(Calendar.MINUTE, 3);
								
								String stFrom = formatter.format(from);
								String stTo = formatter.format(to.getTime());
								
								ResultSet rs = database.query("select sender from smartmsgs where pid = '"+obj.get("pid").toString()+"' "
															 +" and msgTime >= '"+stFrom+"' and msgTime <= '"+stTo+"' group by sender order by sender");
								
								JSONObject root = new JSONObject();
								JSONArray msgsList = new JSONArray();
								//rs.first();
								while(rs.next())
								{
									JSONObject source = new JSONObject();
									source.put("sender", rs.getString(1));
									
									msgsList.add(source);
								}
								root.put("sources", msgsList);
								System.out.println(root.toString());
								database.close();
								if(msgsList.size() >= 4)
									return "OK";
								return root.toString();
								
							}	
						
							catch (ParseException e) {
								// TODO Auto-generated catch block
								database.close();
								System.err.println("-2 : " + e.getMessage());
								return "-2"; //Invalid Input format "Invalid JSON input"
							} catch(Exception e)
							{
								database.close();
								System.err.println("-3 : " + e.getMessage());
								return "-3";
								 //Database level Error
							}
						  }
					      
					      @SuppressWarnings("unchecked")
					      @GET
						  @Path("/getAlertsTest/{pid}")
						  public String getAlertsTest(@PathParam("pid")String pid) 
						  {
						  	  JSONParser parser = new JSONParser();
							  Object parsedJson;
							  
							  Db database = new Db();
							  
							try {
								//Parse the input string
								//parsedJson = parser.parse(jsonInput);
								//Finally we get our json string parsed as json object in obj
								//JSONObject obj = (JSONObject) parsedJson;
								//connect to iNutriCare database in mySql Server
								database.open("iNutriCare");
								
								//System.err.println(jsonInput);
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

							    Date from = new Date();
							    //try
							    //{
							    from = formatter.parse("2016-06-06 03:40:00");
							    //}
							    //catch(ParseException e)
							    //{
							      //   return "0";
							    //}
								
							    Calendar to = Calendar.getInstance();
							    to.setTime(from);
							    
							    String splitPrepareTime[] = "00:15:00".split(":");

								to.add(Calendar.HOUR, Integer.parseInt(splitPrepareTime[0]));
								to.add(Calendar.MINUTE, Integer.parseInt(splitPrepareTime[1]));
								to.add(Calendar.MINUTE, 3);
								to.add(Calendar.SECOND, Integer.parseInt(splitPrepareTime[2]));
								
								String stFrom = formatter.format(from);
								String stTo = formatter.format(to.getTime());
								
								ResultSet rs = database.query("select sender from smartmsgs where pid = '"+pid+"' "
															 +" and msgTime >= '"+stFrom+"' and msgTime <= '"+stTo+"' group by sender order by sender");
								
								JSONObject root = new JSONObject();
								JSONArray msgsList = new JSONArray();
								//rs.first();
								while(rs.next())
								{
									JSONObject source = new JSONObject();
									source.put("sender", rs.getString(1));
									
									msgsList.add(source);
								}
								root.put("sources", msgsList);
								if(msgsList.size() >= 4)
									return "OK";
								return root.toString();
								
							}	
						
							catch (ParseException e) {
								// TODO Auto-generated catch block
								database.close();
								System.err.println("-2 : " + e.getMessage());
								return "-2"; //Invalid Input format "Invalid JSON input"
							} catch(Exception e)
							{
								database.close();
								System.err.println("-3 : " + e.getMessage());
								return "-3";
								 //Database level Error
							}
						  }
					      
					      
					      //Done By Abdulaziz
					      @SuppressWarnings("unchecked")
							@POST
							@Path("/getNewsFeed")
					      	@Consumes(MediaType.APPLICATION_JSON)
							public String getNewsFeed(String jsonInput){
					    	  System.err.println("NewsFeed: ");
						    	try {
						    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
						    	} catch(InterruptedException ex) {
						    	    Thread.currentThread().interrupt();
						    	}
						    	
								Db database = new Db();
								//Timestamp current = new Timestamp(today.getTime());
								JSONObject NewsFeedObj = new JSONObject();
								JSONArray NewsFeedList = new JSONArray();
								//JSONObject error = new JSONObject();
								JSONParser parser = new JSONParser();
								Object parsedJson;
								
								try{

									//connect to iNutriCare database in mySql Server
									database.open("iNutriCare");
									
									parsedJson = parser.parse(jsonInput);
									JSONObject obj = (JSONObject) parsedJson;
									
									System.err.println(jsonInput);
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

								    Date from = new Date();

								    from = formatter.parse(obj.get("meal_time").toString());
		
									
								    Calendar to = Calendar.getInstance();
								    to.setTime(from);
								    
								    String splitPrepareTime[] = obj.get("prepare_time").toString().split(":");

									to.add(Calendar.HOUR, Integer.parseInt(splitPrepareTime[0]));
									to.add(Calendar.MINUTE, Integer.parseInt(splitPrepareTime[1]));
									to.add(Calendar.SECOND, Integer.parseInt(splitPrepareTime[2]));
									to.add(Calendar.MINUTE, 3);
									
									String stFrom = formatter.format(from);
									String stTo = formatter.format(to.getTime());
											
									ResultSet result = database.query("SELECT s.id,concat(p.firstName,' ',p.lastName) as fullName,s.sender,s.receiver,s.msgContent "
											+ "from smartmsgs s,patient p where p.pid=s.pid and p.pid = '"+obj.get("pid").toString()+"'"
											+ "and s.msgTime >= '"+stFrom+"' and s.msgTime <= '"+stTo+"'" 
											+ " order by s.msgTime DESC");
									while(result.next())
									{
											JSONObject NewsFeed = new JSONObject();
											NewsFeed.put("id", result.getString(1));
											NewsFeed.put("fullName", result.getString(2));
											NewsFeed.put("sender", result.getString(3));
											NewsFeed.put("receiver", result.getString(4));
											NewsFeed.put("message", result.getString(5));
											NewsFeedList.add(NewsFeed);
									}
									database.close();
									//if(NewsFeedList.size()==0)
										//return "0";
									NewsFeedObj.put("news", NewsFeedList);
									System.err.println("NewsFeed: " + NewsFeedObj.toString());
									return NewsFeedObj.toString();
									
								}
								catch (ParseException e) {
								// TODO Auto-generated catch block
									database.close();
									System.err.println(e.getMessage());
									return "-2"; 
								}
								 catch(Exception e)
								{
									 database.close();
									 System.err.println(e.getMessage());
									 return "-3";
								}
							  }
						//Done by Yichao
						//Fixed by Vidi
						  @GET
							@Path("/getHealthConditions")
							@Consumes(MediaType.APPLICATION_JSON)
							public String getHealthConditions()
							{
								JSONArray jsonArray = new JSONArray();
								JSONObject HealthConditions = new JSONObject();
								
								Db database = new Db();
								try {
									
									database.open("iNutriCare");
									

							  		ResultSet selectR = database.query("select * from health_conditions");
									while(selectR.next())
									{
										long id= selectR.getLong("id");
										String name= selectR.getString("name");
										JSONObject jsonObj = new JSONObject();
										
						     		  	jsonObj.put("id", id);
						      			jsonObj.put("name", name);
										
										jsonArray.add(jsonObj);

									}
									
									HealthConditions.put("HealthConditions", jsonArray);
									// look like {"HealthConditions":[{"id":"0000","name":"abc0"},{"id":"0001","name":"abc1"},{"id":"0002","name":"abc2"},{"id":"0003","name":"abc3"},{"id":"0004","name":"abc4"}]}
									
								}
								catch(Exception e)
								{
									database.close();
									System.out.println("2"+e.getMessage());
									JSONObject error = new JSONObject();
									error.put("error", e.getMessage());
									return error.toString(); //Database level Error
								}
								//Here we close the connection to the database
								database.close();
								//System.out.println("3");
								//System.out.println("HealthConditions:" + HealthConditions);
							    return HealthConditions.toString();
							   }
						  
						//Done by Yichao
						//Fixed by Vidi
							@SuppressWarnings("unchecked")
							@GET
							@Path("/getAllergies")
							@Consumes(MediaType.APPLICATION_JSON)
							public String getAllergies()
							{
								JSONArray jsonArray = new JSONArray();
								JSONObject Allergies = new JSONObject();
								Db database = new Db();
								try {
									
									database.open("iNutriCare");
									

							  		ResultSet selectR = database.query("select * from allergies");
									while(selectR.next())
									{
										long aid= selectR.getLong("aid");
										String name= selectR.getString("name");
										JSONObject jsonObj = new JSONObject();
										
						     		  	jsonObj.put("aid", aid);
						      			jsonObj.put("name", name);
										
										jsonArray.add(jsonObj);

									}
									
									Allergies.put("allergies", jsonArray);
									// look like {"allergies":[{"aid":"0000","name":"abc0"},{"aid":"0001","name":"abc1"},{"aid":"0002","name":"abc2"},{"aid":"0003","name":"abc3"},{"aid":"0004","name":"abc4"}]}
									
								}
								catch(Exception e)
								{
									database.close();
									JSONObject error = new JSONObject();
									error.put("error", e.getMessage());
									return error.toString(); //Database level Error
								}
								//Here we close the connection to the database
								database.close();
								//System.out.println("3");
								System.out.println("Allergies:" + Allergies);
							    return Allergies.toString();
							    }
							
							//Done by Yichao
							//Fixed by Vidi
							  @POST
							  @Path("/addpatient")
							  @Consumes(MediaType.APPLICATION_JSON)
							  public String addPatient(String jsonInput) 
							  {
							  	JSONParser parser = new JSONParser();
							  	int id=0;
								  Object parsedJson;
								  // if add patirnt successfull, addP==1
								  int addP = 0;
								  //if insert table"rel_patient_caregiver" successfull, inRalPC==1
								  int upRalPC = 0;
								  //if insert table"rel_patient_allergies" successfull, inRalPA==1
								  int inRalPA = 0;
								  // if insert table"rel_patient_health" successfull, inRalPH==1
								  int inRalPH = 0;
								  Db database = new Db();
								try {
									//Parse the input string
									parsedJson = parser.parse(jsonInput);
									//Finally we get our json string parsed as json object in obj
									JSONObject obj = (JSONObject) parsedJson;
									//connect to iNutriCare database in mySql Server
									database.open("iNutriCare");
									
									//Check if userrname not exist
									ResultSet Check = database.query("select * from patient where username='"+obj.get("username")+"'");
									if(Check.first()) return "userrname is exist";
									
									
									addP = database.updateQuery("insert into patient (username, password, firstName, lastName, preferredname, DOB, street, postcode, city, state, phonenumber, nextkinname, relationship, kin_street, kin_postcode, kin_city, kin_state, kin_phone, MMSE, livingstatus, careplan)"
									+" values('"+obj.get("username")+"',"
									+"'"+obj.get("password")+"',"
									+"'"+obj.get("firstName")+"',"
									+"'"+obj.get("lastName")+"',"
									+"'"+obj.get("preferredname")+"',"
									+"'"+obj.get("DOB")+"',"
									+"'"+obj.get("street")+"',"
									+"'"+obj.get("postcode")+"',"
									+"'"+obj.get("city")+"',"
									+"'"+obj.get("state")+"',"
									+"'"+obj.get("phonenumber")+"',"
									+"'"+obj.get("nextkinname")+"',"
									+"'"+obj.get("relationship")+"',"
									+"'"+obj.get("kin_street")+"',"
									+"'"+obj.get("kin_postcode")+"',"
									+"'"+obj.get("kin_city")+"',"
									+"'"+obj.get("kin_state")+"',"
									+"'"+obj.get("kin_phone")+"',"
									+"'"+obj.get("MMSE")+"',"
									+"'"+obj.get("livingstatus")+"',"
									+"'"+obj.get("careplan")+"')");
									
									if(addP==1)
									{
										ResultSet selectR = database.query("select pid from patient where username='"+obj.get("username")+"'");
										while(selectR.next())
										{
											id=selectR.getInt("pid");
										}
										// add that pid to the rel_table of caregiver and patient (pid with cid)
										upRalPC = database.updateQuery("insert into rel_patient_caregiver (pid, cid)"
										+" values('"+id+"',"
										+"'"+obj.get("cid")+"')");
										
										//if the patient has allergies add his pid to the rel_table with allergies
										//NOTE: if the patient has more then one allergies, this line code should change.
										//Parse the allergies in JSONARRAY
										String alString= obj.get("allergies").toString();    // allergies String from json input , key should be "allergies"
										Object alObj=parser.parse(alString);				// allergies obj
										JSONArray alArray=(JSONArray)alObj;					//alArray look like      [{"name":"abc0","aid":"0000"},{"name":"abc1","aid":"0001"},{"name":"abc2","aid":"0002"}]
										
										//check that how many allergies need to input on database   alArray.size()
										if(alArray.size()>0)
										{
											for(int i=0;i<alArray.size();i++)
											{
												String temp=alArray.get(i).toString();
												Object tempAlObj=parser.parse(temp);
												JSONObject jAllergy = (JSONObject) tempAlObj;   //jAllergy look like {"name":"abc0","aid":"0000"}  So, jAllergy.get("aid")  will look like "0000"
												
												inRalPA = database.updateQuery("insert into rel_patient_allergies (pid, aid)"
													+" values('"+id+"',"
													+"'"+jAllergy.get("aid")+"')");
											}
										}
										
										
										
										//update the patient health condition in relationship table
										String hcString= obj.get("healthCondition").toString();      // health condition  String from json input , key should be "healthCondition"
										Object hcObj=parser.parse(hcString);	
										JSONArray hcArray=(JSONArray)hcObj;	
										
										//check that how many allergies need to input on database   alArray.size()
										if(hcArray.size()>0)
										{
											for(int i=0;i<hcArray.size();i++)
											{
												String temp=hcArray.get(i).toString();
												Object tempHCObj=parser.parse(temp);
												JSONObject jHealthCondition = (JSONObject) tempHCObj;        //look like {"name":"abc0","hid":"0000"}  So, jHealthCondition.get("hid")  will look like "0000"
												
												inRalPH = database.updateQuery("insert into rel_patient_health (pid, hid)"
													+" values('"+id+"',"
													+"'"+jHealthCondition.get("hid")+"')");
											}
										}
										
									}
									
								} 
								catch (ParseException e) {
									// TODO Auto-generated catch block
									database.close();
									return "-2"; //Invalid Input format "Invalid JSON input"
								} catch(Exception e)
								{
									database.close();
									return Integer.toString(id); //Database level Error
								}
								//Here we close the connection to the database
								database.close();
							    return "1";
								
							  }

							  //Done by Vidi
							  @GET
								@Path("/getPatients/{cid}")
								@Consumes(MediaType.APPLICATION_JSON)
								public String getPatients(@PathParam("cid")String cid)
								{
									JSONArray jsonArray = new JSONArray();
									JSONObject Patients = new JSONObject();
									
									Db database = new Db();
									try {
										
										database.open("iNutriCare");
										

								  		ResultSet selectR = database.query("select * from patient p,rel_patient_caregiver r where p.pid=r.pid and r.cid="+cid);
										while(selectR.next())
										{
											JSONObject jsonObj = new JSONObject();
							     		  	jsonObj.put("pid", selectR.getInt("pid"));
							      			jsonObj.put("username", selectR.getString("username"));
							      			jsonObj.put("password", selectR.getString("password"));
							      			jsonObj.put("firstName", selectR.getString("firstName"));
							      			jsonObj.put("lastName", selectR.getString("lastName"));
							      			jsonObj.put("preferredname", selectR.getString("preferredname"));
							      			jsonObj.put("DOB", selectR.getString("DOB"));
							      			jsonObj.put("street", selectR.getString("street"));
							      			jsonObj.put("postcode", selectR.getString("postcode"));
							      			jsonObj.put("city", selectR.getString("city"));
							      			jsonObj.put("state", selectR.getString("state"));
							      			jsonObj.put("phonenumber", selectR.getString("phonenumber"));
							      			jsonObj.put("nextkinname", selectR.getString("nextkinname"));
							      			jsonObj.put("relationship", selectR.getString("relationship"));
							      			jsonObj.put("kin_street", selectR.getString("kin_street"));
							      			jsonObj.put("kin_postcode", selectR.getString("kin_postcode"));
							      			jsonObj.put("kin_city", selectR.getString("kin_city"));
							      			jsonObj.put("kin_state", selectR.getString("kin_state"));
							      			jsonObj.put("kin_phone", selectR.getString("kin_phone"));
											jsonArray.add(jsonObj);

										}
										
										Patients.put("Patients", jsonArray);
										// look like {"HealthConditions":[{"id":"0000","name":"abc0"},{"id":"0001","name":"abc1"},{"id":"0002","name":"abc2"},{"id":"0003","name":"abc3"},{"id":"0004","name":"abc4"}]}
										
									}
									catch(Exception e)
									{
										database.close();
										JSONObject error = new JSONObject();
										error.put("error", e.getMessage());
										return error.toString(); //Database level Error
									}
									//Here we close the connection to the database
									database.close();
									//System.out.println("3");
									//System.out.println("HealthConditions:" + HealthConditions);
								    return Patients.toString();
								   }
							  
							//Done by YiChao
							//Fixed by Vidi
							    //@SuppressWarnings("unchecked")
								@GET
								@Path("/getpatient/{pid}")
								//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
								public String getpatient(@PathParam("pid")String pid){
									
							    	try {
							    	    Thread.sleep(5000);                 //1000 milliseconds is one second.
							    	} catch(InterruptedException ex) {
							    	    Thread.currentThread().interrupt();
							    	}
							    	
									Db database = new Db();
									System.err.println(pid);
									JSONObject patientObj = new JSONObject();
									JSONArray allergyList = new JSONArray();
									JSONArray healconList = new JSONArray();
									JSONObject error = new JSONObject();
									try{
										
										//connect to iNutriCare database in mySql Server
										database.open("iNutriCare");
													
										ResultSet result = database.query("SELECT * from patient where pid='"+pid+"'");
										if(result.next()){
											patientObj.put("pid", result.getInt(1));
											patientObj.put("username", result.getString(2));
											patientObj.put("password", result.getString(3));
											patientObj.put("firstName", result.getString(4));
											patientObj.put("lastName", result.getString(5));
											patientObj.put("preferredname", result.getString(6));
											patientObj.put("DOB", result.getString(7));	
											patientObj.put("street", result.getString(8));
											patientObj.put("postcode", result.getString(9));
											patientObj.put("city", result.getString(10));	
											patientObj.put("state", result.getString(11));
											patientObj.put("phonenumber", result.getString(12));
											patientObj.put("nextkinname", result.getString(13));	
											patientObj.put("relationship", result.getString(14));
											patientObj.put("kin_street", result.getString(15));
											patientObj.put("kin_postcode", result.getString(16));	
											patientObj.put("kin_city", result.getString(17));
											patientObj.put("kin_state", result.getString(18));
											patientObj.put("kin_phone", result.getString(19));	
											patientObj.put("MMSE", result.getString(20));
											patientObj.put("livingstatus", result.getString(21));
											patientObj.put("careplan", result.getString(22));
										}
											ResultSet relAllergy = database.query("SELECT a.aid,a.name from allergies a, rel_patient_allergies r where r.pid='"+pid+"' and a.aid=r.aid");
											while(relAllergy.next())
											{
												JSONObject allObj = new JSONObject();
												allObj.put("aid", relAllergy.getString("aid"));
					    						allObj.put("name", relAllergy.getString("name"));
												allergyList.add(allObj);						
											}
											patientObj.put("allergies", allergyList);
											
											ResultSet relhealthcon = database.query("SELECT h.id,h.name from health_conditions h,rel_patient_health r where r.pid='"+pid+"' and h.id=r.hid");
											while(relhealthcon.next())
											{
												JSONObject healthconObj = new JSONObject();
												healthconObj.put("hid", relhealthcon.getString("id"));
					    						healthconObj.put("name", relhealthcon.getString("name"));
												healconList.add(healthconObj);						
											}
											patientObj.put("healthconditions", healconList);
										}
										catch(Exception e)
											{
												error.put("error", e.getMessage());
												database.close();
												return error.toString(); //Database level Error
											}
											//Here we close the connection to the database
											database.close();
											//System.out.println("3");
										    return patientObj.toString();
										    }
								 
								//Done by YiChao
								//Fixed by Vidi
								  @POST
								  @Path("/editpatient")
								  @Consumes(MediaType.APPLICATION_JSON)
								  public String editPatient(String jsonInput) 
								  {
								  	JSONParser parser = new JSONParser();
								  	Object parsedJson;
								  	int updataP = 0;
								  	Db database = new Db();
								  	try{
								  		parsedJson = parser.parse(jsonInput);
								  		JSONObject obj = (JSONObject) parsedJson;
								  		database.open("iNutriCare");

										// First,checking patient had been registered by using pid.
										// update patient information..
										updataP = database.updateQuery("update patient set username='"+obj.get("username")
								  				+"', password='"+obj.get("password")
								  				+"', firstName='"+obj.get("firstName")
								  				+"', lastName='"+obj.get("lastName")
								  				+"', preferredname='"+obj.get("preferredname")
								  				+"', DOB='"+obj.get("DOB")
								  				+"', street='"+obj.get("street")
								  				+"', postcode='"+obj.get("postcode")
												+"', city='"+obj.get("city")
												+"', state='"+obj.get("state")
								  				+"', phonenumber='"+obj.get("phonenumber")
								  				+"', nextkinname='"+obj.get("nextkinname")
								  				+"', relationship='"+obj.get("relationship")
								  				+"', kin_street='"+obj.get("kin_street")
												+"', kin_postcode='"+obj.get("kin_postcode")
								  				+"', kin_city='"+obj.get("kin_city")
								  				+"', kin_state='"+obj.get("kin_state")
								  				+"', kin_phone='"+obj.get("kin_phone")
								  				+"', MMSE='"+obj.get("MMSE")
												+"', livingstatus='"+obj.get("livingstatus")
												+"', careplan='"+obj.get("careplan")
								  				+"' where pid="+obj.get("pid"));
										// delete all of list allergies by pid
										database.updateQuery("delete from rel_patient_allergies where pid='"+obj.get("pid")+"'");	
										
										//insert new list of allergies from jsonArray
										String alString= obj.get("allergies").toString();
										Object alObj=parser.parse(alString);				// allergies obj
										JSONArray alArray=(JSONArray)alObj;					//alArray look like      [{"name":"abc0","aid":"0000"},{"name":"abc1","aid":"0001"},{"name":"abc2","aid":"0002"}]
								
										//check that how many allergies need to input on database   alArray.size()
										if(alArray.size()>0)
										{
											for(int i=0;i<alArray.size();i++)
											{
												String temp=alArray.get(i).toString();
												Object tempAlObj=parser.parse(temp);
												JSONObject jAllergy = (JSONObject) tempAlObj;   //jAllergy look like {"name":"abc0","aid":"0000"}  So, jAllergy.get("aid")  will look like "0000"
													
												database.updateQuery("insert into rel_patient_allergies (pid, aid)"
													+" values('"+obj.get("pid")+"',"
													+"'"+jAllergy.get("aid")+"')");
											}
										}
										
										// delete all of list health conditions by pid
										database.updateQuery("delete from rel_patient_health where pid='"+obj.get("pid")+"'");
										
										//insert new list of health conditions from jsonArray
										String hcString= obj.get("healthCondition").toString();
										Object hcObj=parser.parse(hcString);	
										JSONArray hcArray=(JSONArray)hcObj;	
											
										//check that how many allergies need to input on database   alArray.size()
										if(hcArray.size()>0)
										{
											for(int i=0;i<hcArray.size();i++)
											{
													String temp=hcArray.get(i).toString();
													Object tempHCObj=parser.parse(temp);
													JSONObject jHealthCondition = (JSONObject) tempHCObj;        //look like {"name":"abc0","hid":"0000"}  So, jHealthCondition.get("hid")  will look like "0000"
													
													database.updateQuery("insert into rel_patient_health (pid, hid)"
														+" values('"+obj.get("pid")+"',"
														+"'"+jHealthCondition.get("hid")+"')");
										
											}
										
										}
									}
									catch (ParseException e) {
										// TODO Auto-generated catch block
										database.close();
										return "-2"; //Invalid Input format "Invalid JSON input"
									} catch(Exception e)
									{
										database.close();
										return e.getMessage(); //Database level Error
									}
									//Here we close the connection to the database
									database.close();
								    return "1";
								    }
								  
								  
								  //Done by Yichao
								  @POST
								  @Path("/deletepatient")
								  @Consumes(MediaType.APPLICATION_JSON)
								  public String deletepatient(String jsonInput) 
								  {
								  	JSONParser parser = new JSONParser();
									  Object parsedJson;
									  int result = 0;
									  int relPC = 0;
									  Db database = new Db();
									try {
										//Parse the input string
										parsedJson = parser.parse(jsonInput);
										//Finally we get our json string parsed as json object in obj
										JSONObject obj = (JSONObject) parsedJson;
										//connect to iNutriCare database in mySql Server
										database.open("iNutriCare");
										
										
										// First,checking patient had been added in patient table by using pid.
											//delete patient from rel_patient_caregiver
											relPC = database.updateQuery("delete from rel_patient_caregiver where pid='"+obj.get("pid")+"'"+"AND cid='"+obj.get("cid")+"'");
									}
									catch (ParseException e) {
										// TODO Auto-generated catch block
										database.close();
										//System.out.println("1");
										return "-2"; //Invalid Input format "Invalid JSON input"
									} catch(Exception e)
									{
										database.close();
										//System.out.println("2");
										return e.getMessage(); //Database level Error
									}
									//Here we close the connection to the database
									database.close();
									//System.out.println("3");
								    return relPC+"";
								    }
								  
								//Done by Vidi
								  @SuppressWarnings("unchecked")
									@GET
									@Path("/getexistpatient/{cid}")
									@Consumes(MediaType.APPLICATION_JSON)
									public String getAllergies(@PathParam("cid")String cid)
									{
										JSONArray jsonArray = new JSONArray();
										JSONObject Patients = new JSONObject();
										Db database = new Db();
										try {
											
											database.open("iNutriCare");
											

									  		ResultSet selectR = database.query("SELECT p.pid, concat(p.firstName,'  ',p.lastName) AS name from patient p left join rel_patient_caregiver r on r.pid=p.pid where r.pid is NULL or r.cid<>'"+cid+"'");
											while(selectR.next())
											{
												long pid= Long.parseLong(Integer.toString(selectR.getInt("pid")));
												String name= selectR.getString("name");
												JSONObject jsonObj = new JSONObject();
												
								     		  	jsonObj.put("pid", pid);
								      			jsonObj.put("name", name);
												
												jsonArray.add(jsonObj);

											}
											
											Patients.put("patients", jsonArray);
											
										}
										catch(Exception e)
										{
											database.close();
											JSONObject error = new JSONObject();
											error.put("error", e.getMessage());
											return error.toString(); //Database level Error
										}
										//Here we close the connection to the database
										database.close();
										//System.out.println("3");
									    return Patients.toString();
									    }
								  
								//Done by Vidi
								  @POST
								  @Path("/addexistpatient")
								  @Consumes(MediaType.APPLICATION_JSON)
								  public String addexistpatient(String jsonInput) 
								  {
								  	JSONParser parser = new JSONParser();
								  	int id=0;
									  Object parsedJson;
									  //if insert table"rel_patient_caregiver" successfull, inRalPC==1
									  int upRalPC = 0;
									  Db database = new Db();
									try {
										//Parse the input string
										parsedJson = parser.parse(jsonInput);
										//Finally we get our json string parsed as json object in obj
										JSONObject obj = (JSONObject) parsedJson;
										//connect to iNutriCare database in mySql Server
										database.open("iNutriCare");
											String alString= obj.get("patients").toString();    
											Object alObj=parser.parse(alString);				
											JSONArray alArray=(JSONArray)alObj;					
											String cid=obj.get("cid").toString();
										
											if(alArray.size()>0)
											{
												for(int i=0;i<alArray.size();i++)
												{
													String temp=alArray.get(i).toString();
													Object tempAlObj=parser.parse(temp);
													JSONObject jPatient = (JSONObject) tempAlObj;   			
													upRalPC = database.updateQuery("insert into rel_patient_caregiver (pid, cid)"
															+" values('"+jPatient.get("pid")+"',"
															+"'"+cid+"')");
												}
											}
											
									} 
									catch (ParseException e) {
										// TODO Auto-generated catch block
										database.close();
										return "-2"; //Invalid Input format "Invalid JSON input"
									} catch(Exception e)
									{
										database.close();
										return Integer.toString(id); //Database level Error
									}
									//Here we close the connection to the database
									database.close();
								    return "1";
									
								  }
								  //update Consumption---------------------------------Draft
								    @POST
									@Path("/updateConsumptionDraft")
									@Consumes(MediaType.APPLICATION_JSON)
									public String updateConsumption_Draft(String jsonInput) 
									{
										JSONParser parser = new JSONParser();
										Object parsedJson;
										int result = 0;
										Db database = new Db();
										
									try{
											parsedJson = parser.parse(jsonInput);
											JSONObject obj = (JSONObject) parsedJson;
											database.open("iNutriCare");
											long mid=0;
											long fid=0;
											long calories=0;
											calories=(long) obj.get("calories");
											mid= (long) obj.get("mid");
											fid= (long) obj.get("fid");
											result = database.updateQuery("insert into meal_to_food (mid, fid, calories) values('"
														+mid+"', '"
														+fid+"', '"
														+calories+"')");
											
									
										}
										catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											System.out.println("1");
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											System.out.println("2");
											return e.getMessage(); //Database level Error
										}
										//Here we close the connection to the database
										database.close();
										System.out.println("3");
									    return result+"";
								}
								    
								    //Done by Abdulaziz
								    @SuppressWarnings("unchecked")
									@GET
									@Path("/login/{username}/{password}")
									//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
									public String login(@PathParam("username")String username, @PathParam("password")String password){
										
										Db database = new Db();
										System.err.println(username + ",   " + password);
										try{

											
											//connect to iNutriCare database in mySql Server
											database.open("iNutriCare");
														
											ResultSet rs = database.query("select cid, firstName from caregiver where username='"+username+"'and password='"+password+"'");
											if(!rs.next())
											{
												return "-3";// invalid us or password
											}
											
											String cid = rs.getString(1);
											String fn = rs.getString(2);
											
											JSONObject result = new JSONObject();
											result.put("cid", cid);
											result.put("fName", fn);
											
											return result.toString();
											
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											return "-1"; //Database level Error
										}
									  }
								    
								    //Done by Abdulaziz
								    @SuppressWarnings("unchecked")
									@GET
									@Path("/patientList/{cid}")
									//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
									public String patientList(@PathParam("cid")String cid){
										
								    	try {
								    	    Thread.sleep(2000);                 //1000 milliseconds is one second.
								    	} catch(InterruptedException ex) {
								    	    Thread.currentThread().interrupt();
								    	}
								    	
										Db database = new Db();
										System.err.println(cid+"hi");
										try{

											
											//connect to iNutriCare database in mySql Server
											database.open("iNutriCare");
														
											ResultSet rs = database.query("SELECT pat.pid, pat.firstName, pat.lastName"
											        +" FROM inutricare.patient as pat"
													+", inutricare.caregiver as care"
													+", inutricare.rel_patient_caregiver as rel"
													+" where pat.pid = rel.pid and care.cid = rel.cid and care.cid = "+cid);
											
											JSONObject root = new JSONObject();
											JSONArray patientList = new JSONArray();
											//rs.first();
											while(rs.next())
											{
												JSONObject patient = new JSONObject();
												patient.put("pid", rs.getString(1));
												patient.put("fullName", rs.getString(2) + ", " + rs.getString(3));
												patientList.add(patient);
											}
											root.put("patients", patientList);
											
											if(patientList.size() == 0)
												return "0";
											return root.toString();
											
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											return "-3"; //Database level Error
										}
									  }
								    
								    
								    //Done by Abdulaziz, Testing version not the final one
								    //Fixed by Vidi
								    @SuppressWarnings("unchecked")
									@GET
									@Path("/getFoods/{pid}")
									//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
									public String getFoods(@PathParam("pid")String pid){
										
								    	try {
								    	    Thread.sleep(5000);                 //1000 milliseconds is one second.
								    	} catch(InterruptedException ex) {
								    	    Thread.currentThread().interrupt();
								    	}
								    	
										Db database = new Db();
										System.err.println(pid);
										try{

											
											//connect to iNutriCare database in mySql Server
											database.open("iNutriCare");
														
											ResultSet rs = database.query("select distinct(fid), name from fooditem where fid NOT IN (SELECT distinct(r1.fid) "
													+ "from rel_food_ingrediants r1,rel_allergies_ingrediants r2,rel_health_ingrediant r3,rel_patient_allergies r4,"
													+ "rel_patient_health r5, rel_ingredients_patient r6 where ((r1.iid=r2.iid and r2.aid=r4.aid and r4.pid='"+pid+"') or "
													+ "(r1.iid=r3.iid  and r3.hid=r5.hid  and r5.pid='"+pid+"') or (r1.iid=r6.iid and r6.pid='"+pid+"')))");
											
											JSONObject root = new JSONObject();
											JSONArray foodList = new JSONArray();
											//rs.first();
											while(rs.next())
											{
												JSONObject food = new JSONObject();
												food.put("fid", rs.getString(1));
												food.put("name", rs.getString(2));
												foodList.add(food);
											}
											root.put("foods", foodList);
											
											if(foodList.size() == 0)
												return "0";
											return root.toString();
											
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											return "-3"; //Database level Error
										}
									  }
								    
								    //Done by Abdulaziz
								    @SuppressWarnings("unchecked")
									@GET
									@Path("/deleteMeal/{mid}")
									//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
									public String deleteMeal(@PathParam("mid")String mid){
										
								    	try {
								    	    Thread.sleep(5000);                 //1000 milliseconds is one second.
								    	} catch(InterruptedException ex) {
								    	    Thread.currentThread().interrupt();
								    	}
								    	
										Db database = new Db();
										System.err.println(mid);
										try{

											
											//connect to iNutriCare database in mySql Server
											database.open("iNutriCare");
														
											
											int rows2 = database.updateQuery("delete from rel_food_consumption where mid ='"+mid+"'");
											int rows = database.updateQuery("delete from meal where mid ='"+mid+"'");
											System.out.println("Rows = " + rows +", rows2 = " + rows2);
											if(rows + rows2 > 0)
												return "1";
											return "-1";
											
											
											
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											System.out.println(e.getMessage());
											return "-3"; //Database level Error
										}
									  }
								    
								    //Done by Chris
								    
								    @POST
									@Path("/changepassword")
									@Consumes(MediaType.APPLICATION_JSON)
									public String changepassword(String jsonInput)
								        {
											JSONParser parser = new JSONParser();
											Object parsedJson;
											Db database = new Db();
											int st;
								            System.err.println(jsonInput);
								            try 
								            {
								            	database.open("iNutriCare");
								                parsedJson = parser.parse(jsonInput);
								    			//Finally we get our json string parsed as json object in obj
								    			JSONObject obj = (JSONObject) parsedJson;
								                String id = obj.get("cid").toString();
								    			String pass = obj.get("password").toString();
								    			String oldPass = obj.get("oldPassword").toString();

								    			int count2 = 0;
								    			ResultSet count = database.query("select count(*) from caregiver where cid ='"+id+"' and  password = '"+oldPass+"'");

								    			while(count.next())
								    			{
								    				count2 = count.getInt(1);
								    			}
								    			if(count2 > 0)
								    			{
								    				st = database.updateQuery("update caregiver set password = '"+pass+"' where cid = '"+id+"'");
								    				database.close();
								    				return "1";
								    			}
								    			database.close();
								    			return "-3"; // Old password don't match
								                
								            } catch (ParseException e) {
								    			// TODO Auto-generated catch block
								    			database.close();
								    			return "-2"; //Invalid Input format "Invalid JSON input"
								            }  catch(Exception e)
								    		{
								    			database.close();
								    			return "-1"; //Database level Error
								    		}
								        }
								    
								    
								      // Done by chris first version
								      // Fix some issues by Abdulaziz
									  @POST
									  @Path("/addmeal")
									  @Consumes(MediaType.APPLICATION_JSON)
									  public String addMeal(String jsonInput) 
									  {
									  	JSONParser parser = new JSONParser();
										  Object parsedJson;
										  
										  int addMeal = 0;
										  Db database = new Db();
										  
										  System.err.println(jsonInput);
										  
										try {
											//Parse the input string
											parsedJson = parser.parse(jsonInput);
											//Finally we get our json string parsed as json object in obj
											JSONObject obj = (JSONObject) parsedJson;
											//connect to iNutriCare database in mySql Server
											database.open("iNutriCare");
												
											addMeal = database.updateQuery("insert into meal (pid, mealtype, meal_time, prepare_time, alert_time, comment)"
													+" values('"+obj.get("pid")+"',"
													+"'"+obj.get("mealtype")+"',"
													+"'"+obj.get("meal_time")+"',"
													+"'"+obj.get("prepare_time")+"',"
													+"'"+obj.get("alert_time")+"',"
													+"'"+obj.get("comment")+"')");
											
											if(addMeal==1)
											{
												ResultSet selectR = database.query("select MAX(mid) from meal");
												selectR.next();
												String mid = selectR.getString(1);
												System.err.println(mid);
												JSONArray foodsArray = (JSONArray) obj.get("foods");
												for(Object fid : foodsArray)
												{
													database.updateQuery("insert into rel_food_consumption (mid, fid)"
															+" values('"+mid+"', '"+fid+"')");
												}
												
												return "1";
											}
											
										}	
									
										catch (ParseException e) {
											// TODO Auto-generated catch block
											database.close();
											return "-2"; //Invalid Input format "Invalid JSON input"
										} catch(Exception e)
										{
											database.close();
											return e.getMessage(); //Database level Error
										}
										database.close();
										return "-3";
									  }
									  
									  
									  //Done By Yichao
									    @SuppressWarnings("unchecked")
										@GET
										@Path("/futuremeals/{pid}")
										//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
										public String futuremeals(@PathParam("pid")String pid){
											
									    	try {
									    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
									    	} catch(InterruptedException ex) {
									    	    Thread.currentThread().interrupt();
									    	}
									    	
											Db database = new Db();
											System.err.println(pid);
											Date today = new Date();
											//Timestamp current = new Timestamp(today.getTime());
											JSONObject mealsObj = new JSONObject();
											JSONArray mealList = new JSONArray();
											//JSONObject error = new JSONObject();
											try{

												
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
															
												ResultSet result = database.query("SELECT mid, mealtype, meal_time from meal where pid='"+pid+"'"
														+" and meal_time > now()");
												while(result.next())
												{
														JSONObject meal = new JSONObject();
														meal.put("mid", result.getString(1));
														meal.put("type", result.getString(2));
														meal.put("date", result.getString(3));
														mealList.add(meal);
												}
												if(mealList.size()==0)
													return "0";
												mealsObj.put("meals", mealList);
												return mealsObj.toString();
												
											}
											catch (ParseException e) {
											// TODO Auto-generated catch block
												database.close();
												return "-2"; 
											}
											 catch(Exception e)
											{
												 database.close();
												 return "-3";
											}
										  }
									    
									    //getMeal
									    // Done by Abdulaziz
									    @SuppressWarnings("unchecked")
										@GET
										@Path("/getMeal/{mid}")
										//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
										public String getMeal(@PathParam("mid")String mid){
											
									    	try {
									    	    Thread.sleep(2000);                 //1000 milliseconds is one second.
									    	} catch(InterruptedException ex) {
									    	    Thread.currentThread().interrupt();
									    	}
									    	
											Db database = new Db();
											System.err.println(mid);
											try{

												
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
															
												ResultSet rs = database.query("select mealtype, meal_time, prepare_time, alert_time, comment  from meal where mid ='"+mid+"'");
												
												JSONObject root = new JSONObject();
												JSONArray foodList = new JSONArray();
												//rs.first();
												if(rs.next())
												{
													root.put("meal_type", rs.getString(1));
													root.put("meal_date_time", rs.getString(2));
													root.put("prepare_time", rs.getString(3));
													root.put("alert_time", rs.getString(4));
													root.put("comment", rs.getString(5));
													
												}
												else
												{
													return "0";
												}
												
												ResultSet rs2 = database.query("select fi.fid, fi.name from fooditem as fi, rel_food_consumption as rfc"
							                                                  +" where fi.fid = rfc.fid and rfc.mid = '"+mid+"'");
												
												//rs.first();
												while(rs2.next())
												{
													JSONObject food = new JSONObject();
													food.put("fid", rs2.getString(1));
													food.put("name", rs2.getString(2));
													foodList.add(food);
												}
												root.put("foods", foodList);
												
												//if(foodList.size() == 0)
													//return "0";
												return root.toString();
												
											} catch (ParseException e) {
												// TODO Auto-generated catch block
												database.close();
												return "-2"; //Invalid Input format "Invalid JSON input"
											} catch(Exception e)
											{
												database.close();
												return e.getMessage(); //Database level Error
											}
										  }
									    
									    //Done by Chris
									  //editmeal WebService
										  @Path("/editmeal")
										  @POST
										  @Consumes(MediaType.APPLICATION_JSON)
										  public String editmeal(String jsonInput) 
										  {
										  	JSONParser parser = new JSONParser();
											  Object parsedJson;
											  int result = 0;
											  Db database = new Db();
											try {
												//Parse the input string
												parsedJson = parser.parse(jsonInput);
												//Finally we get our json string parsed as json object in obj
												JSONObject obj = (JSONObject) parsedJson;
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
												
												
												// First,checking meal had been added in meal table by using mid.
										  		ResultSet selectR = database.query("select * from meal where mid="+obj.get("mid"));
												if(selectR.next())
												{
													result = database.updateQuery("update meal set"
										  				+" mealtype='"+obj.get("mealtype")
										  				+"', meal_time='"+obj.get("meal_time")
										  				+"', prepare_time='"+obj.get("prepare_time")
										  				+"', alert_time='"+obj.get("alert_time")
										  				+"', comment='"+obj.get("comment")
										  				+"' where mid="+obj.get("mid"));
														
													database.updateQuery("delete from rel_food_consumption where mid='"+obj.get("mid")+"'");

													JSONArray foodsArray = (JSONArray) obj.get("foods");
													
													for(Object fid : foodsArray)
													{
														database.updateQuery("insert into rel_food_consumption (mid, fid)"
																+" values('"+obj.get("mid")+"', '"+fid+"')");
													}
												}
												else
													return "0";
											}
											catch (ParseException e) {
												// TODO Auto-generated catch block
												database.close();
												//System.out.println("1");
												return "-2"; //Invalid Input format "Invalid JSON input"
											} catch(Exception e)
											{
												database.close();
												//System.out.println("2");
												System.out.println(e.getMessage());
												return "-3"; //Database level Error
											}
											//Here we close the connection to the database
											database.close();
											//System.out.println("3");
										    return "1";
										    }
									    
										  //Done by Vidi
										  @POST
										  @Path("/checkusername")
										  @Consumes(MediaType.APPLICATION_JSON)
										  public String checkUsername(String jsonInput) 
										  {
										  	JSONParser parser = new JSONParser();
										  	int id=0;
											  Object parsedJson;
											  Db database = new Db();
											try {
												//Parse the input string
												parsedJson = parser.parse(jsonInput);
												//Finally we get our json string parsed as json object in obj
												JSONObject obj = (JSONObject) parsedJson;
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
												
												//Check if userrname not exist
												ResultSet Check = database.query("select * from patient where username='"+obj.get("username")+"' and pid!='"+obj.get("pid")+"'");
												if(Check.first()) return "2";
											} 
											catch (ParseException e) {
												// TODO Auto-generated catch block
												database.close();
												return "-2"; //Invalid Input format "Invalid JSON input"
											} catch(Exception e)
											{
												database.close();
												return Integer.toString(id); //Database level Error
											}
											//Here we close the connection to the database
											database.close();
										    return "1";
											
										  }
										  
										  
										  //Done by Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getIngredients")
											@Consumes(MediaType.APPLICATION_JSON)
											public String getIngredients()
											{
												JSONArray jsonArray = new JSONArray();
												JSONObject Ingredients = new JSONObject();
												Db database = new Db();
												try {
													
													database.open("iNutriCare");
													

											  		ResultSet selectR = database.query("select * from ingrediants");
													while(selectR.next())
													{
														long iid= selectR.getLong("iid");
														String name= selectR.getString("name");
														JSONObject jsonObj = new JSONObject();
														
										     		  	jsonObj.put("iid", iid);
										      			jsonObj.put("name", name);
														
														jsonArray.add(jsonObj);

													}
													
													Ingredients.put("ingredients", jsonArray);
													// look like {"allergies":[{"aid":"0000","name":"abc0"},{"aid":"0001","name":"abc1"},{"aid":"0002","name":"abc2"},{"aid":"0003","name":"abc3"},{"aid":"0004","name":"abc4"}]}
													
												}
												catch(Exception e)
												{
													database.close();
													JSONObject error = new JSONObject();
													error.put("error", e.getMessage());
													return error.toString(); //Database level Error
												}
												//Here we close the connection to the database
												database.close();
												//System.out.println("3");
											    return Ingredients.toString();
											    }

										  
										  //Done By Vidi
										  @POST
										  @Path("/addfood")
										  @Consumes(MediaType.APPLICATION_JSON)
										  public String addFood(String jsonInput) 
										  {
										  	JSONParser parser = new JSONParser();
										  	int id=0;
											  Object parsedJson;
											  int addP = 0;
											  Db database = new Db();
											try {
												//Parse the input string
												parsedJson = parser.parse(jsonInput);
												//Finally we get our json string parsed as json object in obj
												JSONObject obj = (JSONObject) parsedJson;
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
												
												
												addP = database.updateQuery("insert into fooditem (name, energy, totalCarb, totalProt, totalFat)"
												+" values('"+obj.get("name")+"',"
												+"'"+obj.get("energy")+"',"
												+"'"+obj.get("totalCarb")+"',"
												+"'"+obj.get("totalProt")+"',"
												+"'"+obj.get("totalFat")+"')");
												
												if(addP==1)
												{
													ResultSet selectR = database.query("select MAX(fid) from fooditem");
													while(selectR.next())
													{
														id=selectR.getInt(1);
													}

													JSONArray IngArray = (JSONArray) obj.get("Ingredients");
													for(Object fid : IngArray)
													{
														database.updateQuery("insert into rel_food_ingrediants (fid, iid)"
																+" values('"+id+"', '"+fid+"')");
													}
												}
												
											} 
											catch (ParseException e) {
												// TODO Auto-generated catch block
												database.close();
												return "-2"; //Invalid Input format "Invalid JSON input"
											} catch(Exception e)
											{
												database.close();
												return e.getMessage(); //Database level Error
											}
											//Here we close the connection to the database
											database.close();
										    return "1";
											
										  }
										  
										  
										  //Done by Vidi
										  @GET
											@Path("/getfood/{fid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getfood(@PathParam("fid")String fid){
												
										    	try {
										    	    Thread.sleep(5000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												JSONObject foodObj = new JSONObject();
												JSONArray foodList = new JSONArray();
												JSONObject error = new JSONObject();
												try{
													
													//connect to iNutriCare database in mySql Server
													database.open("iNutriCare");
																
													ResultSet result = database.query("SELECT * from fooditem where fid='"+fid+"'");
													if(result.next()){
														foodObj.put("fid", result.getInt(1));
														foodObj.put("name", result.getString(2));
														foodObj.put("energy", result.getString(3));
														foodObj.put("totalProt", result.getString(4));
														foodObj.put("totalCarb", result.getString(5));
														foodObj.put("totalFat", result.getString(6));
														
													}
														ResultSet relIng = database.query("SELECT i.iid,i.name from ingrediants i, rel_food_ingrediants r where r.fid='"+fid+"' and i.iid=r.iid");
														while(relIng.next())
														{
															JSONObject allObj = new JSONObject();
															allObj.put("iid", relIng.getString("iid"));
								    						allObj.put("name", relIng.getString("name"));
															foodList.add(allObj);						
														}
														foodObj.put("ingredients", foodList);
														
														
													}
													catch(Exception e)
														{
															error.put("error", e.getMessage());
															database.close();
															return error.toString(); //Database level Error
														}
														//Here we close the connection to the database
														database.close();
														//System.out.println("3");
													    return foodObj.toString();
													    }
										  
										  //Done by Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getFoodsList/")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getFoodsList(){
											  JSONObject root = new JSONObject();
										    	
												Db database = new Db();
												try{

													
													//connect to iNutriCare database in mySql Server
													database.open("iNutriCare");
																
													ResultSet rs = database.query("select fid, name from fooditem");
													
													
													JSONArray foodList = new JSONArray();
													//rs.first();
													while(rs.next())
													{
														JSONObject food = new JSONObject();
														food.put("fid", rs.getString(1));
														food.put("name", rs.getString(2));
														foodList.add(food);
													}
													root.put("foods", foodList);
													
													if(foodList.size() == 0)
														return "0";
													
												} catch (ParseException e) {
													// TODO Auto-generated catch block
													database.close();
													return "-2"; //Invalid Input format "Invalid JSON input"
												} catch(Exception e)
												{
													
													return "-3"; //Database level Error
												}
												database.close();
												return root.toString();
											  }
										  
										  //Done By Vidi
										  @GET
											@Path("/getfoodfrommeals/{mid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getfoodfrommeals(@PathParam("mid")String mid){
												
										    	try {
										    	    Thread.sleep(5000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												JSONObject foodObj = new JSONObject();
												JSONArray foodList = new JSONArray();
												JSONObject error = new JSONObject();
												try{
													
													//connect to iNutriCare database in mySql Server
													database.open("iNutriCare");

														ResultSet relFood = database.query("SELECT f.fid,f.name from fooditem f,rel_food_consumption r where r.fid=f.fid and r.mid='"+mid+"'");
														while(relFood.next())
														{
															JSONObject allObj = new JSONObject();
															allObj.put("fid", relFood.getString("fid"));
								    						allObj.put("name", relFood.getString("name"));
															foodList.add(allObj);						
														}
														foodObj.put("foods", foodList);
														
														
													}
													catch(Exception e)
														{
															error.put("error", e.getMessage());
															database.close();
															return error.toString(); //Database level Error
														}
														//Here we close the connection to the database
														database.close();
														//System.out.println("3");
													    return foodObj.toString();
													    }
										  
										  
										  //Done by YiChao
										  @POST
											@Path("/updateConsumption")
											@Consumes(MediaType.APPLICATION_JSON)
											public String updateConsumption(String jsonInput) 
											{// input look like   {mid=1,foods:[{fid=1,calories=100},{fid=1,calories=200},{fid=1,calories=300},{fid=1,calories=400}]}
												JSONParser parser = new JSONParser();  
												Object parsedJson;
												int result = 0;
												Db database = new Db();
												
											try{
													parsedJson = parser.parse(jsonInput);
													JSONObject obj = (JSONObject) parsedJson;
													database.open("iNutriCare");
													String mid=obj.get("mid").toString();
													parsedJson = parser.parse(obj.get("foods").toString());
													JSONArray array=(JSONArray)parsedJson;
													
													
													for(int i=0;i<array.size();i++)
													{
														parsedJson = parser.parse(array.get(i).toString());
														JSONObject values = (JSONObject)parsedJson;
														result = database.updateQuery("update rel_food_consumption set calories='"+values.get("calories")+"'"
																+"where mid='"+mid+"' and fid='"+values.get("fid")+"'");

														
													}

											
												}
												catch (ParseException e) {
													// TODO Auto-generated catch block
													database.close();
													return "-2"; //Invalid Input format "Invalid JSON input"
												} catch(Exception e)
												{
													database.close();
													return e.getMessage(); //Database level Error
												}
												//Here we close the connection to the database
												database.close();
												System.out.println("3");
											    return "1";
											

										} 
										  
										  
										  //Done By Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/todaymeals/{pid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String todaymeals(@PathParam("pid")String pid){
												
										    	try {
										    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												System.err.println("TodayMeals: "+pid);
												Date today = new Date();
												//Timestamp current = new Timestamp(today.getTime());
												JSONObject mealsObj = new JSONObject();
												JSONArray mealList = new JSONArray();
												//JSONObject error = new JSONObject();
												try{

													
													//connect to iNutriCare database in mySql Server
													database.open("iNutriCare");
																
													ResultSet result = database.query("SELECT mid, mealtype, meal_time from meal where pid='"+pid+"'"
															+" and meal_time > CURDATE()");
													while(result.next())
													{
															JSONObject meal = new JSONObject();
															meal.put("mid", result.getString(1));
															meal.put("type", result.getString(2));
															meal.put("date", result.getString(3));
															mealList.add(meal);
													}
													if(mealList.size()==0)
														return "0";
													mealsObj.put("meals", mealList);
													return mealsObj.toString();
													
												}
												catch (ParseException e) {
												// TODO Auto-generated catch block
													database.close();
													return "-2"; 
												}
												 catch(Exception e)
												{
													 database.close();
													 return "-3";
												}
											  }
										  
										  
										//Done By Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getConsumption/{year}/{month}/{pid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getConsumption(@PathParam("pid")String pid,@PathParam("year")String year,@PathParam("month")String month){
												
										    	try {
										    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												//System.err.println(month);
												//Timestamp current = new Timestamp(today.getTime());
												JSONObject consumptionsObj = new JSONObject();
												JSONArray consumptionList = new JSONArray();
												//JSONObject error = new JSONObject();
												try{
													database.open("iNutriCare");
													ResultSet result= database.query("SELECT SUM(calories) as consumption,month(meal_time) as date from rel_food_consumption r right join meal m on m.mid=r.mid  "
															+ "where year(meal_time)='"+year+"' and pid='"+pid+"' and calories is not null group by month(meal_time)");;
													if(!month.equals("0"))
													{
														//System.out.println(month);
														result= database.query("SELECT SUM(calories) as consumption,day(meal_time) as date from rel_food_consumption r right join meal m on m.mid=r.mid  "
																+ "where month(meal_time)='"+month+"' and year(meal_time)='"+year+"' and pid='"+pid+"' and calories is not null group by dayofmonth(meal_time)");
													}
													while(result.next())
													{
															JSONObject consumption = new JSONObject();
															consumption.put("consumption", result.getString(1));
															consumption.put("date", result.getString(2));
															consumptionList.add(consumption);
													}
													if(consumptionList.size()==0)
														return "0";
													consumptionsObj.put("consumptions", consumptionList);
													return consumptionsObj.toString();
													
												}
												catch (ParseException e) {
												// TODO Auto-generated catch block
													database.close();
													return "-2"; 
												}
												 catch(Exception e)
												{
													 database.close();
													 System.out.println(e.getMessage());
													 return e.getMessage();
												}
											  }
										  
										  
										//Done By Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getNewsFeed")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getNewsFeed(){
												
										    	try {
										    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												//Timestamp current = new Timestamp(today.getTime());
												JSONObject NewsFeedObj = new JSONObject();
												JSONArray NewsFeedList = new JSONArray();
												//JSONObject error = new JSONObject();
												try{

													
													//connect to iNutriCare database in mySql Server
													database.open("iNutriCare");
																
													ResultSet result = database.query("SELECT id,concat(p.firstName,' ',p.lastName) as fullName,sender,receiver,msgContent, DATE_FORMAT(msgTime, '%W %D %M %Y %T') msgTime from smartmsgs s,patient p where p.pid=s.pid order by msgTime DESC");
													while(result.next())
													{
															JSONObject NewsFeed = new JSONObject();
															NewsFeed.put("id", result.getString(1));
															NewsFeed.put("fullName", result.getString(2));
															NewsFeed.put("sender", result.getString(3));
															NewsFeed.put("receiver", result.getString(4));
															NewsFeed.put("message", result.getString(5));
															NewsFeedList.add(NewsFeed);
													}
													if(NewsFeedList.size()==0)
														return "0";
													NewsFeedObj.put("news", NewsFeedList);
													return NewsFeedObj.toString();
													
												}
												catch (ParseException e) {
												// TODO Auto-generated catch block
													database.close();
													return "-2"; 
												}
												 catch(Exception e)
												{
													 database.close();
													 return "-3";
												}
											  }
										  
										  //Done by Vidi
										  @POST
										  @Path("/addexistfood")
										  @Consumes(MediaType.APPLICATION_JSON)
										  public String addexistfood(String jsonInput) 
										  {
										  	JSONParser parser = new JSONParser();
											  Object parsedJson;
											  
											  Db database = new Db();
											  
											  System.err.println(jsonInput);
											  
											try {
												//Parse the input string
												parsedJson = parser.parse(jsonInput);
												//Finally we get our json string parsed as json object in obj
												JSONObject obj = (JSONObject) parsedJson;
												//connect to iNutriCare database in mySql Server
												database.open("iNutriCare");
												long mid=(long)obj.get("mid");
												
												JSONArray foodsArray = (JSONArray) obj.get("foods");
												for(int i=0;i<foodsArray.size();i++)
												{
													String temp=foodsArray.get(i).toString();
													Object tempAlObj=parser.parse(temp);
													JSONObject jfood = (JSONObject) tempAlObj; 
													System.out.println(jfood.get("fid").toString());
													
													database.updateQuery("insert into rel_food_consumption (mid, fid)"
																+" values('"+mid+"', '"+jfood.get("fid")+"')");
												}
												database.close();
												return "1";
												
											}	
										
											catch (ParseException e) {
												// TODO Auto-generated catch block
												database.close();
												return "-2"; //Invalid Input format "Invalid JSON input"
											} catch(Exception e)
											{
												database.close();
												return e.getMessage(); //Database level Error
											}
										  }
										  
										//Done By Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getActivity/{year}/{month}/{pid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getActivity(@PathParam("pid")String pid,@PathParam("year")String year,@PathParam("month")String month){
												
										    	try {
										    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												//System.err.println(month);
												//Timestamp current = new Timestamp(today.getTime());
												JSONObject consumptionsObj = new JSONObject();
												JSONArray consumptionList = new JSONArray();
												//JSONObject error = new JSONObject();
												try{
													database.open("iNutriCare");
													ResultSet result= database.query("SELECT count(*) as consumption,month(msgTime) as date from smartmsgs where year(msgTime)='"+year+"' and pid='"+pid+"' group by month(msgTime)");
													if(!month.equals("0"))
													{
														//System.out.println(month);
														result= database.query("SELECT count(*) as consumption,day(msgTime) as date from smartmsgs where month(msgTime)='"+month+"' and year(msgTime)='"+year+"' and pid='"+pid+"' group by dayofmonth(msgTime)");
													}
													while(result.next())
													{
															JSONObject consumption = new JSONObject();
															consumption.put("consumption", result.getString(1));
															consumption.put("date", result.getString(2));
															consumptionList.add(consumption);
													}
													if(consumptionList.size()==0)
														return "0";
													consumptionsObj.put("consumptions", consumptionList);
													return consumptionsObj.toString();
													
												}
												catch (ParseException e) {
												// TODO Auto-generated catch block
													database.close();
													return "-2"; 
												}
												 catch(Exception e)
												{
													 database.close();
													 System.out.println(e.getMessage());
													 return e.getMessage();
												}
											  }
										  
										//Done By Vidi
										  @SuppressWarnings("unchecked")
											@GET
											@Path("/getDetailMeal/{year}/{month}/{position}/{pid}")
											//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
											public String getDetailMeal(@PathParam("pid")String pid,@PathParam("year")String year,@PathParam("month")String month,@PathParam("position")String position){
												
										    	try {
										    	    Thread.sleep(3000);                 //1000 milliseconds is one second.
										    	} catch(InterruptedException ex) {
										    	    Thread.currentThread().interrupt();
										    	}
										    	
												Db database = new Db();
												//System.err.println(month);
												//Timestamp current = new Timestamp(today.getTime());
												JSONObject consumptionsObj = new JSONObject();
												JSONArray consumptionList = new JSONArray();
												//JSONObject error = new JSONObject();
												try{
													database.open("iNutriCare");
													ResultSet result= database.query("SELECT SUM(calories) as consumption,mealtype from rel_food_consumption r right join meal m on m.mid=r.mid  "
															+ "where month(meal_time)='"+position+"' and year(meal_time)='"+year+"' and pid='"+pid+"' group by mealtype");
													if(!month.equals("0"))
													{
														//System.out.println(month);
														result= database.query("SELECT SUM(calories) as consumption,mealtype from rel_food_consumption r right join meal m on m.mid=r.mid  "
																+ "where day(meal_time)='"+position+"' and month(meal_time)='"+month+"' and year(meal_time)='"+year+"' and pid='"+pid+"' group by mealtype");
													}
													while(result.next())
													{
															JSONObject consumption = new JSONObject();
															consumption.put("consumption", result.getString(1));
															consumption.put("mealtype", result.getString(2));
															consumptionList.add(consumption);
													}
													if(consumptionList.size()==0)
														return "0";
													consumptionsObj.put("consumptions", consumptionList);
													return consumptionsObj.toString();
													
												}
												catch (ParseException e) {
												// TODO Auto-generated catch block
													database.close();
													return "-2"; 
												}
												 catch(Exception e)
												{
													 database.close();
													 System.out.println(e.getMessage());
													 return e.getMessage();
												}
											  }
		} 