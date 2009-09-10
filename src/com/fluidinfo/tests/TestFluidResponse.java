package com.fluidinfo.tests;

import junit.framework.TestCase;
import org.junit.*;
import com.fluidinfo.*;

public class TestFluidResponse extends TestCase{
	
	/**
	 * Tests that the constructor for the FluidResponse class works properly
	 */
	@Test 
	public void testFluidResponseConstructor()
	{
		FluidResponse fr = new FluidResponse(200, "OK", "application/json", "{\"value\": \"testing 123\"}");
		assertEquals(200, fr.getResponseCode());
		assertEquals("OK", fr.getResponseMessage());
		assertEquals("application/json", fr.getResponseContentType());
		assertEquals("{\"value\": \"testing 123\"}", fr.getResponseContent());
	}
}
