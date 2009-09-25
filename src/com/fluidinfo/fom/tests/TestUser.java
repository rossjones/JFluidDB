package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;
import org.json.JSONException;
import com.fluidinfo.*;
import com.fluidinfo.fom.*;
import com.fluidinfo.tests.TestUtils;
import com.fluidinfo.utils.StringUtil;

/**
 * Exercise the User class
 * 
 * @author ntoll
 *
 */
public class TestUser extends User {
	
	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public TestUser() throws Exception {
		super(TestUtils.getFluidConnectionWithSettings(), "id", TestUtils.getFluidConnectionWithSettings().getUsername());
	}
	
	@Test
	public void testConstructor() {
		// using the information in the overridden ctor defined above
		assertEquals("id", this.getId());
		String[] path = {"/users", this.fdb.getUsername()};
		assertEquals(StringUtil.URIJoin(path), this.getPath());
	}
	
	@Test
	public void testGetItem() throws Exception {
		User testUser = new User(this.fdb, "", this.fdb.getUsername());
		// There isn't anything there
		assertEquals("", testUser.getId());
		// Lets call FluidDB and populate the fields... now there should be an ID
		testUser.getItem();
		assertEquals(true, testUser.getId().length()>0);
	}
	
	@Test
	public void testGetName() throws FOMException, FluidException, IOException, JSONException {
		User testUser = new User(this.fdb, "", this.fdb.getUsername());
		testUser.getItem();
		assertEquals(this.fdb.getUsername(), testUser.getName());
	}
	
	@Test
	public void testGetNamespace() throws FOMException, FluidException, JSONException, IOException {
		// Lets get the root namespace
		this.getItem();
		Namespace rootNamespace = this.RootNamespace();
		String[] path = {"/namespaces", this.fdb.getUsername()};
		assertEquals(StringUtil.URIJoin(path), rootNamespace.getPath());
	}
}
