/*
 * Copyright (c) 2009 Nicholas H.Tollervey (ntoll) and others
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.fluidinfo.fom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

import com.fluidinfo.*;
import com.fluidinfo.utils.*;

/**
 * 
 * Base class for all other Fluid Object Model (FOM) classes
 * 
 * Provides shared / common functionality
 * 
 * @author ntoll
 *
 */
public abstract class BaseFOM implements FOMInterface {
	
	/**
	 * The connection to FluidDB
	 */
	protected FluidConnector fdb = null;
	
	/**
	 * The id of the object in FluidDB that corresponds to the thing represented by the 
	 * instance.
	 */
	protected String id = "";
	
	/**
	 * The instance's "root" path (namespaces, objects, tags etc)
	 */
	protected String rootPath = "";
	
	/**
	 * The instance's path within FluidDB underneath the root path
	 */
	protected String path = "";

	/**
	 * Constructor
	 * @param fdb - the connection to FluidDB to be used with this instance
	 * @param id - the id of the object in FluidDB that corresponds to the instance
	 */
	public BaseFOM(FluidConnector fdb, String id){
		this.fdb = fdb;
		this.id = id;
	}
	
	/**
	 * Return the FluidDB object id corresponding this instance
	 * @return the FluidDB object id corresponding this instance
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Return's the instance's path in FluidDB (/objects/xyz..../ etc)
	 * @return the instance's path in FluidDB
	 */
	public String getPath(){
		Vector<String> paths = new Vector<String>();
		paths.add(this.rootPath);
		paths.add(this.path);
		return StringUtil.URIJoin(paths);
	}
	
	/**
	 * Given a path will return the name of the thing referenced (the last item in the path)
	 * @param path The path to the item
	 * @return The name of the thing referenced by the path (the last item in the path)
	 * @throws FOMException If the method cannot determine the name from the path
	 */
	protected String GetNameFromPath(String path) throws FOMException{
		// Test / normalize the path
		while (path.endsWith("/")){
			// cut any trailing "/" off the end
			path=path.substring(0, path.lastIndexOf("/"));
		}
		if(path.length()>0) {
			return path.substring(path.lastIndexOf("/")+1);
		} else {
			throw new FOMException("Cannot determine the name from the supplied path.");
		}
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body) throws FluidException, IOException {
		return this.Call(m, expectedReturnCode, body, new Hashtable<String, String>());
	}
	
	/**
	 * 
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, final Hashtable<String, String> args) throws FluidException, IOException{
		String callPath;
		if(this.path=="" || this.path==null)
		{
			callPath = this.rootPath;
		} else {
			String[] fluidPath = {this.rootPath, this.path};
			callPath = StringUtil.URIJoin(fluidPath);
		}
		return this.Call(m, expectedReturnCode, body, args, callPath);
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @param callPath the URI to call in FluidDB
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, final Hashtable<String, String> args, String callPath) throws FluidException, IOException{
		FluidResponse response = this.fdb.Call(m, callPath, body, args);
		if(response.getResponseCode()==expectedReturnCode){
			return response;
		} else {
			// Hmmm... we didn't get the response we were expecting so build as helpful
			// an exception as possible.
			StringBuilder sb = new StringBuilder();
			sb.append("FluidDB returned the following error: ");
			sb.append(response.getResponseCode());
			sb.append(" (");
			sb.append(response.getResponseMessage());
			sb.append(") ");
			sb.append(response.getResponseError());
			sb.append(" - with the request ID: ");
			sb.append(response.getErrorRequestID());
			throw new FluidException(sb.toString().trim());
		}
	}
	
	/**
	 * Given a FluidResponse object will return an appropriate representation as a JSONObject
	 * @param response The FluidResponse object to process
	 * @return an appropriate representation of the FluidResponse object as a JSONObject
	 * @throws FOMException If the content type of the response is NOT "application/json"
	 * @throws JSONException If there was a problem processing the json content of the response
	 */
	protected JSONObject getJsonObject(FluidResponse response) throws FOMException, JSONException {
		String contentType = response.getResponseContentType();
		if(contentType.equals("application/json")){
			return this.getJsonObject(response.getResponseContent());
		} else {
			throw new FOMException("Unable to convert response to json because the content type is "+contentType);
		}
	}
	
	/**
	 * Given a string of json code will return an appropriate representation as a JSONObject
	 * @param jsonInput The string of json code to be turned into a JSONObject instance 
	 * @return The resulting JSONObject instance
	 * @throws JSONException If there was a problem processing the jsonInput
	 */
	protected JSONObject getJsonObject(String jsonInput) throws JSONException{
		JSONTokener jsonResultTokener = new JSONTokener(jsonInput);
		JSONObject jsonResult = new JSONObject(jsonResultTokener);
		return jsonResult;
	}
	
	/**
	 * Given a JSONArray (of strings) will return the String[] array representation
	 * @param input The JSONArray to process
	 * @return The resulting String[]
	 * @throws JSONException
	 */
	protected String[] getStringArrayFromJSONArray(JSONArray input) throws JSONException {
		String[] result = new String[input.length()];
		for(int i=0; i<input.length(); i++) {
			result[i] = input.getString(i);
		}
		return result;
	}
}