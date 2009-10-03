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

import com.fluidinfo.FluidConnector;
import com.fluidinfo.FluidException;
import com.fluidinfo.FluidResponse;
import com.fluidinfo.utils.Method;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * See http://doc.fluidinfo.com/fluidDB/users.html
 * 
 * When a new user (or application) is created in FluidDB, an object is made to hold 
 * information for the user. FluidDB stores some information about the user on this object.
 * 
 * The user is free to add more information to the object that represents them. Because 
 * FluidDB objects are not owned, other users or applications may do the same.
 * 
 * @author ntoll
 *
 */
public class User extends BaseFOM{
	
	private String name;

	/**
	 * Constructor
	 * 
	 * @param fdb The connection to FluidDB
	 * @param id The id of the user in FluidDB
	 * @param path The path to the user in FluidDB
	 * @throws FOMException
	 */
	public User(FluidConnector fdb, String id, String path) throws FOMException {
		super(fdb, id);
		this.rootPath = "/users";
		this.path = path;
		this.name = this.GetNameFromPath(path);
	}

	@Override
	public void getItem() throws FluidException, IOException, FOMException,
			JSONException {
		FluidResponse response = this.Call(Method.GET, 200, "");
		JSONObject jsonResult = this.getJsonObject(response);
		this.id = jsonResult.getString("id");
		this.name = jsonResult.getString("name");
	}
	
	/**
	 * Get the user's name
	 * 
	 * @return the user's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the user's root namespace (/namespace/USERNAME)
	 * 
	 * @return the user's root namespace
	 * @throws JSONException 
	 * @throws FOMException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public Namespace RootNamespace() throws FluidException, IOException, FOMException, JSONException {
		Namespace rootNamespace = new Namespace(this.fdb, "", this.name);
		// populate it
		rootNamespace.getItem();
		return rootNamespace;
	}
}
