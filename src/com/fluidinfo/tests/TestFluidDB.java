package com.fluidinfo.tests;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.UUID;

import org.junit.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.fluidinfo.*;
import com.fluidinfo.fom.*;
import com.fluidinfo.fom.Object;
import com.fluidinfo.utils.StringUtil;

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
	public void testGetLoggedInUser() throws Exception {
	    FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
        assertEquals(FluidConnector.SandboxURL, fdb.getURL());
	    fdb.Login(this.testUsername, this.testPassword);
	    User u = fdb.getLoggedInUser();
	    assertEquals(this.testUsername, u.getName());
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
    public void testCreateObject() throws FOMException, JSONException, FluidException, IOException {
        FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
        assertEquals(FluidConnector.SandboxURL, fdb.getURL());
        String about = "jFluidDBUnitTest: "+UUID.randomUUID().toString();
        Object o = fdb.createObject(about);
        assertEquals(true, o.getId().length()>0);
        assertEquals(o.getAbout(), about);
    }
	
	@Test
	public void testGetObject() throws FOMException, FluidException, IOException, JSONException {
	    FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
        assertEquals(FluidConnector.SandboxURL, fdb.getURL());
        User user = fdb.getUser(this.testUsername);
        Object o = fdb.getObject(user.getId());
        assertEquals(user.getId(), o.getId());
        assertEquals(true, o.getAbout().length()>0);
        assertEquals(true, o.getTagPaths().length>0);
	}
	
	@Test
	public void testSearchObjects() throws Exception {
	    // Lets set up a little scenario we can use to play with
	    FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
        fdb.Login(this.testUsername, this.testPassword);
        User u = fdb.getLoggedInUser();
        Namespace root = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = root.createTag(tagName, "For the purposes of testing jFluidDB", true);
        Object o = fdb.getObject(u.getId());
        // Lets tag and search
        o.tag(t, 1);
        String[] path = {root.getName(), t.getName()};
        String[] result = fdb.searchObjects(StringUtil.URIJoin(path)+" = 1");
        assertEquals(1, result.length);
        assertEquals(o.getId(), result[0]);
        // clean up
        o.deleteTag(t);
        t.delete();
	}
	
	@Test(expected=FluidException.class)
    public void testSearchObjectsFail() throws FluidException, IOException, JSONException {
        // Can't get the name from this path
	    FluidDB fdb = new FluidDB(FluidConnector.SandboxURL);
	    fdb.searchObjects("/foo/bar = 1");
    }
}
