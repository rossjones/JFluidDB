/*
 * Copyright (c) 2009 Ross Jones 
 *   - Derived from code by Nicholas H.Tollervey
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
package com.fluidinfo;

import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;

public class FluidConnector {

	public final static String URL = "http://fluiddb.fluidinfo.com/";  
	public final static String SandboxURL = "http://sandbox.fluidinfo.com/";  
		
    private boolean _alwaysUseJson = false;

    /* May not be required, although I need to check whether Authenticator is
       likely to cause problems if it is a global setting */
    private String _username = "";
    private String _password = "";
    private boolean _authSet = false;

    /**
     * Getter for determining if response=json with GET/PUT requests
     * @return true if the connector will always use json
     */
	public boolean isAlwaysUseJson() {
		return _alwaysUseJson;
	}

    /**
     * If set then response=json will be used when doing GET/PUT requests 
     * @return true if the connector will always use json
     */
	public void setAlwaysUseJson(boolean alwaysUseJson) {
		this._alwaysUseJson = alwaysUseJson;
	}

	/**
	 * Sets the authentication details for PUT requests
	 * @param username The username of the account to use
	 * @param password The password for this user
	 */
	public void setAuthDetails( final String username, final String password )
	{
	    Authenticator.setDefault(new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});			 
		
		this._username = username;
		this._password = password;
		this._authSet = true;
	}
	

	/**
	 * Calls FluidDB and returns the JSON representation of whatever resource
	 * we requested.
	 * @param m The method to use 
	 * @param path The path of the resource
	 * @return A JSON string
     * @throws FluidException If an error occurs, such as no such resource
	 */
    public String Call(Method m, String path) throws FluidException
    {
    	return this.Call(m, path, "");
    }


    /**
   	 * Calls FluidDB and returns the JSON representation of whatever resource
   	 * we requested.
   	 * @param m The method to use 
   	 * @param path The path of the resource
     * @param body An optional body to send with the request
     * @return A JSON string
     * @throws FluidException If an error occurs, such as no such resource
     */
    public String Call(Method m, String path, String body) throws FluidException
    {
        return this.Call(m, path, body, new Hashtable<String, String>());
    }

    
    /**
  	 * Calls FluidDB and returns the JSON representation of whatever resource
  	 * we requested.
  	 * @param m The method to use 
  	 * @param path The path of the resource
     * @param body An optional body to send with the request
     * @param args A dictionary of arguments to pass with the request
     * @return A JSON string
     * @throws FluidException If an error occurs, such as no such resource or malformed
     *          arguments
     */
    @SuppressWarnings("deprecation")
	public String Call(Method m, String path, String body, Hashtable<String, String> args) throws FluidException
    {
    	StringBuffer uri = new StringBuffer();
    	uri.append( URL );
    	uri.append( path);
    	if ( this._alwaysUseJson)
    	{
    		 if (!args.containsKey("format"))
             {
    			 args.put("format", "json");
             }
    	}
    	
    	if (args.size() > 0)
    	{
    		uri.append("?");
    		Vector<String> argList = new Vector<String>();
    		Enumeration<String> e = args.keys();
    		while( e.hasMoreElements())
    		{
    			String k = e.nextElement();
    			argList.add( k + "=" + URLEncoder.encode(args.get(k)) );
    		}
    		
    		uri.append( StringUtil.join(argList, "&") );
    	}
    	
    	BufferedReader    reader      = null;
    	OutputStream      writer      = null;
        HttpURLConnection connection  = null;
        StringBuffer      sb          = new StringBuffer();
        String            line        = "";
        
    	try
    	{
	 	     connection = (HttpURLConnection)new URL( uri.toString() ).openConnection();
			 connection.setRequestMethod(  m.toString().toUpperCase() );
			 if ( m == Method.POST || m == Method.POST )
				 connection.setDoInput(true);
			 connection.setDoOutput(true);
			 connection.setReadTimeout(1000);
			 connection.setRequestProperty("accept", "application/json");
			 connection.setRequestProperty("user-agent", "JFluidDB");
			 
			 if ( body == "" || body == null)
			 {
				 connection.setRequestProperty("content-type", "text/plain");
			 }
			 else
			 {
				 byte[] data = body.getBytes("US-ASCII");
				 
				 connection.setRequestProperty("content-type", "application/json");
				 connection.setRequestProperty("content-length", new Integer(data.length).toString() );
				 
				 // writes the data, although this is currently a bit naughty when it is a 
				 // GET request, this is likely to change on the server
				 writer = connection.getOutputStream();
				 writer.write(data);
				 writer.close();
			 }
			 
			 // Read the entire response			 
			 reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 while ((line = reader.readLine()) != null)
			 {
			     sb.append(line);
			 }
			 reader.close();
    	 }
	   	 catch( FileNotFoundException fnfe )
		 {
		     // Resource not found, we might want to add more information to the exception
	   		 throw new FluidException(fnfe);
		 }
    	 catch( Exception ee )
    	 {
             // DO NOT leave this block in, catch all of the exceptions we can through so 
             // that we can provide more fine-grained error handling
	   		 throw new FluidException(ee);
    	 }
    	 finally
    	 {
    		 connection.disconnect();
    		 reader = null;
    		 writer = null;
    		 connection = null;
    	 }
    	 
    	return sb.toString();
    }

    // Temporary test hooks
    public static void main(String[] args)
    {
    	FluidConnector fc = new FluidConnector();
    	String resp = "";
    	Hashtable<String, String> arguments = new Hashtable<String,String>();
    	
    	try
    	{
	    	arguments.put("query", "has fluiddb/users/username");
	    	resp = fc.Call( Method.GET, "objects", "", arguments);
	    	System.out.println(resp);
    	}
    	catch( FluidException fe )
    	{
    		
    	}

    	try
    	{
	        // Return an object where the tag “username” from the “fluiddb/users” namespace has the value “ntoll”
	        arguments.put("query","fluiddb/users/username = \"ntoll\"");
	        resp = fc.Call(Method.GET, "objects", "", arguments);
	        System.out.println( resp );
		}
		catch( FluidException fe )
		{
			
		}

		try
		{
	        resp = fc.Call(Method.GET, "objects/5873e7cc-2a4a-44f7-a00e-7cebf92a7332", "{'showAbout': True}");
	        System.out.println( resp );
		}
		catch( FluidException fe )
		{
			
		}

		try
		{
	        resp = fc.Call(Method.GET, "objects/5873e7cc-2a4a-44f7-a00e-7cebf92a7332/fluiddb/users/name");
	        System.out.println( resp );
		}
		catch( FluidException fe )
		{
			
		}
        

		try
		{
	        fc.setAlwaysUseJson( true);
	        resp = fc.Call(Method.GET, "objects/5873e7cc-2a4a-44f7-a00e-7cebf92a7332/fluiddb/users/name");
	        System.out.println( resp );
    	}
    	catch( FluidException fe )
    	{
    		
    	}

    }
}
	
