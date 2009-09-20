package com.fluidinfo.tests;

import java.util.Hashtable;
import java.util.UUID;

import junit.framework.TestCase;
import org.junit.*;

import com.fluidinfo.*;
import com.fluidinfo.utils.Method;

import org.json.JSONObject;

/**
 * Making use of jUnit to exercise all the possible call types
 * 
 * @author ntoll
 *
 */
public class TestFluidConnector extends TestCase {
	
	/**
	 * The connection to FluidDB
	 */
	FluidConnector fdb = null;
	
	/**
	 * The ID of an object that is known to exist in the database for the 
	 * purposes of testing
	 */
	private String objectID = "";
	
	/**
	 * Gets the temporary object's id being used in the unit tests
	 * @return the temporary object's id used in the unit tests
	 */
	public String getObjectID()
	{
		return this.objectID;
	}
	
	public String username = "";
	public String password = "";
	
	@Before
	protected void setUp() throws Exception {
		this.fdb = TestUtils.getFluidConnectionWithSettings();
		// lets make sure we *always* have an object that exists for the purposes of testing
		String about = "{\"about\": \"Created for the purpose of unit-testing the jFluidDB library\"}";
		try {
			FluidResponse result = this.fdb.Call(Method.POST, "/objects", about);
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			this.objectID = jsonResult.getString("id");
		} catch (Exception e) {
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test an HTTP GET call
	 * @throws FluidException 
	 */
	@Test 
	public void testCallGet() throws FluidException
	{
		// A call with the query language
		Hashtable<String, String> args = new Hashtable<String, String>();
		args.put("query", "has fluiddb/users/username");
		try {
			FluidResponse result = this.fdb.Call(Method.GET, "/objects", "", args);
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(true, jsonResult.getJSONArray("ids").length()>0);
			// A call with no other args
			// Get the value of the tag "fluiddb/about" for the object with the UUID found
			// in this.objectID
			result = this.fdb.Call(Method.GET, "/objects/"+this.objectID+"/fluiddb/about");
			assertEquals("Created for the purpose of unit-testing the jFluidDB library", result.getResponseContent());
			assertEquals(200, result.getResponseCode());
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test an HTTP POST call
	 */
	@Test 
	public void testCallPost() throws FluidException
	{
		// Lets post to namespaces to create a new one
		String newNamespaceName = UUID.randomUUID().toString();
		try {
			JSONObject jsonPayload = new JSONObject();
			jsonPayload.put("description", "Created for the purpose of unit-testing the JFluidDB client library");
			jsonPayload.put("name", newNamespaceName);
			FluidResponse result = this.fdb.Call(Method.POST, "/namespaces/"+this.fdb.getUsername(), jsonPayload.toString());
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(201, result.getResponseCode());
			assertEquals(true, jsonResult.has("id"));
			assertEquals(true, jsonResult.has("URI"));
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test an HTTP PUT call
	 * @throws FluidException 
	 */
	@Test 
	public void testCallPut() throws FluidException
	{
		// Lets post to namespaces to create a new one
		String newNamespaceName = UUID.randomUUID().toString();
		try {
			JSONObject jsonPayload = new JSONObject();
			jsonPayload.put("description", "Created for the purpose of unit-testing the JFluidDB client library");
			jsonPayload.put("name", newNamespaceName);
			FluidResponse result = this.fdb.Call(Method.POST, "/namespaces/"+this.fdb.getUsername(), jsonPayload.toString());
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(201, result.getResponseCode());
			assertEquals(true, jsonResult.has("id"));
			assertEquals(true, jsonResult.has("URI"));
			// so that worked... now lets update the new namespace's description with a PUT 
			jsonPayload = new JSONObject();
			jsonPayload.put("description", "Updated");
			result = this.fdb.Call(Method.PUT, "/namespaces/"+this.fdb.getUsername()+"/"+newNamespaceName, jsonPayload.toString());
			assertEquals(204, result.getResponseCode());
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test an HTTP HEAD call
	 * @throws FluidException 
	 */
	@Test 
	public void testCallHead() throws FluidException
	{
		// The HEAD method on objects can be used to test whether an object has a given
        // tag or not, without retrieving the value of the tag. 
		try {
			// This object *does* have the tag
			FluidResponse result = this.fdb.Call(Method.HEAD, "/objects/"+this.objectID+"/fluiddb/about");
			assertEquals(200, result.getResponseCode());
			
			// This object *doesn't* have the tag (404 will also be returned if the
            // user doesn't have SEE permission for the tag)
			result = this.fdb.Call(Method.HEAD, "/objects/"+this.objectID+"/fluiddb/users/username");
			assertEquals(404, result.getResponseCode());
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test an HTTP DELETE call
	 * @throws FluidException 
	 */
	@Test 
	public void testCallDelete() throws FluidException
	{
		// Lets post to namespaces to create a new one
		String newNamespaceName = UUID.randomUUID().toString();
		try {
			JSONObject jsonPayload = new JSONObject();
			jsonPayload.put("description", "Created for the purpose of unit-testing the JFluidDB client library");
			jsonPayload.put("name", newNamespaceName);
			FluidResponse result = this.fdb.Call(Method.POST, "/namespaces/"+this.fdb.getUsername(), jsonPayload.toString());
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(201, result.getResponseCode());
			assertEquals(true, jsonResult.has("id"));
			assertEquals(true, jsonResult.has("URI"));
			// so that worked... now lets delete the new namespace with a DELETE 
			result = this.fdb.Call(Method.DELETE, "/namespaces/"+this.fdb.getUsername()+"/"+newNamespaceName);
			assertEquals(204, result.getResponseCode());
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	/**
	 * Test the various user credential call options
	 * @throws FluidException 
	 */
	@Test 
	public void testUserCredentials() throws FluidException
	{
		try {
			// Check we can do something that requires authorization with the
			// credentials supplied in this test fixture
			String newNamespaceName = UUID.randomUUID().toString();
			JSONObject jsonPayload = new JSONObject();
			jsonPayload.put("description", "Created for the purpose of unit-testing the JFluidDB client library");
			jsonPayload.put("name", newNamespaceName);
			FluidResponse result = this.fdb.Call(Method.POST, "/namespaces/"+this.fdb.getUsername(), jsonPayload.toString());
			JSONObject jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(201, result.getResponseCode());
			assertEquals(true, jsonResult.has("id"));
			assertEquals(true, jsonResult.has("URI"));
			
			// Now lets try it with the wrong credentials
			this.fdb.setPassword("not_a_valid_password_123");
			newNamespaceName = UUID.randomUUID().toString();
			result = this.fdb.Call(Method.POST, "/namespaces/"+this.fdb.getUsername(), jsonPayload.toString());
			assertEquals(401, result.getResponseCode());
			
			// Lets try something that doesn't require credentials at all
			this.fdb.setPassword("");
			this.fdb.setUsername("");
			Hashtable<String, String> args = new Hashtable<String, String>();
			args.put("query", "has fluiddb/users/username");
			result = this.fdb.Call(Method.GET, "/objects", "", args);
			jsonResult = TestUtils.getJsonObject(result.getResponseContent());
			assertEquals(true, jsonResult.getJSONArray("ids").length()>0);
		} catch (Exception e) {
			throw new FluidException(e);
		}
	}
	
	/**
	 * For GET and PUT, make sure we get back json (if specified)
	 * @throws FluidException 
	 */
	@Test 
	public void testAlwaysUseJson() throws FluidException
	{
		try {
			// GET without json
			assertEquals(false, this.fdb.getAlwaysUseJson());
			FluidResponse result = this.fdb.Call(Method.GET, "/objects/"+this.objectID+"/fluiddb/about");
			assertEquals(200, result.getResponseCode());
			assertEquals("text/plain; charset=UTF-8", result.getResponseContentType());
			assertEquals("Created for the purpose of unit-testing the jFluidDB library", result.getResponseContent());
			// GET (with json)
			this.fdb.setAlwaysUseJson(true);
			result = this.fdb.Call(Method.GET, "/objects/"+this.objectID+"/fluiddb/about");
			assertEquals(200, result.getResponseCode());
			assertEquals("application/json", result.getResponseContentType());
			assertEquals("{\"value\": \"Created for the purpose of unit-testing the jFluidDB library\"}", result.getResponseContent());
		} catch (Exception e)
		{
			throw new FluidException(e);
		}
	}
	
	@Test
	public void testGetUrl() {
		assertEquals(FluidConnector.SandboxURL, this.fdb.getUrl());
	}
}
