/*
 * Copyright (c) 2009 Ross Jones and others
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

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class FluidConnector {

	/**
	 * The URL for FluidDB
	 */
	public final static String URL = "http://fluiddb.fluidinfo.com/";  
	
	/**
	 * The URL for the Sandbox for development testing
	 */
	public final static String SandboxURL = "http://sandbox.fluidinfo.com/";  
	
	private String url = URL;
	
	/**
	 * Setter for the URL to use for connecting to FluidDB
	 * @param url The URL to use for connecting to FluidDB
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Getter for the URL used for connecting to FluidDB
	 * @return the URL used to connect to FluidDB
	 */
	public String getUrl() {
		return url;
	}
		
	/**
	 * Use format=json for responses from GET and PUT requests
	 * 
	 * (FluidDB defaults to raw payload)
	 */
    private boolean alwaysUseJson = false;
    
    /**
     * Getter for determining if response=json with GET/PUT requests
     * @return true if the connector will always use json
     */
	public boolean getAlwaysUseJson() {
		return alwaysUseJson;
	}

    /**
     * If set then response=json will be used when doing GET/PUT requests 
     * @return true if the connector will always use json
     */
	public void setAlwaysUseJson(boolean alwaysUseJson) {
		this.alwaysUseJson = alwaysUseJson;
	}

    

	/**
	 * The FluidDB username
	 */
    private String username = "";
    
    /**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
    
	/**
	 * The FluidDB password
	 */
	private String password = "";
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Makes a call to FluidDB
	 * @param m The type of HTTP method to use 
	 * @param path The path to call
	 * @return A string version of the result
     * @throws FluidException If an error occurs, such as no such resource
	 */
    public String Call(Method m, String path) throws FluidException
    {
    	return this.Call(m, path, "");
    }


    /**
   	 * Makes a call to FluidDB
   	 * @param m The type of HTTP method to use 
   	 * @param path The path to call
     * @param body An optional body to send with the request
     * @return A string version of the result
     * @throws FluidException If an error occurs, such as no such resource
     */
    public String Call(Method m, String path, String body) throws FluidException
    {
        return this.Call(m, path, body, new Hashtable<String, String>());
    }

    
    /**
  	 * Makes a call to FluidDB
  	 * @param m The type of HTTP method to use 
  	 * @param path The path to call
     * @param body An optional body to send with the request
     * @param args A dictionary of arguments to pass with the request
     * @return A string version of the result
     * @throws FluidException If an error occurs, such as no such resource or malformed
     *          arguments
     */
    @SuppressWarnings("deprecation")
	public String Call(Method m, String path, String body, Hashtable<String, String> args) throws FluidException
    {
    	StringBuffer uri = new StringBuffer();
    	uri.append( URL );
    	uri.append( path);
    	if ( this.alwaysUseJson)
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
			 if(!(this.password == "" & this.username == ""))
			 {
				String userpass = this.username+":"+password;
				connection.setRequestProperty("Authorization", Base64.encode(userpass.getBytes()));
			 }
			 if ( body == "" || body == null)
			 {
				 connection.setRequestProperty("content-type", "text/plain");
			 }
			 else
			 {
				 byte[] data = body.getBytes("US-ASCII");
				 
				 connection.setRequestProperty("content-type", "application/json");
				 connection.setRequestProperty("content-length", new Integer(data.length).toString() );
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
}
	
