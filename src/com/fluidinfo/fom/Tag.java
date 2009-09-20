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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidinfo.FluidConnector;
import com.fluidinfo.FluidException;
import com.fluidinfo.FluidResponse;
import com.fluidinfo.utils.Method;

/**
 * 
 * A FluidDB user, for example named Sara, can tag as many different objects as she likes 
 * with her tags, using whatever values she likes. For example, she might tag an object 
 * representing The Eiffel Tower, with a sara/opinion of beautiful and another object representing 
 * Quantum Electrodynamics, with the sara/opinion of hard.
 * 
 * @author ntoll
 *
 */
public class Tag extends BaseFOM {
	
	/**
	 * The name of this tag
	 */
	private String name = null;
	
	/**
	 * The description associated with this tag
	 */
	private String description = null;
	
	/**
	 * Whether of not tag values should be indexed
	 */
	private boolean indexed = true;
	
	public Tag(FluidConnector fdb, String id, String path) throws FOMException {
		super(fdb, id);
		this.rootPath="/tags";
		this.path=path;
		this.name = this.GetNameFromPath(path);
	}

	/**
	 * Constructor
	 * @param fdb The connection to FluidDB
	 * @param id The id of the tag in FluidDB
	 * @param indexed Whether or not tag values are indexed
	 * @param description The description associated with this tag
	 * @param path The path to the namespace in FluidDB
	 * @throws FOMException 
	 */
	public Tag(FluidConnector fdb, String id, boolean indexed, String description, String path) throws FOMException {
		super(fdb, id);
		this.indexed=indexed;
		this.description=description;
		this.rootPath="/tags";
		this.path=path;
		this.name = this.GetNameFromPath(path);
	}

	@Override
	public void getItem() throws FluidException, IOException, FOMException,
			JSONException {
		Hashtable<String, String> args = new Hashtable<String, String>();
		args.put("returnDescription", "True");
		FluidResponse response = this.Call(Method.GET, 200, "", args);
		JSONObject jsonResult = this.getJsonObject(response);
		this.id = jsonResult.getString("id");
		this.description = jsonResult.getString("description");
		this.indexed = jsonResult.getBoolean("indexed");
	}
	
	/**
	 * Returns the name of this tag
	 * @return the name of this tag
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the description associated with this tag
	 * @return the description associated with this tag
	 * @throws FOMException 
	 */
	public String getDescription() throws FOMException {
		if(this.description==null){
			try {
				this.getItem();
			} catch(Exception ex) {
				throw new FOMException("Unable to get description.", ex);
			}
		}
		return this.description;
	}
	
	/**
	 * Sets the description for this tag
	 * @param description The new description
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public void setDescription(String description) throws JSONException, FluidException, IOException{
		JSONObject jsonPayload = new JSONObject();
		jsonPayload.put("description", description);
		this.Call(Method.PUT, 204, jsonPayload.toString());
		this.description=description;
	}
	
	/**
	 * Returns whether or not tag values are indexed
	 * @return
	 */
	public boolean isIndexed() {
		return this.indexed;
	}
	
	/**
	 * Deletes this tag from FluidDB
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public void delete() throws FluidException, IOException{
		this.Call(Method.DELETE, 204, "");
	}

}
