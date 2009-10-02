package com.fluidinfo.fom.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.fluidinfo.FluidResponse;
import com.fluidinfo.fom.*;
import com.fluidinfo.fom.Object;
import com.fluidinfo.tests.TestUtils;
import java.util.UUID;

/**
 * Exercise the Object class
 * 
 * @author ntoll
 *
 */
public class TestObject extends Object {

    /**
     * Silly constructor to make this test class work
     * @throws Exception 
     */
    public TestObject() throws Exception {
        super(TestUtils.getFluidConnectionWithSettings(), "testID", "testID");
        // So we need to get something with an object ID and pretend this object 
        // represents it... lets use the test user
        User u = new User(this.fdb, "", this.fdb.getUsername());
        u.getItem();
        this.id=u.getId();
        this.path=u.getId();
    }
    
    @Test
    public void testConstructor() {
        // using the information in the overridden ctor define above
        assertEquals(true, this.getId().length()>0);
        assertEquals("/objects/"+this.getId(), this.getPath());
    }

    @Test
    public void testGetItem() throws Exception {
        // Lets try calling the getItem method for a newly instantiated object
        Object o = new Object(this.fdb, "", this.getId());
        o.getItem();
        // If we got the item properly we'll be able to get the value of the "about" tag
        assertEquals(true, o.getAbout().length()>0);
        // As we're using the object id of the test user then every object that is a user 
        // will have at least fluiddb/about
        assertEquals(true, o.getTagPaths().length>1);
    }
    
    @Test
    public void testGetAbout() throws Exception {
        Object o = new Object(this.fdb, "", this.getId());
        // getAbout will call getItem() if about is null (as it will be in a newly instantiated
        // object)
        assertEquals(true, o.getAbout().length()>0);
    }
    
    @Test
    public void testGetTagPaths() throws Exception {
        Object o = new Object(this.fdb, "", this.getId());
        // getTagPaths will call getItem() if tagPaths is null (as it will be in a newly instantiated
        // object)
        assertEquals(true, o.getTagPaths().length>1);
    }
    
    @Test
    public void testHasTag() throws Exception {
        Object o = new Object(this.fdb, "", this.getId());
        String badTagName = UUID.randomUUID().toString();
        // pass as a path
        assertEquals(true, o.hasTag("/fluiddb/about"));
        assertEquals(false, o.hasTag("/fluiddb/"+badTagName));
        // pass as a tag
        Tag goodTag = new Tag(this.fdb,"", "/fluiddb/about");
        Tag badTag = new Tag(this.fdb,"", badTagName);
        assertEquals(true, o.hasTag(goodTag));
        assertEquals(false, o.hasTag(badTag));
    }
    
    @Test
    public void testDeleteTag() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a null value
        o.tag(t);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // delete the tag
        o.deleteTag(t);
        // now check it DOESN'T have the tag associated with it
        assertEquals(false, o.hasTag(t));
        // lets clean up and remove the tag from our namespace
        t.delete();
    }
    
    @Test
    public void testTagNull() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a null value
        o.tag(t);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        assertEquals("null", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
    
    @Test
    public void testTagBoolean() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a boolean value
        o.tag(t, true);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        assertEquals("true", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // and make sure we can update
        o.tag(t, false);
        // lets make sure that the tag has the right value
        fr = o.getTagValue(t);
        assertEquals("false", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
    
    @Test
    public void testTagInteger() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with an integer value
        int val = 1;
        o.tag(t, val);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        assertEquals("1", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // and make sure we can update
        val=-1;
        o.tag(t, val);
        // lets make sure that the tag has the right value
        fr = o.getTagValue(t);
        assertEquals("-1", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
    
    @Test
    public void testTagFloatingPoint() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a floating point (double) value
        double val = 1.234;
        o.tag(t, val);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        assertEquals("1.234", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // and make sure we can update
        val=-1.234;
        o.tag(t, val);
        // lets make sure that the tag has the right value
        fr = o.getTagValue(t);
        assertEquals("-1.234", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
    
    @Test
    public void testTagString() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a string value
        o.tag(t, "foo");
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        assertEquals("\"foo\"", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // and make sure we can update
        o.tag(t, "bar");
        // lets make sure that the tag has the right value
        fr = o.getTagValue(t);
        assertEquals("\"bar\"", fr.getResponseContent());
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
    
    @Test
    public void testTagStringSet() throws Exception {
        Object o = new Object(this.fdb, this.getId(), this.getId());
        User u = new User(this.fdb, "", this.fdb.getUsername());
        Namespace n = u.RootNamespace();
        String tagName = UUID.randomUUID().toString();
        Tag t = n.createTag(tagName, "A test tag", true);
        // Create the tag with a string array value
        String[] val = {"foo", "bar", "baz"};
        o.tag(t, val);
        // check it has the tag now associated with it
        assertEquals(true, o.hasTag(t));
        // lets make sure that the tag has the right value
        FluidResponse fr = o.getTagValue(t);
        String content = fr.getResponseContent();
        assertEquals(true, content.contains("foo"));
        assertEquals(true, content.contains("bar"));
        assertEquals(true, content.contains("baz"));
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // and make sure we can update
        String[] val2 = {"ham", "and", "eggs"};
        o.tag(t, val2);
        // lets make sure that the tag has the right value
        fr = o.getTagValue(t);
        content = fr.getResponseContent();
        assertEquals(true, content.contains("ham"));
        assertEquals(true, content.contains("and"));
        assertEquals(true, content.contains("eggs"));
        assertEquals("application/vnd.fluiddb.value+json", fr.getResponseContentType());
        // Housekeeping
        o.deleteTag(t);
        t.delete();
    }
}
