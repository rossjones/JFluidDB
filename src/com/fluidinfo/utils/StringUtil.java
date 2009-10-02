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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
	
	/**
     * Given a string of json code will return an appropriate representation as a JSONObject
     * @param jsonInput The string of json code to be turned into a JSONObject instance 
     * @return The resulting JSONObject instance
     * @throws JSONException If there was a problem processing the jsonInput
     */
    public static JSONObject getJsonObjectFromString(String jsonInput) throws JSONException{
        JSONTokener jsonResultTokener = new JSONTokener(jsonInput);
        JSONObject jsonResult = new JSONObject(jsonResultTokener);
        return jsonResult;
    }
    
    /**
     * Given a JSONArray (of strings) will return the String[] array representation
     * @param input The JSONArray to process
     * @return The resulting String[]
     * @throws JSONException
     */
    public static String[] getStringArrayFromJSONArray(JSONArray input) throws JSONException {
        String[] result = new String[input.length()];
        for(int i=0; i<input.length(); i++) {
            result[i] = input.getString(i);
        }
        return result;
    }
}
