/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Unit tests for the SimpleXmlParser class.
 *
 * @author Sung Gon Kim
 */
public class SimpleXmlParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private SimpleXmlParser parser;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder, '\'', '"');
    builder = new StringBuilder();
  }

  private void prepare()
      throws IOException, SAXException, ParserConfigurationException, InterruptedException {
    File file = utils.getPath("test.xml");
    utils.writeLines(builder, file);
    parser = new SimpleXmlParser(new FilePath(file));
  }

  @Test
  public void testInitialState() {
    parser = new SimpleXmlParser();
    assertEquals(0, parser.getChildNodes("X").size());
  }

  @Test(expected = NoSuchFileException.class)
  public void testWithUnknownPath() throws Exception {
    new SimpleXmlParser(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = SAXParseException.class)
  public void testParserWithMalformedTag() throws Exception {
    builder.append("</tag>");
    prepare();
    parser.getChildNodes("tag");
  }

  @Test
  public void testParserWithoutMatchingTag() throws Exception {
    builder.append("<tag></tag>");
    prepare();
    TagList tags = parser.getChildNodes("X");
    assertEquals(0, tags.size());
    assertEquals("", tags.first().getTagName());
    assertEquals("", tags.last().getTagName());
  }

  @Test
  public void testTagList() throws Exception {
    builder
        .append("<tags>")
        .append("  <tag number='1' string='A' empty=''>")
        .append("    <text>first</text>")
        .append("  </tag>")
        .append("  <tag number='2' string='B' empty=''>")
        .append("    <text>second</text>")
        .append("  </tag>")
        .append("  <extra/>")
        .append("</tags>");
    prepare();

    TagList tags = parser.getChildNodes("tag");
    assertEquals(2, tags.size());

    Tag first = tags.first();
    assertEquals("tag", first.getTagName());
    assertTrue(first.hasAttribute("number"));
    assertFalse(first.hasAttribute("X"));
    assertEquals("1", first.getAttribute("number"));
    assertEquals("A", first.getAttribute("string"));
    assertEquals("", first.getAttribute("empty"));
    assertEquals("0", first.getAttribute("empty", "0"));
    assertEquals("1", first.getAttribute("unknown", "1"));
    assertTrue(first.hasChildNodes());
    assertEquals("first", first.getChildNodes().first().getTextContent());

    Tag second = tags.last();
    assertEquals("tag", second.getTagName());
    assertTrue(second.hasAttribute("number"));
    assertFalse(second.hasAttribute("X"));
    assertEquals("2", second.getAttribute("number"));
    assertEquals("B", second.getAttribute("string"));
    assertEquals("", second.getAttribute("empty"));
    assertEquals("0", second.getAttribute("empty", "0"));
    assertEquals("1", second.getAttribute("unknown", "1"));
    assertTrue(second.hasChildNodes());
    assertEquals("second", second.getChildNodes().first().getTextContent());

    tags = parser.getChildNodes("extra");
    assertEquals(1, tags.size());
    assertFalse(tags.first().hasChildNodes());
  }

  @Test
  public void testParseXmlWithDtdManyTimes()
      throws IOException, ParserConfigurationException, SAXException, InterruptedException {
    builder
        .append("<?xml version=\"1.0\"?>")
        .append("<!DOCTYPE coverage ")
        .append("SYSTEM \"http://cobertura.sourceforge.net/xml/coverage-04.dtd\">")
        .append("<coverage></coverage>");
    prepare();
    for (int i = 0; i < 1000; i++) {
      parser = new SimpleXmlParser(new FilePath(utils.getPath("test.xml")));
    }
  }
}
