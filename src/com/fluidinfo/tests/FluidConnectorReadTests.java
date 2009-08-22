/*
 * Copyright (c) 2009 Ross Jones 
 *   - Derived from code by Nicholas H.Tollervey
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.fluidinfo.tests;

import java.util.*;
import com.fluidinfo.*;

/**
* Tests the read (GET) functionality of the FluidConnector
**/
public class FluidConnectorReadTests 
{
    FluidConnector c = new FluidConnector();
    
    public void start() throws FluidException
    {
        testTopLevelNamespaceRead();
        testTopLevelNamespaceReadWithArguments();
        testKnownNamespaceRead();
        testFakeKnownNamespaceRead();
        
        testReadUserKnown();
        testReadUserFake();        
    }
 
    private void testTopLevelNamespaceRead() throws FluidException
    {
        String resp = c.Call(Method.GET, "namespaces");
        TestRunner.Assert( resp != null, "Top level namespace is missing");
    }

    private void testTopLevelNamespaceReadWithArguments() throws FluidException
    {
        Hashtable<String,String> args = new Hashtable<String,String>();
        args.put("returnDescription", "True");
        args.put("returnNamespaces", "True");
        args.put("returnTags", "True");                
        String resp = c.Call(Method.GET, "namespaces", "", args);
        TestRunner.Assert( resp != null, "Failed to retrieve top-level namespace with args");
    }
    
    private void testKnownNamespaceRead() throws FluidException
    {
        Hashtable<String,String> args = new Hashtable<String,String>();
        args.put("returnDescription", "True");
        args.put("returnNamespaces", "True");
        args.put("returnTags", "True");                
        
        String resp = c.Call(Method.GET, "namespaces/daepark", "", args);
        TestRunner.Assert( resp != null, "Requested a namespace that doesn't exist");
    }
    
    private void testFakeKnownNamespaceRead() throws FluidException
    {
        Hashtable<String,String> args = new Hashtable<String,String>();
        args.put("returnDescription", "True");
        args.put("returnNamespaces", "True");
        args.put("returnTags", "True");                
        
        try
        {
            c.Call(Method.GET, "namespaces/womble_dont_exist", "", args);
            TestRunner.Assert( 1==0, "This namespace shouldn't exist");            
        }
        catch( FluidException fe )
        {
        }
    }

    private void testReadUserKnown() throws FluidException
    {
        String resp = c.Call(Method.GET, "users/rossjones");
        TestRunner.Assert( resp != null, "Argh, my user account has been deleted");            
    }
    
    private void testReadUserFake() throws FluidException
    {
        try
        {
            c.Call(Method.GET, "users/noexistyuseraccount");
            TestRunner.Assert( 1==0, "This user shouldn't exist");            
        }
        catch( FluidException fe )
        {
        }
    }
    
}
