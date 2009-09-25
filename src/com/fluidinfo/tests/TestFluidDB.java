package com.fluidinfo.tests;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.fluidinfo.*;
import com.fluidinfo.fom.*;

/**
 * Tests the core FluidDB class
 * 
 * @author ntoll
 *
 */
public class TestFluidDB extends FluidDB{
	private String testUsername = "";
	private String testPassword = "";
	
	@Before
	public void setUp() throws Exception {
		JSONObject credentials = TestUtils.getSettings();
        this.testUsername = credentials.getString("username");
        this.testPassword = credentials.getString("password");
	}
	
	@Test
	public void testFluidDBConstructor() {
		FluidDB fdb = new FluidDB();
		assertEquals(FluidConnector.URL, fdb.getURL());
		fdb = new FluidDB(FluidConnector.SandboxURL);
		assertEquals(FluidConnector.SandboxURL, fdb.getURL());
	}
	
	@Test
	public void testFluidDBLoginLogout() {
		this.Login(this.testUsername, this.testPassword);
		assertEquals(this.testUsername, this.fdb.getUsername());
		assertEquals(this.testPassword, this.fdb.getPassword());
		this.Logout();
		assertEquals("", this.fdb.getUsername());
		assertEquals("", this.fdb.getPassword());
	}
	
	@Test
	public void testGetNamespace() throws FOMException, FluidException, IOException, JSONException {
		FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
		assertEquals(FluidConnector.SandboxURL, fdb.getURL());
		// lets get the test user's root namespace (the same as their username)
		Namespace ns = fdb.getNamespace(this.testUsername);
		assertEquals(this.testUsername, ns.getName());
		// The following test proves an id was retrieved from FluidDB
		assertEquals(true, ns.getId().length()>0);
	}
	
	@Test
	public void testGetTag() throws FOMException, FluidException, IOException, JSONException {
		FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
		assertEquals(FluidConnector.SandboxURL, fdb.getURL());
		// lets get the username tag from the fluidDB user
		Tag usernameTag = fdb.getTag("fluiddb/users/username");
		assertEquals("username", usernameTag.getName());
	}
    
    @Test
    public void testGetUser() throws FOMException, FluidException, IOException, JSONException {
        FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
        assertEquals(FluidConnector.SandboxURL, fdb.getURL());
        User user = fdb.getUser(this.testUsername);
        assertEquals(this.testUsername, user.getName());
        assertEquals(true, user.getId().length()>0);
    }
	
	@Test
	public void testGetObject() {
		// TODO: Finish
	}
}
