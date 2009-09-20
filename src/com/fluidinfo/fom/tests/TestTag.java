package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.*;
import org.json.JSONException;
import com.fluidinfo.*;
import com.fluidinfo.fom.*;
import com.fluidinfo.tests.TestUtils;
import com.fluidinfo.utils.StringUtil;

import java.util.UUID;

public class TestTag extends Tag {
	/**
	 * Silly constructor to make this test class work
	 * @throws Exception 
	 */
	public TestTag() throws Exception {
		super(TestUtils.getFluidConnectionWithSettings(), "testID", "test");
		this.fdb = TestUtils.getFluidConnectionWithSettings();
	}
	
	@Test
	public void testConstructor() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		String[] ctorPath = {this.fdb.getUsername(), newName};
		String ctorURI = StringUtil.URIJoin(ctorPath);
		Tag ctorTag = new Tag(this.fdb, newTag.getId(), ctorURI);
		assertEquals(newTag.getId(), ctorTag.getId());
		// will cause a get from FluidDB - making sure we've supplied enough information in the ctor
		assertEquals(newTag.getDescription(), ctorTag.getDescription());
		assertEquals(true, ctorTag.isIndexed());
		
		// now lets try the other ctor signature
		Tag bigCtorTag = new Tag(this.fdb, "id", true, "description", "path");
		// and make sure all the getters return the right thing
		assertEquals("id", bigCtorTag.getId());
		assertEquals(true, bigCtorTag.isIndexed());
		assertEquals("description", bigCtorTag.getDescription());
		assertEquals("path", bigCtorTag.getName());
		assertEquals("/tags/path", bigCtorTag.getPath());
		newTag.delete();
	}
	
	@Test
	public void testGetItem() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		String[] getPath = {this.fdb.getUsername(), newName};
		String getURI = StringUtil.URIJoin(getPath);
		Tag getTag = new Tag(this.fdb, "", getURI);
		getTag.getItem();
		// will cause a get from FluidDB - if the IDs are the same then we got the right tag
		assertEquals(newTag.getId(), getTag.getId());
		newTag.delete();
	}
	
	@Test
	public void testGetName() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		assertEquals(newName, newTag.getName());
		newTag.delete();
	}
	
	@Test
	public void testGetSetDescription() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		newTag.setDescription("test description");
		String[] getPath = {this.fdb.getUsername(), newName};
		String getURI = StringUtil.URIJoin(getPath);
		Tag getTag = new Tag(this.fdb, "", getURI);
		assertEquals("test description", getTag.getDescription());
		getTag.delete();
	}
	
	@Test
	public void testIsIndexed() throws FOMException, JSONException, FluidException, IOException {
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		testNamespace.createTag(newName, "This is a test tag", true);
		String[] getPath = {this.fdb.getUsername(), newName};
		String getURI = StringUtil.URIJoin(getPath);
		Tag getTag = new Tag(this.fdb, "", getURI);
		assertEquals(true, getTag.isIndexed());
		getTag.delete();
	}
	
	@Test
	public void testDelete() throws FOMException, JSONException, FluidException, IOException {
		// Create a new tag to delete
		Namespace testNamespace = new Namespace(this.fdb, "", this.fdb.getUsername());
		String newName = UUID.randomUUID().toString();
		Tag newTag = testNamespace.createTag(newName, "This is a test tag", true);
		// check the change happens at FluidDB
		testNamespace.getItem();
		assertEquals(true, TestUtils.contains(testNamespace.getTagNames(), newName));
		// delete the tag
		newTag.delete();
		// need to update from FluidDB
		testNamespace.getItem();
		// test the tag doesn't exist anymore
		assertEquals(false, TestUtils.contains(testNamespace.getTagNames(), newName));
	}
}
