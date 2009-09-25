/*
 * Copyright (c) 2009 Nicholas H.Tollervey and others
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

/**
 * 
 * A simple class used to hold the raw results from a call to FluidDB
 * 
 * @author ntoll
 *
 */
public class FluidResponse {
	
	private int responseCode;
	private String responseMessage;
	private String responseContentType;
	private String responseContent;
	private String responseError;
	private String responseRequestID;
	
	/**
	 * Gets the HTTP response code returned from FluidDB (e.g. 200, 404)
	 * 
	 * @return the HTTP response code returned from FluidDB
	 */
	public int getResponseCode(){
		return this.responseCode;
	}
	
	/**
	 * Gets the response message returned from FluidDB (e.g. OK, Not Found etc)
	 * 
	 * @return the response message returned from FluidDB
	 */
	public String getResponseMessage(){
		return this.responseMessage;
	}
	
	/**
	 * The content-type returned from FluidDB (usually "application/json")
	 * 
	 * @return the content-type returned from FluidDB
	 */
	public String getResponseContentType(){
		return this.responseContentType;
	}
	
	/**
	 * The raw content of the response from FluidDB (see the content-type to work out what it is)
	 * 
	 * @return the raw content of the response from FluidDB
	 */
	public String getResponseContent(){
		return this.responseContent;
	}
	
	/**
	 * The error message returned by FluidDB
	 * 
	 * @return the error message returned by FluidDB
	 */
	public String getResponseError(){
		return this.responseError;
	}
	
	/**
	 * The request ID that caused the error - useful for feedback and reporting bugs to FluidInfo
	 * 
	 * @return the ID of the request that caused the error
	 */
	public String getErrorRequestID(){
		return this.responseRequestID;
	}
	
	/**
	 * Constructor
	 * 
	 * @param ResponseCode - the HTTP response code returned from FluidDB (e.g. 200, 404)
	 * @param ResponseMessage - the response message returned from FluidDB (e.g. OK, Not Found etc)
	 * @param ResponseContentType - the content-type returned from FluidDB (usually "application/json")
	 * @param ResponseContent - the raw content of the response from FluidDB
	 */
	public FluidResponse(int ResponseCode, String ResponseMessage, String ResponseContentType, String ResponseContent){
		this(ResponseCode, ResponseMessage, ResponseContentType, ResponseContent, null, null);
	}
	
	/**
	 * Constructor
	 * 
	 * @param ResponseCode - the HTTP response code returned from FluidDB (e.g. 200, 404)
	 * @param ResponseMessage - the response message returned from FluidDB (e.g. OK, Not Found etc)
	 * @param ResponseContentType - the content-type returned from FluidDB (usually "application/json")
	 * @param ResponseContent - the raw content of the response from FluidDB
	 * @param ResponseError - the 
	 * @param RequestID
	 */
	public FluidResponse(int ResponseCode, String ResponseMessage, String ResponseContentType, String ResponseContent, String ResponseError, String RequestID) {
		this.responseCode=ResponseCode;
		this.responseMessage=ResponseMessage;
		this.responseContentType=ResponseContentType;
		this.responseContent=ResponseContent;
		this.responseError=ResponseError;
		this.responseRequestID=RequestID;
	}
}
