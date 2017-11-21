package iNutri.care.web.services;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Db {
	
	 public Db (){
	 }
	private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
	 
	 
    public void open(String dbName) throws Exception {
        try {
                // This will load the driver, each DB has its own driver
		        Class.forName("com.mysql.jdbc.Driver").newInstance();
		        
        		// Setup the connection with the DB
        		//jdbc:postgresql://host:port/database
		        String url = "jdbc:mysql://localhost:3306/"+dbName;
		        
		        //Replace the username and password with the ones you sat during mySql installation ---!
		        String username = "root";
		        String password = "admin";
		        
		        Connection connect = DriverManager.getConnection(url, username, password);

		        // Statements allow to issue SQL queries to the database
		        statement = connect.createStatement();
		        
		        

	
        } catch (Exception e) {
            throw e;
          } 

	}

    public ResultSet query(String q) throws SQLException
    {
    	return statement.executeQuery(q);
    
    }
    
    public int updateQuery(String q) throws SQLException
    {
    	return statement.executeUpdate(q);
    }
    // You need to close the resultSet
    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
		statement.close();
            }

            if (connect != null) {
		connect.close();
            }
	} catch (Exception e) {
          }
    }/////////////////////

}
