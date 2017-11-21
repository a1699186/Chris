package iNutri.care.web.services;

public class Meals {
	private int mid;
	private String pname;
	private String mealtype;
	private String mealtime;
	private int ttoprep;
	private String ttoalert;
	private String comment;
	 
	public Meals()
	{
	 
	}
	 
	public Meals(int mid, String pname, String mealtype, String mealtime, int ttoprep, String ttoalert,String comment)
	{
	super();
	this.mid= mid;
	this.pname = pname;
	this.mealtype = mealtype;
	this.mealtime = mealtime;
	this.ttoprep = ttoprep;
	this.ttoalert = ttoalert;
	this.comment = comment;
	}
	 
	public int getId()
	{
	return mid;
	}
	 
	public void setId(int mid)
	{
	this.mid = mid;
	}
	 
	public String getName()
	{
	return pname;
	}
	 
	public void setName(String pname)
	{
	this.pname = pname;
	}
	 
	public String getMealType()
	{
	return mealtype;
	}
	 
	public void setMealType(String mealtype)
	{
	this.mealtype = mealtype;
	}
	
	public String getMealTime()
	{
	return mealtime;
	}
	 
	public void setMealTime(String mealtime)
	{
	this.mealtime = mealtime;
	}
	
	public int getTimeToPrep()
	{
	return ttoprep;
	}
	 
	public void setTimeToPrep(int ttoprep)
	{
	this.ttoprep = ttoprep;
	}
	
	public String getTimeToAlert()
	{
	return ttoalert;
	}
	 
	public void setTimeToAlert(String ttoalert)
	{
	this.ttoalert = ttoalert;
	}
	
	public String getComment()
	{
	return comment;
	}
	 
	public void setComment(String comment)
	{
	this.comment = comment;
	}

}
