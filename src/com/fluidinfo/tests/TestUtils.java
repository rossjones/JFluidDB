package com.fluidinfo.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fluidinfo.FluidConnector;
import com.fluidinfo.FluidException;

/**
 * A utility class containing lots of static methods and other useful stuff for use by the 
 * unit test classes
 * 
 * @author ntoll
 *
 */
public class TestUtils {
	
	/**
	 * Does the passed array contain the string
	 * @param array the array to check
	 * @param item the string we're looking for
	 * @return
	 */
	public static boolean contains(String[] array, String item) {
		if(array==null || array.length==0) return false;
		for(int i=0; i<array.length; i++) {
			if(item.equals(array[i])) return true;
		}
		return false;
	}
	
	/**
	 * Return a FluidConnector instance with the supplied credentials
	 * @param username the username to use
	 * @param password the password to use
	 * @return the resulting FluidConnector
	 * @throws FluidException
	 */
	public static FluidConnector getFluidConnection(String username, String password) throws FluidException {
		FluidConnector fdb = new FluidConnector();
		fdb.setUrl(FluidConnector.SandboxURL);
		fdb.setUsername(username);
		fdb.setPassword(password);
		return fdb;
	}
	
	/**
	 * Get the settings for username and password from the credentials.json file
	 * @return The JSONObject containing the user credentials
	 * @throws Exception 
	 */
	public static JSONObject getSettings() throws Exception {
		// Read the credentials.json file found in the home directory of the user 
		// running the unit tests
		File file = new File(System.getProperty("user.home"), "credentials.json");
		StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file.getPath()));
            String text = null;
            while ((text = reader.readLine()) != null){
                contents.append(text).append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                    reader.close();
            }
        }
        JSONObject credentials = TestUtils.getJsonObject(contents.toString());
        return credentials;
	}
	
	/**
	 * Get a FluidConnector with the credentials found in credentials.json
	 * @return the resulting FluidConnector
	 * @throws Exception
	 */
	public static FluidConnector getFluidConnectionWithSettings() throws Exception {
		JSONObject credentials = TestUtils.getSettings();
		String username = credentials.getString("username");
		String password = credentials.getString("password");
		if (username=="" || password=="")
		{
			throw new FluidException("You must supply a username and password in a credentials.json file in order to run the unit tests.");
		}
		return TestUtils.getFluidConnection(username, password);
	}
	
	/**
	 * Given a string will attempt to create a JSONObject
	 * @param input the string to process
	 * @return the resulting JSONObject
	 * @throws JSONException
	 */
	public static JSONObject getJsonObject(String input) throws JSONException{
		JSONTokener jsonResultTokener = new JSONTokener(input);
		JSONObject jsonResult = new JSONObject(jsonResultTokener);
		return jsonResult;
	}
}
