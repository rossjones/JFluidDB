package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;
import org.junit.*;

import com.fluidinfo.fom.*;

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
		super(null);
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
	
}
