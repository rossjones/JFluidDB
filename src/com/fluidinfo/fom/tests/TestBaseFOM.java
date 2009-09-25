package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import com.fluidinfo.FluidException;
import com.fluidinfo.FluidResponse;
import com.fluidinfo.fom.*;
import com.fluidinfo.tests.TestUtils;
import com.fluidinfo.utils.Method;
import com.fluidinfo.utils.StringUtil;

/**
 * Checking all methods are exercised appropriately (attempting close to 100% coverage)
 * 
 * By using annotations and extending BaseFOM I get access to the protected methods ;-)
 * 
 * @author ntoll
 *
 */
public class TestBaseFOM extends BaseFOM {
	
	/**
	 * Silly constructor to make this test class work
	 */
	public TestBaseFOM() {
		super(null, "testID");
	}

	@Test
	public void testGetNameFromPathWorkingExamples() throws FOMException {
		// Lets make sure we get the name from the path
		String path1 = "/foo/bar/baz";
		String path2 = "/foo";
		String path3 = "foo";
		String path4 = "/foo/bar/baz/";
		
		assertEquals("baz", this.GetNameFromPath(path1));
		assertEquals("foo", this.GetNameFromPath(path2));
		assertEquals("foo", this.GetNameFromPath(path3));
		assertEquals("baz", this.GetNameFromPath(path4));
	}
	
	@Test(expected=FOMException.class)
	public void testGetNameFromPathNoPath() throws FOMException {
		// Can't get the name from this path
		this.GetNameFromPath("");
	}
	
	@Test(expected=FOMException.class)
	public void testGetNameFromPathOnlySlashes() throws FOMException {
		// Can't get the name from this path either!
		this.GetNameFromPath("//");
	}
	
	@Test
	public void testGetPath() {
		this.rootPath="/tags";
		this.path="foo";
		assertEquals("/tags/foo", this.getPath());
	}
	
	@Test
	public void testGetId() {
		assertEquals("testID", this.getId());
	}
	
	@Test
	public void testGetJsonObjectWorks() throws JSONException, FOMException {
		// With a single String
		String jsonInput = "{ \"foo\": \"bar\"}";
		JSONObject jObj = this.getJsonObject(jsonInput);
		assertEquals("bar", jObj.get("foo"));
		// With a FluidResponse
		FluidResponse fR = new FluidResponse(200, "", "application/json", jsonInput);
		jObj = this.getJsonObject(fR);
		assertEquals("bar", jObj.get("foo"));
	}
	
	@Test(expected=FOMException.class)
	public void testGetJsonObjectException() throws JSONException, FOMException {
		// Lets make it throw the exception because of the wrong response content type
		String jsonInput = "{ \"foo\": \"bar\"}";
		FluidResponse fR = new FluidResponse(200, "", "plain/text", jsonInput);
		this.getJsonObject(fR); // will throw the expected exception
	}
	
	@Test
	public void testGetStringArrayFromJSONArray() throws JSONException {
		String jsonInput = "{\"foo\": [ \"bar\", \"baz\"]}";
		JSONObject jObj = this.getJsonObject(jsonInput);
		String[] result = this.getStringArrayFromJSONArray(jObj.getJSONArray("foo"));
		assertEquals(2, result.length);
		assertEquals("bar", result[0]);
		assertEquals("baz", result[1]);
	}
	
	@Test
	public void testCall() throws Exception {
		this.fdb = TestUtils.getFluidConnectionWithSettings();
		// lets get the information about the current user - shortest call signature
		this.rootPath="/users";
		this.path=this.fdb.getUsername();
		FluidResponse r = this.Call(Method.GET, 200, "");
		assertEquals(200, r.getResponseCode());
		// lets send a body in a put to the user's root namespace
		this.rootPath="/namespaces";
		JSONObject body = this.getJsonObject("{\"description\":\"a test\"}");
		r = this.Call(Method.PUT, 204, body.toString());
		// lets get the user's root namespace's information  - call sig includes the Hashtable of args
		Hashtable<String, String> args = new Hashtable<String, String>();
		args.put("returnDescription", "True");
		r = this.Call(Method.GET, 200, "", args);
		assertEquals(200, r.getResponseCode());
		JSONObject jsonResult = this.getJsonObject(r);
		assertEquals("a test", jsonResult.getString("description"));
		// Finally, lets call with the final sig: with the path specified
		String[] callPath = {"/users", this.path}; // remember the rootPath currently = "/namespaces"
		r = this.Call(Method.GET, 200, "", new Hashtable<String, String>(), StringUtil.URIJoin(callPath));
		assertEquals(200, r.getResponseCode());
	}
	
	@Test
	public void testCallWithException() throws Exception {
		this.fdb = TestUtils.getFluidConnectionWithSettings();
		this.rootPath="/namespaces";
		this.path="fluiddb"; // we don't have permissions for this namespace - it's the system account
		// Lets try to update the system account's namespace to throw an exception
		JSONObject body = this.getJsonObject("{\"description\":\"a test\"}");
		try {
			this.Call(Method.PUT, 204, body.toString());
		} catch (FluidException ex) {
			assertEquals(true, ex.getMessage().startsWith("FluidDB returned the following error: 401 (Unauthorized) TPathPermissionDenied - with the request ID: "));
		}
	}

	@Override
	public void getItem() throws FluidException, IOException, FOMException,
			JSONException {
		// Doesn't do anything in this class but here because BaseFOM implements FOMInterface		
	}
}