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
package com.fluidinfo;

import java.io.IOException;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fluidinfo.fom.*;
import com.fluidinfo.fom.Object;
import com.fluidinfo.utils.Method;
import com.fluidinfo.utils.StringUtil;

/**
 * Represents an instance of FluidDB
 * 
 * @author ntoll
 *
 */
public class FluidDB {
	
	/**
	 * The instance of the connector to use to call to FluidDB
	 */
	protected FluidConnector fdb = null;

	/**
	 * Represents the URL for FluidDB - defaults to something sensible
	 */
	private String url = FluidConnector.URL;
	
	/**
	 * Returns the URL to use to connect to the FluidDB instance
	 * @return the URL to use to connect to the FluidDB instance
	 */
	public String getURL(){
		return this.url;
	}

	/**
	 * Default constructor. 
	 * 
	 * Defaults to http://fluiddb.fluidinfo.com/
	 */
	public FluidDB(){
		this.fdb = new FluidConnector();
		this.fdb.setUrl(this.url);
	}
	
	/**
	 * Constructor
	 * @param URL The location of the FluidDB to connect to
	 */
	public FluidDB(String URL){
		this.url = URL;
		this.fdb = new FluidConnector();
		this.fdb.setUrl(this.url);
	}
	
	/**
	 * Sets the credentials for connecting to the FluidDB
	 * 
	 * @param username the fluidDB username
	 * @param password the fluidDB password
	 */
	public void Login(String username, String password){
		this.fdb.setUsername(username);
		this.fdb.setPassword(password);
	}
	
	/**
	 * Sets the connection to FluidDB as anonymous
	 */
	public void Logout(){
		this.fdb.setUsername("");
		this.fdb.setPassword("");
	}
	
	/**
	 * Returns specified namespace
	 * @param path that uniquely identifies the namespace
	 * @return the specified namespace or null if it doesn't exist
	 * @throws FOMException 
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public Namespace getNamespace(String path) throws FOMException, FluidException, IOException, JSONException{
		Namespace childNamespace = new Namespace(this.fdb, "", path);
		// populate it
		childNamespace.getItem();
		return childNamespace;
	}
	
	/**
	 * Returns the specified tag
	 * @param path that uniquely identifies the tag
	 * @return the specified tag
	 * @throws FOMException
	 * @throws FluidException
	 * @throws IOException
	 * @throws JSONException
	 */
	public Tag getTag(String path) throws FOMException, FluidException, IOException, JSONException {
		Tag childTag = new Tag(this.fdb, "", path);
		childTag.getItem();
		return childTag;
	}
	
	/**
	 * Returns the User instance for the logged in user
	 * @return the User instance for the logged in user
	 * @throws FOMException
	 * @throws FluidException
	 * @throws IOException
	 * @throws JSONException
	 */
	public User getLoggedInUser() throws FOMException, FluidException, IOException, JSONException {
	    return this.getUser(this.fdb.getUsername());
	}
	
	/**
	 * Returns the specified user
	 * @param username for the user in question
	 * @return the specified user
	 * @throws FOMException 
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public User getUser(String username) throws FOMException, FluidException, IOException, JSONException {
        User user = new User(this.fdb, "", username);
        user.getItem();
        return user;
    }
	
	/**
	 * Creates a new object in FluidDB
	 * 
	 * @param about The contents of the special "about" tag
	 * @return The newly created object
	 * @throws FOMException
	 * @throws JSONException
	 * @throws FluidException
	 * @throws IOException
	 */
	public Object createObject(String about) throws FOMException, JSONException, FluidException, IOException {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("about", about);
        FluidResponse response = this.fdb.Call(Method.POST, "/objects", jsonPayload.toString());
        JSONTokener jsonResultTokener = new JSONTokener(response.getResponseContent());
        JSONObject jsonResult = new JSONObject(jsonResultTokener);
        String newId = jsonResult.getString("id");
        Object newObject = new Object(this.fdb, newId, newId);
        return newObject;
    }
	
	/**
	 * Gets an object with the provided id
	 * @param id the id of the object to return
	 * @return the object with the referenced id
	 * @throws FOMException
	 * @throws FluidException
	 * @throws IOException
	 * @throws JSONException
	 */
	public Object getObject(String id) throws FOMException, FluidException, IOException, JSONException {
	    Object o = new Object(this.fdb, id, id);
        o.getItem();
        return o;
	}
	
	/**
	 * Given a query, will return a list of object ids that match. From the FluidDB docs:
	 * 
	 * FluidDB provides a simple query language that allows applications to search for objects 
	 * based on their tags’ values. The following kinds of queries are possible:
     *
     *  * Numeric: To find objects based on the numeric value of tags. For example, tim/rating > 5.
     *  
     *  * Textual: To find objects based on text matching of their tag values, e.g., 
     *    sally/opinion matches fantastic. Text matching is done with Lucene, meaning that 
     *    Lucene matching capabilities and style will be available [NOT YET IMPLEMENTED].
     *    
     *  * Presence: Use has to request objects that have a given tag. For example, has 
     *    sally/opinion.
     *    
     *  * Set contents: A tag on an object can hold a set of strings. For example, a tag called 
     *    mary/product-reviews/keywords might be on an object with a value of [ "cool", "kids", 
     *    "adventure" ]. The contains operator can be used to select objects with a matching value. 
     *    The query mary/product-reviews/keywords contains "kids" would match the object in this 
     *    example.
     *    
     *  * Exclusion: You can exclude objects with the except keyword. For example has 
     *    nytimes.com/appeared except has james/seen. The except operator performs a set difference.
     *    
     *  * Logic: Query components can be combined with and and or. For example, has sara/rating and 
     *    tim/rating > 5.
     *    
     *  * Grouping: Parentheses can be used to group query components. For example, has sara/rating 
     *    and (tim/rating > 5 or mike/rating > 7).
     *
     * That’s it!
     * 
	 * @param query The query
	 * @return An array of the matching object ids
	 * @throws IOException 
	 * @throws FluidException 
	 * @throws JSONException 
	 */
	public String[] searchObjects(String query) throws FluidException, IOException, JSONException {
	    Hashtable<String, String> args = new Hashtable<String, String>();
	    args.put("query", query);
	    FluidResponse r = this.fdb.Call(Method.GET, "/objects", "", args);
	    if(r.getResponseCode()==200) {
            JSONArray ids = StringUtil.getJsonObjectFromString(r.getResponseContent()).getJSONArray("ids");
            return StringUtil.getStringArrayFromJSONArray(ids);
	    } else {
	        // Lets generate a helpful exception...
	        StringBuilder sb = new StringBuilder();
            sb.append("FluidDB returned the following error: ");
            sb.append(r.getResponseCode());
            sb.append(" (");
            sb.append(r.getResponseMessage());
            sb.append(") ");
            sb.append(r.getResponseError());
            sb.append(" - with the request ID: ");
            sb.append(r.getErrorRequestID());
            throw new FluidException(sb.toString().trim());
	    }
	}
}