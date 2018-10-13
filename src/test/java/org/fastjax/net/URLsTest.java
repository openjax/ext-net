/* Copyright (c) 2008 FastJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.fastjax.net;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class URLsTest {
  @Test
  public void testIsLocal() throws Exception {
    assertTrue(URLs.isLocal(new URL("jar:file:///C:/proj/parser/jar/parser.jar!/test.xml")));
    assertTrue(URLs.isLocal(new URL("file:///c:/path/to/the%20file.txt")));
    assertTrue(URLs.isLocal(new URL("file:///tmp.txt")));
    assertTrue(URLs.isLocal(new URL("jar:file:/root/app.jar!/repository")));
    assertFalse(URLs.isLocal(new URL("http://127.0.0.1:8080/a.properties")));
    assertFalse(URLs.isLocal(new URL("file://localhost/etc/fstab")));
    assertFalse(URLs.isLocal(new URL("file://localhost/c|/WINDOWS/clock.avi")));
    assertFalse(URLs.isLocal(new URL("file://hostname/path/to/the%20file.txt")));
    assertFalse(URLs.isLocal(new URL("ftp://user:password@server:80/path")));
    assertFalse(URLs.isLocal(new URL("https://mail.google.com/mail/u/0/?zx=gc46uk9snw66#inbox")));
    assertFalse(URLs.isLocal(new URL("jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class")));
  }

  @Test
  public void testIsAbsolute() throws Exception {
    assertTrue(URLs.isAbsolute("c:\\Windows"));
    assertTrue(URLs.isAbsolute("file:///c:/autoexec.bat"));
    assertTrue(URLs.isAbsolute("/usr/share"));
    assertTrue(URLs.isAbsolute("file:///etc/resolv.conf"));
    assertTrue(URLs.isAbsolute("http://www.google.com/"));

    assertFalse(URLs.isAbsolute(".bashrc"));
    assertFalse(URLs.isAbsolute("Thumbs.db"));

    try {
      URLs.isAbsolute(null);
      fail("Expected a NullPointerException");
    }
    catch (final NullPointerException e) {
    }
  }

  @Test
  public void testMakeCanonicalUrlFromPath() throws Exception {
    final Map<URL,String> absolute = new LinkedHashMap<>();
    final Map<URL,String[]> relative = new LinkedHashMap<>();
    if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
      absolute.put(new URL("file", "", "/c:/Windows"), "c:\\Windows");
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"c:\\Windows", "system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"c:\\Windows", "\\system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"c:\\Windows\\", "system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"c:\\Windows\\", "\\system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"\\c:\\Windows", "system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"\\c:\\Windows", "\\system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"\\c:\\Windows\\", "system32"});
      relative.put(new URL("file", "", "/c:/Windows/system32"), new String[] {"\\c:\\Windows\\", "\\system32"});
    }
    else {
      absolute.put(new URL("file", "", "/etc/resolv.conf"), "/etc/resolv.conf");
      absolute.put(new URL("file", "", "/initrd.img"), "initrd.img");
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"", "etc/resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"", "/etc/resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"etc/resolv.conf", ""});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"/etc/resolv.conf", ""});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"etc", "resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"etc", "/resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"etc/", "resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"etc/", "/resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"/etc", "resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"/etc", "/resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"/etc/", "resolv.conf"});
      relative.put(new URL("file", "", "/etc/resolv.conf"), new String[] {"/etc/", "/resolv.conf"});
    }

    absolute.put(new URL("http://www.google.com/webhp"), "http://www.google.com/webhp");
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"", "http://www.google.com/webhp"});
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"http://www.google.com/webhp", ""});
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"http://www.google.com/", "webhp"});
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"http://www.google.com", "webhp"});
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"http://www.google.com", "/webhp"});
    relative.put(new URL("http://www.google.com/webhp"), new String[] {"http://www.google.com/", "/webhp"});
    for (final Map.Entry<URL,String> entry : absolute.entrySet())
      assertEquals(entry.getKey(), URLs.makeCanonicalUrlFromPath(entry.getValue()));

    for (final Map.Entry<URL,String[]> entry : relative.entrySet())
      assertEquals(entry.getKey(), URLs.makeCanonicalUrlFromPath(entry.getValue()[0], entry.getValue()[1]));
  }

  @Test
  public void testToExternalForm() throws Exception {
    assertEquals(URLs.toExternalForm(new URL("http://www.google.com/webhp")), "http://www.google.com/webhp");
    try {
      URLs.toExternalForm(new URL("fbiy384ehd"));
      fail("Expected a MalformedURLException");
    }
    catch (final MalformedURLException e) {
    }
  }

  @Test
  public void testExists() throws Exception {
    if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
      assertTrue(URLs.exists(new URL("file", "", "/c:/")));
    else
      assertTrue(URLs.exists(new URL("file", "", "/usr")));

    // FIXME: Some machines may not be connected to the web!
//      assertTrue(URLs.exists(new URL("http://www.google.com/")));

    assertFalse(URLs.exists(new URL("file", "", "/ngfodbbgfid")));
    assertFalse(URLs.exists(new URL("http://fndos.grnoe.dfsn/")));
  }

  @Test
  public void testCanonicalizeURL() throws Exception {
    final Map<URL,URL> map = new HashMap<>();
    map.put(new URL("file:///usr/share"), new URL("file:///usr/share/../share"));
    map.put(new URL("file:///usr/lib"), new URL("file:///usr/share/../share/../lib"));
    map.put(new URL("file:///var"), new URL("file:///usr/share/../share/../lib/../../var"));

    for (final Map.Entry<URL,URL> entry : map.entrySet())
      assertEquals(entry.getKey(), URLs.canonicalizeURL(entry.getValue()));

    assertNull(URLs.canonicalizeURL(null));
  }

  @Test
  public void testGetName() throws Exception {
    assertNull(URLs.canonicalizeURL(null));
    assertEquals("share.txt", URLs.getName(new URL("file:///usr/share/../share.txt")));
    assertEquals("lib", URLs.getName(new URL("file:///usr/share/../share/../lib")));
    assertEquals("var", URLs.getName(new URL("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv.conf", URLs.getName(new URL("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetShortName() throws Exception {
    assertNull(URLs.canonicalizeURL(null));
    assertEquals("share", URLs.getShortName(new URL("file:///usr/share/../share")));
    assertEquals("lib", URLs.getShortName(new URL("file:///usr/share/../share/../lib")));
    assertEquals("var", URLs.getShortName(new URL("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv", URLs.getShortName(new URL("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetParent() throws Exception {
    assertNull(URLs.getCanonicalParent(null));
    assertEquals(new URL("file:///usr/share/.."), URLs.getParent(new URL("file:///usr/share/../share")));
    assertEquals(new URL("file:///usr/local/bin/../lib/.."), URLs.getParent(new URL("file:///usr/local/bin/../lib/../bin")));
  }

  @Test
  public void testGetCanonicalParent() throws Exception {
    assertNull(URLs.getCanonicalParent(null));
    assertEquals(new URL("file:///usr"), URLs.getCanonicalParent(new URL("file:///usr/share/../share")));
    assertEquals(new URL("file:///usr/local"), URLs.getCanonicalParent(new URL("file:///usr/local/bin/../lib/../bin")));
  }

  @Test
  public void testGetLastModified() throws Exception {
    assertTrue(URLs.getLastModified(Thread.currentThread().getContextClassLoader().getResource(Test.class.getName().replace('.', '/') + ".class")) > 0);
    assertTrue(URLs.getLastModified(Thread.currentThread().getContextClassLoader().getResource(URLsTest.class.getName().replace('.', '/') + ".class")) > 0);
    assertTrue(URLs.getLastModified(new File("").toURI().toURL()) > 0);
    assertTrue(URLs.getLastModified(new URL("http://www.dot.ca.gov/hq/roadinfo/Hourly")) > -1);
  }

  @Test
  public void testUrlDecode() {
    assertEquals("+ ", URLs.decode("%2B+"));
  }

  @Test
  public void testUrlEncode() {
    assertEquals("%2B+", URLs.urlEncode("+ "));
  }

  @Test
  public void testPathEncode() {
    // rfc3986.txt 3.3
    // segment-nz = 1*pchar
    // pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
    // sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
    // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"

    // '&' has to be represented as &amp; in WADL

    final String pathChars = ":@!$&'()*+,;=-._~";
    final String str = URLs.pathEncode(pathChars);
    assertEquals(str, pathChars);
  }

  @Test
  public void testPathEncodeWithPlusAndSpace() {
    assertEquals("+%20", URLs.pathEncode("+ "));
  }

  @Test
  public void testURLEncode() {
    assertEquals("%2B+", URLs.urlEncode("+ "));
  }

  @Test
  public void testUrlDecodeReserved() {
    assertEquals("!$&'()*,;=", URLs.decode("!$&'()*,;="));
  }

  @Test
  public void testPathDecode() {
    assertEquals("+++", URLs.pathDecode("+%2B+"));
  }
}