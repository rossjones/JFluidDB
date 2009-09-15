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
	 * Constructor
	 * @param fdb The connection to FluidDB
	 * @param path The path to the namespace in FluidDB
	 */
	public Tag(FluidConnector fdb, String path) {
		super(fdb);
		this.rootPath="/tags";
		this.path=path;
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
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description for this tag
	 * @param description The new description
	 */
	public void setDescription(String description){
		
	}
	
	/**
	 * Deletes this tag from FluidDB
	 */
	public void delete(){
		
	}
}
