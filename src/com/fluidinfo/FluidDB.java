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

import org.json.JSONException;

import com.fluidinfo.fom.*;

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
	
	/*public Object getObject(String id) {
		
	}
	
	*/
}