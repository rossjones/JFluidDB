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
 * See http://doc.fluidinfo.com/fluidDB/objects.html
 * 
 * FluidDB is conceptually very simple: it holds a large number of objects, all of 
 * a single flexible kind, and it provides the means to create, modify, and retrieve 
 * these objects.
 * 
 * A FluidDB object is just a collection of tags, usually with values.
 * 
 * @author ntoll
 *
 */
public class Object extends BaseFOM{

    private String about = null;
    
    private String[] tagPaths = null;
    
    /**
     * Constructor
     * 
     * @param fdb The connection to FluidDB
     * @param id The id of the object in FluidDB
     * @param path The path to the object in FluidDB
     * @throws FOMException
     */
    public Object(FluidConnector fdb, String id, String path) throws FOMException {
        super(fdb, id);
        this.rootPath = "/objects";
        this.path = path;
        if(id==null || id.length()==0){
            this.id = this.GetNameFromPath(path);
        }
    }
    
    @Override
    public void getItem() throws FluidException, IOException, FOMException, JSONException {
        Hashtable<String, String> args = new Hashtable<String, String>();
        args.put("showAbout", "True");
        FluidResponse response = this.Call(Method.GET, 200, "", args);
        JSONObject jsonResult = this.getJsonObject(response);
        this.about = jsonResult.getString("about");
        if(jsonResult.has("tagPaths")){
            this.tagPaths = StringUtil.getStringArrayFromJSONArray(jsonResult.getJSONArray("tagPaths"));
        } else {
            this.tagPaths = new String[0];
        }
    }
    
    /**
     * FluidDB has a special tag known as the about tag.
     * 
     * When an application creates an object, it can (optionally) specify what it intends 
     * the object to be about. This is just a convention, but it is a highly useful one as 
     * it allows users and applications certainty as to what their tags will be associated 
     * with.
     * 
     * This method is a getter for the about tag.
     * 
     * @return the value of the special "about" tag
     * @throws FOMException 
     */
    public String getAbout() throws FOMException {
        // If this.about is null then call back to FluidDB to check its value
        if(this.about==null){
            try {
                this.getItem();
            } catch(Exception ex) {
                throw new FOMException("Unable to get the value of the 'about' tag.", ex);
            }
        }
        return this.about;
    }
    
    /**
     * Returns the full path names of the tags on this object (for which the requesting user 
     * has SEE permission), if any.
     * 
     * @return The full path names of the tags on this object (for which the requesting user 
     * has SEE permission), if any.
     * @throws FOMException 
     */
    public String[] getTagPaths() throws FOMException {
        // If this.tagPaths is null then call back to FluidDB to check its value
        if(this.tagPaths==null){
            try {
                this.getItem();
            } catch(Exception ex) {
                throw new FOMException("Unable to get tag paths.", ex);
            }
        }
        return this.tagPaths;
    }
    
    /**
     * Indicates if the object has a given tag or not, without retrieving 
     * the value of the tag
     * 
     * @param path The path of the tag. e.g. /namespace1/namespace2/tag
     * @return a boolean indication of the presence of the tag on this object
     * @throws IOException 
     * @throws FluidException 
     */
    public boolean hasTag(String path) throws FluidException, IOException {
        String[] pathToTag = {this.rootPath, this.path, path};
        FluidResponse response = this.fdb.Call(Method.HEAD, StringUtil.URIJoin(pathToTag));
        return response.getResponseCode()==200;
    }
    
    /**
     * Indicates if the object has a given tag or not, without retrieving 
     * the value of the tag
     * 
     * @param tag An instance of the tag we're interested in
     * @return a boolean indication of the presence of the tag on this object
     * @throws IOException 
     * @throws FluidException 
     */
    public boolean hasTag(Tag tag) throws FluidException, IOException {
        return this.hasTag(tag.path);
    }
    
    /**
     * Delete the referenced tag from the object
     * 
     * @param path The path of the tag. e.g. /namespace1/namespace2/tag
     * @throws FOMException 
     * @throws IOException 
     * @throws FluidException 
     */
    public void deleteTag(String path) throws FOMException, FluidException, IOException {
        String[] pathToTag = {this.rootPath, this.path, path};
        FluidResponse response = this.fdb.Call(Method.DELETE, StringUtil.URIJoin(pathToTag));
        if(response.getResponseCode()==401){
            throw new FOMException("You don't have persmission to do that.");
        }
    }
    
    /**
     * Delete the referenced tag from the object
     * 
     * @param tag An instance of the tag we're interested in
     * @throws IOException 
     * @throws FluidException 
     * @throws FOMException 
     */
    public void deleteTag(Tag tag) throws FOMException, FluidException, IOException {
        this.deleteTag(tag.path);
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated value "null"
     * @param tag the tag to associate with this object
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag) throws FluidException, IOException {
        this.tagPrimitive(tag, "null");
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated boolean value
     * @param tag the tag to associate with this object
     * @param value the boolean value of the tag on this object
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag, boolean value) throws FluidException, IOException {
        this.tagPrimitive(tag, Boolean.toString(value));
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated integer value
     * @param tag the tag to associate with this object
     * @param value the integer value of the tag on this object
     * @throws JSONException
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag, int value) throws JSONException, FluidException, IOException {
        this.tagPrimitive(tag, JSONObject.numberToString(value));
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated double (floating point)
     * value
     * @param tag the tag to associate with this object
     * @param value the floating point value of the tag on this object
     * @throws JSONException
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag, double value) throws JSONException, FluidException, IOException {
        this.tagPrimitive(tag, JSONObject.doubleToString(value));
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated string value
     * @param tag the tag to associate with this object
     * @param value the string value of the tag on this object
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag, String value) throws FluidException, IOException {
        this.tagPrimitive(tag, JSONObject.quote(value));
    }
    
    /**
     * Tag this object with the passed Tag instance and the associated string array value
     * @param tag the tag to associate with this object
     * @param value the string array value of the tag on this object
     * @throws FluidException
     * @throws IOException
     */
    public void tag(Tag tag, String[] values) throws FluidException, IOException {
        StringBuffer jsonArray = new StringBuffer();
        jsonArray.append("[ ");
        for(int i=0; i<values.length; i++) {
            jsonArray.append(JSONObject.quote(values[i]));
            jsonArray.append(",");
        }
        jsonArray.deleteCharAt(jsonArray.length()-1);
        jsonArray.append(" ]");
        this.tagPrimitive(tag, jsonArray.toString());
    }
    
    /**
     * Tags this object with the passed tag and primitive jsonValue
     * @param tag the tag to associate with this object
     * @param jsonValue the value of the tag on this object expressed in json
     * @throws FluidException
     * @throws IOException
     */
    private void tagPrimitive(Tag tag, String jsonValue) throws FluidException, IOException {
        String[] tagPath = {this.getPath(), tag.path};
        this.Call(Method.PUT, 204, jsonValue, StringUtil.URIJoin(tagPath), "application/vnd.fluiddb.value+json");
    }
    
    /**
     * Returns the FluidResponse containing the value of the passed tag
     * @param tag the tag whose value we're interested in
     * @return the tag's value expressed as a FluidResponse
     * @throws FluidException
     * @throws IOException
     */
    public FluidResponse getTagValue(Tag tag) throws FluidException, IOException {
        String[] tagPath = {this.getPath(), tag.path};
        return this.Call(Method.GET, 200, "", new Hashtable<String, String>(), StringUtil.URIJoin(tagPath));
    }
}
