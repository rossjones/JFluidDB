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

import com.fluidinfo.utils.Base64;
import com.fluidinfo.utils.Method;
import com.fluidinfo.utils.StringUtil;

/**
 * A base class defining the call methods for all the other FluidDB classes
 * 
 * @author rossjones, ntoll
 *
 */
public class FluidConnector {
    
    /**
    * The URL for FluidDB
    */
    public final static String URL = "http://fluiddb.fluidinfo.com";  
    
    /**
    * The URL for the Sandbox for development testing
    */
    public final static String SandboxURL = "http://sandbox.fluidinfo.com";  
    
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
    * @throws IOException 
    */
    public FluidResponse Call(Method m, String path) throws FluidException, IOException {
        return this.Call(m, path, "");
    }

    /**
     * Makes a call to FluidDB
     * @param m The type of HTTP method to use 
     * @param path The path to call
     * @param body An optional body to send with the request
     * @return A string version of the result
     * @throws FluidException If an error occurs, such as no such resource
     * @throws IOException 
     */
    public FluidResponse Call(Method m, String path, String body) throws FluidException, IOException {
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
     *         arguments
     * @throws IOException Will get thrown if we can't extract the errorStream from the connection
     */
    public FluidResponse Call(Method m, String path, String body, Hashtable<String, String> args) throws FluidException, IOException {
        return this.Call(m, path, body, args, "application/json; charset=utf-8");
    }
    
    /**
     * Makes a call to FluidDB
     * @param m The type of HTTP method to use 
     * @param path The path to call
     * @param body An optional body to send with the request
     * @param args A dictionary of arguments to pass with the request
     * @param content_type The value for the Content-Type header
     * @return A string version of the result
     * @throws FluidException If an error occurs, such as no such resource or malformed
     *         arguments
     * @throws IOException Will get thrown if we can't extract the errorStream from the connection
     */
    public FluidResponse Call(Method m, String path, String body, Hashtable<String, String> args, String content_type) throws FluidException, IOException {   
        // Build the URI we'll be calling
        StringBuffer uri = new StringBuffer();
        uri.append( this.url );
        uri.append( path);
        
        if (args.size() > 0){
            try{
                uri.append("?");
                Vector<String> argList = new Vector<String>();
                Enumeration<String> e = args.keys();
                while( e.hasMoreElements()){
                    String k = e.nextElement();
                    argList.add( k + "=" + URLEncoder.encode(args.get(k), "UTF-8") );
                }
                uri.append( StringUtil.join(argList, "&") );
            } catch (Exception e){
                throw new FluidException(e);
            }
        }
        
        // Declare some vars we'll use in a moment...
        BufferedReader    reader      = null;
        OutputStream      writer      = null;
        HttpURLConnection connection  = null;
        StringBuffer      sb          = new StringBuffer();
        String            line        = "";
        FluidResponse 	  response	  = null;
        
        // Lets build the HTTP request and attempt to get a response
        try{
            // Basic setup of the connection to FluidDB
            connection = (HttpURLConnection)new URL( uri.toString() ).openConnection();
            connection.setRequestMethod(  m.toString().toUpperCase() );
            if ( m == Method.POST || m == Method.PUT )
                connection.setDoInput(true);
            connection.setDoOutput(true);
            //connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("user-agent", "JFluidDB");
            // Authorization header (if required)
            if(!(this.password == "" & this.username == "")){
                String userpass = this.username+":"+password;
                connection.setRequestProperty("Authorization", "Basic "+Base64.encodeBytes(userpass.getBytes()));
            }
            // Content type and body for POST/PUT requests
            if ( body == "" || body == null){
                connection.setRequestProperty("content-type", "text/plain; charset=utf-8");
            } else {
                byte[] data = body.getBytes("UTF-8");
                connection.setRequestProperty("content-type", content_type);
                connection.setRequestProperty("content-length", new Integer(data.length).toString() );
                writer = connection.getOutputStream();
                writer.write(data);
                writer.close();
            }
            
            // Read the entire response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            reader.close();
            response = this.BuildResponse(connection, sb.toString());		 
        } catch (FileNotFoundException fnfe){
            // Build a 404 response
            response = this.BuildResponse(connection, connection.getURL().toString());
        } catch ( IOException e) {
            // Build a 401 (usually)
            response = this.BuildResponse(connection, connection.getURL().toString());
        } catch ( Exception e ) {
            // catch all of the other exceptions so that we can provide more 
            // fine-grained error handling for the response object otherwise barf with
            // a FluidException
            if(connection.getHeaderFields().containsKey("X-FluidDB-Error-Class")) {
                response =  this.BuildResponse(connection, "");
            } else {
                throw new FluidException(e);
            }
        } finally {
            // Tidy up after ourselves ;-)
            connection.disconnect();
            reader = null;
            writer = null;
            connection = null;
        }
        // et voila!
        return response;
    }

    /**
     * Utility method to build new FluidDBResponse instances given a connection and some content
     * 
     * @param connection The connection made to FluidDB
     * @param content The payload of the response
     * @return a new FluidDBResponse instance
     * @throws IOException
     */
    private FluidResponse BuildResponse(HttpURLConnection connection, String content) throws IOException {
        // Grab some useful information
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseEncoding = connection.getHeaderField("Content-Type");
        String responseError = connection.getHeaderField("X-FluidDB-Error-Class");
        String requestID = connection.getHeaderField("X-FluidDB-Request-Id");
        // Build the FluidResponse object
        return new FluidResponse(responseCode, responseMessage, responseEncoding, content, responseError, requestID);
    }
}
	
