/*
 * Copyright (c) 2009 Ross Jones 
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
package com.fluidinfo.utils;

import java.util.AbstractCollection;

/**
 * Some generic utilities for manipulating strings
 * @author rossjones, ntoll
 *
 */
public class StringUtil {
	
	/**
	 * Joins an array of strings with the specified delimiter
	 * @param s The strings to join
	 * @param delim The delimiter
	 * @return The joined strings
	 */
	public static String join(String[] s, String delim) {
		if (s == null || s.length==0) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<s.length; i++){
			sb.append(s[i]).append(delim);
		}
		return sb.deleteCharAt(sb.length()-1).toString();
	}

	/**
	 * Joins a collection of strings with the specified delimiter
	 * @param s The strings to join
	 * @param delim The delimiter
	 * @return The joined strings
	 */
	public static String join(AbstractCollection<String> s, String delim) {
		return StringUtil.join(s.toArray(new String[0]), delim);
	}
	
	/**
	 * Given an array of strings returns them joined with a valid URI path separator.
	 * @param s The strings to join
	 * @return the resulting path
	 */
	public static String URIJoin(String[] s){
		return StringUtil.join(s, "/").replaceAll("/[/]*", "/");
	}
	
	/**
	 * Given a collection of strings returns them joined with a valid URI path separator.
	 * @param s The strings to join
	 * @return the resulting path
	 */
	public static String URIJoin(AbstractCollection<String> s){
		return StringUtil.URIJoin(s.toArray(new String[0]));
	}
}
