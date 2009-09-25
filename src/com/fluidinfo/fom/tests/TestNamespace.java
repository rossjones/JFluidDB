package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.*;
import org.json.JSONException;
import com.fluidinfo.*;
import com.fluidinfo.fom.*;
import com.fluidinfo.tests.TestUtils;
import java.util.UUID;

/**
 * Exercise the Namespace class
 * 
 * @author ntoll
 *
 */
public class TestNamespace extends Namespace {
	/**
	 * Silly constructor to make this test class work
	 * @throws Exception 
	 */
	public TestNamespace() throws Exception {
		super(TestUtils.getFluidConnectionWithSettings(), "testID", "test");
	}
	
	@Test
	public void testConstructor() {
		// using the information in the overridden ctor define above
		assertEquals("testID", this.getId());
		assertEquals("/namespaces/test", this.getPath());
	}
	
	@Test
	public void testGetItem() throws Exception {
		// We'll make use of the user's default root namespace (defined by their username)
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		// There isn't anything there
		assertEquals("", testNamespace.getId());
		// Lets call FluidDB and populate the fields... now there should be an ID
		testNamespace.getItem();
		assertEquals(true, testNamespace.getId().length()>0);
	}
	
	@Test
	public void testGetSetDescription() throws FOMException, JSONException, FluidException, IOException {
		// We'll make use of the user's default root namespace (defined by their username)
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		// Lets set the description
		testNamespace.setDescription("This is a test description");
		assertEquals("This is a test description", testNamespace.getDescription());
		// Lets get the same description from a newly instantiated object referencing the same
		// namespace so the description gets automagically pulled from FluidDB
		Namespace testNamespace2 = new Namespace(this.fdb, "", this.fdb.getUsername());
		assertEquals("This is a test description", testNamespace2.getDescription());
	}
	
	@Test
	public void testCreateNamespace() throws FOMException, FluidException, JSONException, IOException {
		// Lets create a new namespace underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Namespace newNamespace = testNamespace.createNamespace(newName, "This is a test namespace");
		assertEquals(newName, newNamespace.getName());
		assertEquals("This is a test namespace", newNamespace.getDescription());
		assertEquals(true, newNamespace.getId().length()>0);
		assertEquals(true, TestUtils.contains(testNamespace.getNamespaceNames(), newName));
		newNamespace.delete();
	}
	
	@Test
	public void testDelete() throws FOMException, FluidException, JSONException, IOException {
		// Lets create a new namespace underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Namespace newNamespace = testNamespace.createNamespace(newName, "This is a test namespace");
		assertEquals(true, TestUtils.contains(testNamespace.getNamespaceNames(), newName));
		newNamespace.delete();
		testNamespace.getItem();
		assertEquals(false, TestUtils.contains(testNamespace.getNamespaceNames(), newName));	
	}
	
	@Test
	public void testCreateTag() throws FOMException, JSONException, FluidException, IOException {
		// Lets create a tag underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		// if we successfully created a tag there'll be an id
		assertEquals(true, newTag.getId().length()>0);
		testNamespace.getItem(); // not really needed
		assertEquals(true, TestUtils.contains(testNamespace.getTagNames(), newName));
		newTag.delete();
		testNamespace.getItem();
		assertEquals(false, TestUtils.contains(testNamespace.getTagNames(), newName));
	}
	
	@Test
	public void testGetTagNames() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		// check the change happens at FluidDB
		testNamespace.getItem();
		assertEquals(true, TestUtils.contains(testNamespace.getTagNames(), newName));
		// delete the tag
		newTag.delete();
		// this *won't* mean the tag is removed from the local object
		assertEquals(true, TestUtils.contains(testNamespace.getTagNames(), newName));
		// need to update from FluidDB
		testNamespace.getItem();
		assertEquals(false, TestUtils.contains(testNamespace.getTagNames(), newName));
	}
	
	@Test
	public void testGetNamespaceNames() throws FOMException, FluidException, JSONException, IOException {
		// Lets create a new namespace underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Namespace newNamespace = testNamespace.createNamespace(newName, "This is a test namespace");
		// check the change happens at FluidDB
		testNamespace.getItem();
		assertEquals(true, TestUtils.contains(testNamespace.getNamespaceNames(), newName));
		// delete the namespace
		newNamespace.delete();
		// this *won't* mean the namespace is removed from the local object
		assertEquals(true, TestUtils.contains(testNamespace.getNamespaceNames(), newName));
		// need to update from FluidDB
		testNamespace.getItem();
		assertEquals(false, TestUtils.contains(testNamespace.getNamespaceNames(), newName));
	}
	
	@Test
	public void testGetTag() throws FOMException, JSONException, FluidException, IOException {
		// Lets create a tag underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		// if we successfully created a tag we'll be able to get it from FluidDB
		Tag gotTag = testNamespace.getTag(newName);
		assertEquals(newTag.getId(), gotTag.getId());
		gotTag.delete();
	}
	
	@Test
	public void testGetNamespace() throws FOMException, FluidException, JSONException, IOException {
		// Lets create a new namespace underneath the user's default root namespace
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Namespace newNamespace = testNamespace.createNamespace(newName, "This is a test namespace");
		// if we successfully created a new namespace we'll be able to get it from FluidDB
		Namespace gotNamespace = testNamespace.getNamespace(newName);
		assertEquals(newNamespace.getId(), gotNamespace.getId());
		gotNamespace.delete();
	}
}
