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

import com.fluidinfo.*;
import com.fluidinfo.utils.*;

/**
 * 
 * Base class for all other Fluid Object Model (FOM) classes
 * 
 * Provides shared / common functionality (obviously)
 * 
 * @author ntoll
 *
 */
public abstract class BaseFOM {
	
	/**
	 * The connection to FluidDB
	 */
	private FluidConnector fdb = null;
	
	/**
	 * The instance's "root" path (namespaces, objects, tags etc)
	 */
	protected String rootPath = "";
	
	/**
	 * The instance's path within FluidDB underneath the root path
	 */
	protected String path = "";
	
	/**
	 * Return's the instance's path in FluidDB (/objects/xyz..../ etc)
	 * @return the instance's path in FluidDB
	 */
	public String getPath(){
		return this.rootPath;
	}

	/**
	 * Constructor
	 * @param fdb - the connection to FluidDB to be used with this instance
	 */
	public BaseFOM(FluidConnector fdb){
		this.fdb=fdb;
	}
	
	/**
	 * Given a path will return the name of the thing referenced (the last item in the path)
	 * @param path The path to the item
	 * @return The name of the thing referenced by the path (the last item in the path)
	 * @throws FOMException If the method cannot determine the name from the path
	 */
	protected String GetNameFromPath(String path) throws FOMException{
		// Test / normalize the path
		while (path.endsWith("/")){
			// cut any trailing "/" off the end
			path=path.substring(0, path.lastIndexOf("/"));
		}
		if(path.length()>0) {
			return path.substring(path.lastIndexOf("/")+1);
		} else {
			throw new FOMException("Cannot determine the name from the supplied path.");
		}
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, final String body, final Hashtable<String, String> args) throws FluidException, IOException{
		String callPath = this.rootPath+this.path;
		if(this.path==""|| this.path==null)
		{
			callPath = this.rootPath;
		}
		return this.fdb.Call(m, callPath, body, args);
	}
}
	