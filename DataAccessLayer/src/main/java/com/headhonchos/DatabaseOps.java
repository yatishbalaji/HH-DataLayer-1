/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.headhonchos;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author richa
 */
public class DatabaseOps {
	static String DRIVER = "com.mysql.jdbc.Driver";
	static String DB_CONNECTION_URL = null;
	static String DB_USER = null;
	static String PASSWORD = null;

	//Logger logger = LoggerFactory.getLogger(DatabaseOps.class);
	Connection DB_FAIL=null,DB_URL=null,DB_URL_READ=null,DB_LIVE=null;
	Statement st_fail=null,st_url=null,st_url_read=null,st_live=null;


	public Connection connection=null;
	public Statement statement=null;

	/**
	 *
	 * @param DB_CONNECTION_URL 
	 * @param DB_USER username of database.
	 * @param PASSWORD Password of database.
	 */
	public DatabaseOps(String DB_CONNECTION_URL, String DB_USER, String PASSWORD){
		try {
			//new GlobalInstances();
			Class.forName(GlobalInstances.DRIVER);
			connection = (Connection) DriverManager.getConnection(DB_CONNECTION_URL, DB_USER, PASSWORD);
//            System.out.println("using DB-"+DB_CONNECTION_URL+"@"+DB_USER);
			statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 */
	/*public DatabaseOps(String prefix) {
		Properties props = new Properties();
		try {
			props.load(new FileReader("/opt/java/resume_parser/Embedded_GATE/GlobalFile.txt"));
		} catch (IOException e) {
			System.err.println("property file read error");
		}

		DRIVER = props.getProperty("DRIVER");
		DB_CONNECTION_URL = props.getProperty(prefix+".DB_CONNECTION_URL");
		DB_USER = props.getProperty(prefix+".DB_USER");
		PASSWORD = props.getProperty(prefix+".PASSWORD");

		try {
			Class.forName(DRIVER);
			connection = (Connection) DriverManager.getConnection(DB_CONNECTION_URL, DB_USER, PASSWORD);
			statement =  connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
	}*/

	//TODO change mehtod signature.
	/**
	 *Gets a flag value from database.  
	 *@param id ID of field for which flag value is required. 
	 *@param field column name.
	 *@return flag
	 *@since 10-Apr-2013
	 */
	public boolean getFlag(String id,String table,String field){
		ResultSet rs =null;
		boolean result=false;
		try {
			rs=statement.executeQuery("SELECT "+field+" FROM "+table+" where id="+id);
			rs.next();
			result = rs.getBoolean(field);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *Executes a update,insert or delete query and return a success flag.  
	 *@param query query to be run for updation.
	 *@return if query run successful without any exception and rowsEffected > 0 return true otherwise false.
	 *@since 29-Apr-2013
	 */
	public boolean update(String query){

		int rowsEffected=0;
		try {
			rowsEffected= statement.executeUpdate(query);
		} catch (SQLException e) {
            System.out.println("Database update error for query -- "+query);
			e.printStackTrace();
			return false;
		}
		if(rowsEffected!=0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 *Gets string by executing query.     
	 *@param query sql query to be executed. 
	 *@return first column value of first record obtained by executing the given query OR null if no results were found.
	 *@since 29-Apr-2013
	 */
	public String getString(String query) {
		ResultSet rs =null;
		String result =null;
		try {
			rs=statement.executeQuery(query);
			if(!rs.next()){
				result=null;
			}
			else{
				result=rs.getString(1);
				//System.out.println(result+"---dbout");
			}
			rs.close();
		} catch (SQLException e) {
            System.out.println("Error in executing query -- "+query);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *Checks if a database record exists corresponding to given query.  
	 *@param query query for which existance of records to be found.
	 *@return true if record exists false otherwise.
	 *@since 03-May-2013
	 */
	public boolean recordExists(String query){
		ResultSet rs =null;
		boolean exists=false;
		try {
			rs=statement.executeQuery(query);
			if(!rs.next()){
				exists=false;
			}
			else{
				exists=true;
			}
			rs.close();
		} catch (SQLException e) {
            System.out.println("Error in executing query -- "+query);
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 *Executes a single Sql query using preparedStatement object.
	 *@return If executed successfully then true otherwise false.
	 *@since 29-Apr-2013
	 */
	public boolean pstUpdate(PreparedStatement pst, String[] values) {
		boolean success=false;
		try {
			for(int i=0;i<values.length;i++){
				pst.setString(i+1, values[i]);
			}
			try{
			pst.executeUpdate();
			}catch(Exception e)
			{
				System.out.println("query not executed");
				System.out.println(pst);
                e.printStackTrace();
			}
			success=true;
		} catch (Exception e) {
            System.out.println("failed execution of writing results to database."+e.getMessage());
			success=false;
		}
		return success;
	}

	/**
	 *Closes the database onject properly.  
	 *@return true if successful false otherwise. 
	 *@since 29-Apr-2013
	 */
	public boolean close(){
		try {
			connection.close();
			if(statement!=null)
				statement.close();
			if(st_fail!=null)
				st_fail.close();
			if(st_live!=null)
				st_live.close();
			if(st_url!=null)
				st_url.close();
			if(st_url_read!=null)
				st_url_read.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *Gets results by executing given query.  
	 *@param query query to be executed.
	 *@param expectedNumberOfResults Number of results Expected by executing this query.
	 *@return an ArrayList of results.
	 *@since 03-May-2013
	 */
	public List<String> getStrings(String query,int expectedNumberOfResults){
		ResultSet rs =null;
		List<String> result =new ArrayList<String>(expectedNumberOfResults);
		try {
			rs=statement.executeQuery(query);
			while(rs.next()){	
				result.add(rs.getString(1));
			}
			rs.close();
		} catch (SQLException e) {
            System.out.println("Error in executing query -- "+query);
			e.printStackTrace();
		}
		return result;
	}

	public List<String> getAllStrings(String query, int noOfColumns){
        ResultSet rs =null;
		List<String> result =new ArrayList<String>();
		try {
			rs=statement.executeQuery(query);
			while(rs.next()){
				for(int i=1;i<=noOfColumns;i++)
					result.add(rs.getString(i));
			}
			rs.close();
		} catch (SQLException e) {
            System.out.println("Error in executing query -- "+query);
            //System.out.println("Error - "+query);
            e.printStackTrace();
		}
        catch(Exception e){
            System.out.println("Error in executing query -- "+query);
        }
		return result;
	}

	/**
	 *Creates a {@link PreparedStatement} object.  
	 *@param query query for which {@link PreparedStatement} object to be created.
	 *@return {@link PreparedStatement} a PreparedStatement object.
	 *@since 02-May-2013
	 */
	public PreparedStatement preparePst(String query) {
		PreparedStatement pst=null;
		try {
			pst= connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pst;

	}

	public void addToBatch(PreparedStatement pst,String[] values){
		try {
			for(int i=0;i<values.length;i++){
				pst.setString(i+1, values[i]);
			}
            pst.addBatch();
		} catch (SQLException e) {
            System.out.println("failed execution of writing results to database.");
			e.printStackTrace();
		}
	}

	public int[] executeBatch(PreparedStatement pst){
		int[] result = null;
		try {
			result = pst.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void executePreparedStatement(PreparedStatement pst){
		int[] result = null;
		try {
			pst.execute();
		} catch (SQLException e) {
			System.out.println("error in executing statement: \n"+pst.toString());
			e.printStackTrace();
		}
	}

	/**
	 * method creating fail connection using GlobalInstances
	 * @return statement type object
	 * @since 9.Apr.2013
	 */
	public Statement failConnection()    
	{
		try 
		{
			st_fail=connection.createStatement();
		}
		catch(Exception e)
		{
            System.out.println("Databse Connection exception: "+e.getMessage());
		}
		return st_fail;
	}
	/**
	 * method creating url_read connection using GlobalInstances
	 * @return statement type object
	 * @since 9.Apr.2013
	 */
	public Statement urlReadConnection()    
	{
		try 
		{
			st_url_read=connection.createStatement();
		}
		catch(Exception e)
		{
            System.out.println("Databse Connection exception: "+e.getMessage());
		}
		return st_url_read;
	}
	/**
	 * method creating url connection using GlobalInstances
	 * @return statement type object
	 * @since 9.Apr.2013
	 */
	public Statement urlConnection()    
	{
		try 
		{
			st_url=connection.createStatement();
		}
		catch(Exception e)
		{
            System.out.println("Database Connection exception: "+e.getMessage());
		}
		return st_url;
	}
	/**
	 * method creating live connection using GlobalInstances
	 * @return statement type object
	 * @since 9.Apr.2013
	 */
	public Statement liveConnection()    
	{
		try 
		{
			st_live=connection.createStatement();
		}
		catch(Exception e)
		{
            System.out.println("Databse Connection exception: "+e.getMessage());
		}
		return st_live;
	}

}
