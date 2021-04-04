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

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the TestList class.
 *
 * @author Sung Gon Kim
 */
public class TestListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TestList objects;

  @Before
  public void setUp() {
    objects = new TestList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = FileUtils.getFile(folder.getRoot(), FilenameUtils.getPath(path));
    if (!directory.exists()) {
      FileUtils.forceMkdir(directory);
    }
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(TestData object, String recipe, String suite, String name,
      String message) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(suite, object.getSuite());
    assertEquals(name, object.getName());
    assertEquals(message, object.getMessage());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    TestData first = new PassedTestData("A", "a.suite", "a.tc", "msg");
    TestData second = new PassedTestData("B", "b.suite", "b.tc", "msg");
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = TestList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File file = folder.newFolder("report/A/test");
    objects = TestList.create(file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    List<String> data = Arrays.asList(
        "<testsuite",
        "testsuite/>"
    );
    File file = createTempFile("report/A/test/1.xml", data);
    objects = TestList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    List<String> data = Collections.singletonList("");
    File file = createTempFile("report/B/test/1.xml", data);
    objects = TestList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    List<String> data = Arrays.asList(
        "<testsuites>",
        "</testsuites>"
    );
    File file = createTempFile("report/B/test/1.xml", data);
    objects = TestList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    List<String> data = Arrays.asList(
        "<testsuites>",
        "  <testsuite name=\"A\">",
        "    <testcase name=\"test1\"/>",
        "  </testsuite>",
        "</testsuites>"
    );
    File file = createTempFile("report/C/test/1.xml", data);

    objects = TestList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    TestData object = objects.iterator().next();
    assertValues(object, "C", "A", "test1", "");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    List<String> data = Arrays.asList(
        "<testsuites>",
        "  <testsuite name=\"A\">",
        "    <testcase name=\"test1\"/>",
        "  </testsuite>",
        "  <testsuite name=\"B\">",
        "    <testcase name=\"test2\">",
        "      <failure message=\"failure\"/>",
        "    </testcase>",
        "  </testsuite>",
        "  <testsuite name=\"C\">",
        "    <testcase name=\"test3\">",
        "      <error message=\"error\"/>",
        "    </testcase>",
        "  </testsuite>",
        "  <testsuite name=\"D\">",
        "    <testcase name=\"test4\">",
        "      <skipped message=\"skipped\"/>",
        "    </testcase>",
        "  </testsuite>",
        "</testsuites>"
    );
    File file = createTempFile("report/D/test/1.xml", data);

    objects = TestList.create(file.getParentFile().getParentFile());
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "A", "test1", "");
    assertValues(iterator.next(), "D", "B", "test2", "failure");
    assertValues(iterator.next(), "D", "C", "test3", "error");
    assertValues(iterator.next(), "D", "D", "test4", "skipped");
  }

  @Test
  public void testCreateWithMultipleFiles() throws Exception {
    Map<String, List<String>> contents = new HashMap<>();
    contents.put("report/D/test/1.xml",
        Arrays.asList(
            "<testsuites>",
            "  <testsuite name=\"A\">",
            "    <testcase name=\"test1\"/>",
            "  </testsuite>",
            "</testsuites>"
        )
    );
    contents.put("report/D/test/2.xml",
        Arrays.asList(
            "<testsuites>",
            "  <testsuite name=\"B\">",
            "    <testcase name=\"test2\">",
            "      <failure message=\"failure\"/>",
            "    </testcase>",
            "  </testsuite>",
            "</testsuites>"
        )
    );
    contents.put("report/D/test/3.xml",
        Arrays.asList(
            "<testsuites>",
            "  <testsuite name=\"C\">",
            "    <testcase name=\"test3\">",
            "      <error message=\"error\"/>",
            "    </testcase>",
            "  </testsuite>",
            "</testsuites>"
        )
    );
    contents.put("report/D/test/4.xml",
        Arrays.asList(
            "<testsuites>",
            "  <testsuite name=\"D\">",
            "    <testcase name=\"test4\">",
            "      <skipped message=\"skipped\"/>",
            "    </testcase>",
            "  </testsuite>",
            "</testsuites>"
        )
    );
    for (Map.Entry<String, List<String>> entry : contents.entrySet()) {
      createTempFile(entry.getKey(), entry.getValue());
    }
    objects = TestList.create(FileUtils.getFile(folder.getRoot(), "report", "D"));
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "A", "test1", "");
    assertValues(iterator.next(), "D", "B", "test2", "failure");
    assertValues(iterator.next(), "D", "C", "test3", "error");
    assertValues(iterator.next(), "D", "D", "test4", "skipped");
  }
}
