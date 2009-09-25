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

import com.fluidinfo.FluidConnector;
import com.fluidinfo.FluidException;
import com.fluidinfo.FluidResponse;
import com.fluidinfo.utils.Method;
import com.fluidinfo.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * See http://doc.fluidinfo.com/fluidDB/namespaces.html
 * 
 * FluidDB namespaces provide a simple hierarchical way of organizing names - names of tags, 
 * and names of other (sub-)namespaces.
 * 
 * When a new user is created within FluidDB, a top-level namespace is created for them. For 
 * example, if Tim chooses the FluidDB user name tim, a top-level tim namespace is created for 
 * him.
 * 
 * FluidDB user names are case insensitive.
 * 
 * @author ntoll
 *
 */
public class Namespace extends BaseFOM {
	
	private String name = null;
	
	protected String description = null;
	
	protected String[] namespaces = null;
	
	protected String[] tags = null;

	/**
	 * Constructor
	 * @param fdb The connection to FluidDB
	 * @param id The id of the namespace in FluidDB
	 * @param path The path to the namespace in FluidDB
	 * @throws FOMException
	 */
	public Namespace(FluidConnector fdb, String id, String path) throws FOMException {
		super(fdb, id);
		this.rootPath = "/namespaces";
		this.path = path;
		this.name = this.GetNameFromPath(path);
	}
	
	@Override
	public void getItem() throws FluidException, IOException, FOMException, JSONException {
		Hashtable<String, String> args = new Hashtable<String, String>();
		args.put("returnDescription", "True");
		args.put("returnNamespaces", "True");
		args.put("returnTags", "True");
		FluidResponse response = this.Call(Method.GET, 200, "", args);
		JSONObject jsonResult = this.getJsonObject(response);
		this.id = jsonResult.getString("id");
		this.description = jsonResult.getString("description");
		this.namespaces = this.getStringArrayFromJSONArray(jsonResult.getJSONArray("namespaceNames"));
		this.tags = this.getStringArrayFromJSONArray(jsonResult.getJSONArray("tagNames"));
	}
	
	/**
	 * Returns the name of this namespace
	 * @return the name of this namespace
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the description associated with this namespace
	 * @return the description associated with this namespace
	 * @throws FOMException 
	 */
	public String getDescription() throws FOMException {
		// If this.description is null then call back to FluidDB to check its value
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
	 * Sets the description for this namespace
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
	 * Creates a new namespace *underneath* this namespace
	 * @param name The name of the new namespace
	 * @param description The description of the new namespace
	 * @return The newly created namespace
	 * @throws FluidException 
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FOMException 
	 */
	public Namespace createNamespace(String name, String description) throws FluidException, JSONException, IOException, FOMException{
		JSONObject jsonPayload = new JSONObject();
		jsonPayload.put("description", description);
		jsonPayload.put("name", name);
		FluidResponse response = this.Call(Method.POST, 201, jsonPayload.toString());
		JSONObject jsonResult = this.getJsonObject(response.getResponseContent());
		String newId = jsonResult.getString("id");
		String[] newPath = {this.path, name};
		Namespace newNamespace = new Namespace(this.fdb, newId, StringUtil.URIJoin(newPath));
		newNamespace.description = description;
		return newNamespace;
	}
	
	/**
	 * Deletes this namespace from FluidDB
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public void delete() throws FluidException, IOException{
		this.Call(Method.DELETE, 204, "");
	}
	
	/**
	 * Creates a new tag *underneath* this namespace
	 * @param name The name of the new tag
	 * @param description The description of the new tag
	 * @param indexed Indicates if this tag is to be indexed
	 * @return The newly created tag
	 * @throws FOMException 
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public Tag createTag(String name, String description, boolean indexed) throws FOMException, JSONException, FluidException, IOException{
		JSONObject jsonPayload = new JSONObject();
		jsonPayload.put("description", description);
		jsonPayload.put("indexed", indexed);
		jsonPayload.put("name", name);
		String[] tagPath = {"/tags", this.path};
		String tagPathURI = StringUtil.URIJoin(tagPath);
		FluidResponse response = this.Call(Method.POST, 201, jsonPayload.toString(), new Hashtable<String, String>(), tagPathURI);
		JSONObject jsonResult = this.getJsonObject(response.getResponseContent());
		String newId = jsonResult.getString("id");
		String[] tagFullPath = {this.path, name};
		String tagFullURI = StringUtil.URIJoin(tagFullPath);
		Tag newTag = new Tag(this.fdb, newId, indexed, description, tagFullURI);
		return newTag;
	}
	
	/**
	 * Gets an array of the names of the tags associated with this namespace
	 * @return an array of the names of the tags associated with this namespace
	 * @throws JSONException 
	 * @throws FOMException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public String[] getTagNames() throws FluidException, IOException, FOMException, JSONException {
		if(this.tags==null){
			try {
				this.getItem();
			} catch(Exception ex) {
				throw new FOMException("Unable to get tag names.", ex);
			}
		}
		return this.tags;
	}
	
	/**
	 * Gets an array of the names of the namespaces underneath this namespace
	 * @return an array of the names of the namespaces underneath this namespace
	 * @throws JSONException 
	 * @throws FOMException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public String[] getNamespaceNames() throws FluidException, IOException, FOMException, JSONException {
		if(this.namespaces==null){
			try {
				this.getItem();
			} catch(Exception ex) {
				throw new FOMException("Unable to get namespace names.", ex);
			}
		}
		return this.namespaces;
	}

	/**
	 * Gets a named tag associated with this namespace
	 * @param name The name of the tag to retrieve
	 * @return An instance of the retrieved tag
	 * @throws IOException 
	 * @throws FluidException 
	 * @throws FOMException 
	 * @throws JSONException 
	 */
	public Tag getTag(String name) throws FluidException, IOException, FOMException, JSONException {
		String[] tagPath = {this.path, name};
		String newPath = StringUtil.URIJoin(tagPath);
		Tag childTag = new Tag(this.fdb, "", newPath);
		childTag.getItem();
		return childTag;
	}
	
	/**
	 * Gets a named namespace from underneath this namespace 
	 * @param name The name of the namespace to retrieve
	 * @return An instance on the retrieved namespace
	 * @throws FOMException 
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws FluidException 
	 */
	public Namespace getNamespace(String name) throws FOMException, FluidException, IOException, JSONException {
		// Define the child namespace
		String[] childPath = {this.path, name};
		Namespace childNamespace = new Namespace(this.fdb, "", StringUtil.URIJoin(childPath));
		// populate it
		childNamespace.getItem();
		return childNamespace;
	}
}