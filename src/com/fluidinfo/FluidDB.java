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
	private FluidConnector fdb = null;

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
	 * Returns either the specified namespace or null if it doesn't exist
	 * @param path that uniquely identifies the namespace
	 * @return the specified namespace or null if it doesn't exist
	 */
	public Namespace getNamespace(String path){
		// ToDo: finish this off...
	}
	
	/**
	 * Creates a new namespace at the specified path. If the namespace already exists
	 * it'll return an instance of the existing namespace. Will throw an exception if
	 * you don't have the appropriate permission to create the namespace.
	 * 
	 * @param path the path to the new namespace
	 * @return an instance representing the newly created namespace
	 * @throws FluidException - especially if you don't have permission!
	 */
	public Namespace createNamespace(String path) throws FluidException{
		// ToDo: finish this off
	}
	
	// ToDo: add get and create methods for the yet-to-be-written Object, Tag and User classes
}