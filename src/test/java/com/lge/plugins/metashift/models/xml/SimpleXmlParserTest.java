/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.models.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.TemporaryFileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder, '\'', '"');
    builder = new StringBuilder();
  }

  @Test(expected = FileNotFoundException.class)
  public void testWithUnknownPath() throws Exception {
    new SimpleXmlParser(utils.getPath("unknown"));
  }

  @Test
  public void testParsingXML() throws Exception {
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>a.file</sourceFile>")
        .append("    <sourceFilePath>path/to/a.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("  <mutation detected='false'>")
        .append("    <sourceFile>b.file</sourceFile>")
        .append("    <sourceFilePath>path/to/b.file</sourceFilePath>")
        .append("    <mutatedClass>B</mutatedClass>")
        .append("    <mutatedMethod>func2</mutatedMethod>")
        .append("    <lineNumber>2</lineNumber>")
        .append("    <mutator>BOR</mutator>")
        .append("    <killingTest>test2</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    File file = utils.getPath("mutations.xml");
    utils.writeLines(builder, file);
    SimpleXmlParser parser = new SimpleXmlParser(file);

    TagList tags;
    Tag tag;

    tags = parser.findByName("mutation");
    assertEquals(2, tags.size());

    tag = tags.first();
    assertEquals("mutation", tag.getTagName());
    assertTrue(tag.hasAttribute("detected"));
    assertEquals("true", tag.getAttribute("detected"));
    assertTrue(tag.hasChildNodes());

    tags = tag.getChildNodes();
    assertEquals(7, tags.size());
    assertEquals("sourceFile", tags.first().getTagName());
    assertEquals("a.file", tags.first().getTextContent());

    tags = tag.findByName("mutator");
    assertEquals(1, tags.size());
    assertEquals("AOR", tags.first().getTextContent());
  }
}
