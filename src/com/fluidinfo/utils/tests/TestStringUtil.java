package com.fluidinfo.utils.tests;

import static org.junit.Assert.*;
import java.util.Vector;
import org.junit.*;
import com.fluidinfo.utils.*;

/**
 * Checking the string utils (currently only join) work correctly
 * 
 * @author ntoll
 *
 */
public class TestStringUtil {
	
	@Test
	public void testJoin(){
		// Single item in the path
		Vector<String> paths = new Vector<String>();
		paths.add("/foo");
		assertEquals("/foo", StringUtil.join(paths.toArray(new String[0]), "/"));
		// Empty path
		paths.clear();
		assertEquals("", StringUtil.join(paths.toArray(new String[0]), "/"));
		// two elements in the path
		paths.add("/foo");
		paths.add("bar");
		assertEquals("/foo/bar", StringUtil.join(paths.toArray(new String[0]), "/"));
		assertEquals("/foo/bar", StringUtil.join(paths, "/")); // make sure it works with a collection
	}
	
	@Test
	public void testURIJoin() {
		Vector<String> paths = new Vector<String>();
		// elements that already contain the delimiter
		paths.add("/foo");
		paths.add("bar/");
		paths.add("/baz");
		assertEquals("/foo/bar/baz", StringUtil.URIJoin(paths.toArray(new String[0])));
		assertEquals("/foo/bar/baz", StringUtil.URIJoin(paths)); // make sure it works with a collection
	}
	
}
