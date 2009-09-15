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

import com.fluidinfo.FluidConnector;

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
	
	private String description = null;

	/**
	 * Constructor
	 * @param fdb The connection to FluidDB
	 * @param path The path to the namespace in FluidDB
	 */
	public Namespace(FluidConnector fdb, String path) throws FOMException {
		super(fdb);
		this.rootPath = "/namespaces";
		this.path = path;
		this.name = this.GetNameFromPath(path);
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
	 */
	public String getDescription() {
		// If this.description is null then call back to FluidDB to check its value
		if(this.description==null){
			// ToDo: Finish this!
		}
		return this.description;
	}
	
	/**
	 * Sets the description for this namespace
	 * @param description The new description
	 */
	public void setDescription(String description){
		
	}
	
	/**
	 * Creates a new namespace *underneath* this namespace
	 * @param name The name of the new namespace
	 * @param description The description of the new namespace
	 * @return The newly created namespace
	 */
	public Namespace createNamespace(String name, String description){
	
	}
	
	/**
	 * Deletes this namespace from FluidDB
	 */
	public void delete(){
		
	}
	
	/**
	 * Creates a new tag *underneath* this namespace
	 * @param name The name of the new tag
	 * @param description The description of the new tag
	 * @param indexed Indicates if this tag is to be indexed
	 * @return The newly created tag
	 */
	public Tag createTag(String name, String description, boolean indexed){
		
	}
	
	/**
	 * Gets an array of the names of the tags associated with this namespace
	 * @return an array of the names of the tags associated with this namespace
	 */
	public String[] getTagNames() {
		
	}
	
	/**
	 * Gets an array of the names of the namespaces underneath this namespace
	 * @return an array of the names of the namespaces underneath this namespace
	 */
	public String[] getNamespaceNames() {
		
	}

	/**
	 * Gets a named tag associated with this namespace
	 * @param name The name of the tag to retrieve
	 * @return An instance of the retrieved tag
	 */
	public Tag getTag(String name) {
		
	}
	
	/**
	 *Gets a named namespace from underneath this namespace 
	 * @param name The name of the namespace to retrieve
	 * @return An instance on the retrieved namespace
	 */
	public Namespace getNamespace(String name) {
		
	}
}