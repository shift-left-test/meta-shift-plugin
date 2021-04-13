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

package com.lge.plugins.metashift.models.factory;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.TemporaryFileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the CoverageFactory class.
 *
 * @author Sung Gon Kim
 */
public class CoverageFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<CoverageData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(CoverageData object, String recipe, String file, String function,
      long line, long index, boolean covered) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(function, object.getFunction());
    assertEquals(line, object.getLine());
    assertEquals(index, object.getIndex());
    assertEquals(covered, object.isCovered());
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = CoverageFactory.create(utils.getPath("path-to-unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "coverage").getParentFile();
    objects = CoverageFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("<class filename='a.cpp'>")
        .append("  <methods>")
        .append("    <method name='func1()'>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    CoverageFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    CoverageFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<classes> </classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithInsufficientMethodData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("<class filename='a.cpp'>")
        .append("  <methods/>")
        .append("  <lines>")
        .append("    <line hits='1' number='1'/>")
        .append("  </lines>")
        .append("</class>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithEmptyLineData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines/>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='2'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithInsufficientLineData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='2'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<CoverageData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "a.cpp", "func1()", 2, 0, true);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='1'/>")
        .append("      <line branch='false' hits='0' number='10'/>")
        .append("      <line branch='true' hits='1' number='30' condition-coverage='50% (1/2)'>")
        .append("        <conds>")
        .append("          <cond block_number='0' branch_number='0' hit='1'/>")
        .append("          <cond block_number='0' branch_number='1' hit='0'/>")
        .append("        </conds>")
        .append("      </line>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(4, objects.size());

    Iterator<CoverageData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "a.cpp", "func1()", 1, 0, true);
    assertValues(iterator.next(), "D-1.0.0-r0", "a.cpp", "func1()", 10, 0, false);
    assertValues(iterator.next(), "D-1.0.0-r0", "a.cpp", "func2()", 30, 0, true);
    assertValues(iterator.next(), "D-1.0.0-r0", "a.cpp", "func2()", 30, 1, false);
  }

  @Test
  public void testCreateWithMultipleFiles() throws Exception {
    File directory = utils.createDirectory("report", "E-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='1'/>")
        .append("      <line branch='false' hits='0' number='10'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("  <class filename='b.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='true' hits='1' number='30' condition-coverage='50% (1/2)'>")
        .append("        <conds>")
        .append("          <cond block_number='0' branch_number='0' hit='1'/>")
        .append("          <cond block_number='0' branch_number='1' hit='0'/>")
        .append("        </conds>")
        .append("      </line>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    objects = CoverageFactory.create(directory);
    assertEquals(4, objects.size());

    Iterator<CoverageData> iterator = objects.iterator();
    assertValues(iterator.next(), "E-1.0.0-r0", "a.cpp", "func1()", 1, 0, true);
    assertValues(iterator.next(), "E-1.0.0-r0", "a.cpp", "func1()", 10, 0, false);
    assertValues(iterator.next(), "E-1.0.0-r0", "b.cpp", "func2()", 30, 0, true);
    assertValues(iterator.next(), "E-1.0.0-r0", "b.cpp", "func2()", 30, 1, false);
  }
}
