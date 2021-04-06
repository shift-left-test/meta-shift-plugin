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

import com.lge.plugins.metashift.metrics.Visitable;
import com.lge.plugins.metashift.models.xml.SimpleXmlParser;
import com.lge.plugins.metashift.models.xml.Tag;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

/**
 * Represents a set of TestData objects.
 *
 * @author Sung Gon Kim
 */
public final class TestList extends DataList<TestData> {

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   */
  public static TestList create(final File path) throws IllegalArgumentException {
    TestList list = new TestList();
    String recipe = path.getName();
    String[] extensions = {"xml"};
    File directory = new File(path, "test");
    if (!directory.exists()) {
      return list;
    }
    Collection<File> files = FileUtils.listFiles(directory, extensions, true);
    if (files.isEmpty()) {
      return list;
    }
    for (File file : files) {
      try {
        list.addAll(parseFile(recipe, file));
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ParserConfigurationException | SAXException | IndexOutOfBoundsException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Failed to parse: " + file, e);
      }
    }
    Collections.sort(list);
    return list;
  }

  /**
   * parse the report file to create list of test data.
   *
   * @param recipe name
   * @param file   report file
   * @return a list of test data objects
   * @throws ParserConfigurationException if failed to parse the xml files
   * @throws IOException                  if failed to parse the xml files
   * @throws SAXException                 if failed to parse the xml files
   */
  private static Collection<? extends TestData> parseFile(final String recipe, final File file)
      throws ParserConfigurationException, IOException, SAXException {
    TestList list = new TestList();

    SimpleXmlParser parser = new SimpleXmlParser(file);
    for (Tag testsuite : parser.findByName("testsuite")) {
      String suite = testsuite.getAttribute("name");
      for (Tag testcase : testsuite.findByName("testcase")) {
        list.add(createInstance(recipe, suite, testcase));
      }
    }
    return list;
  }

  /**
   * Create a test data instance using the given tag.
   *
   * @param recipe   name
   * @param suite    name
   * @param testcase object
   * @return a test data object
   */
  private static TestData createInstance(final String recipe, final String suite,
      final Tag testcase) {
    String name = testcase.getAttribute("name");
    for (Tag tag : testcase.getChildNodes()) {
      String message = tag.getAttribute("message");
      String status = tag.getTagName();
      switch (status.toLowerCase()) {
        case "failure":
          return new FailedTestData(recipe, suite, name, message);
        case "error":
          return new ErrorTestData(recipe, suite, name, message);
        case "skipped":
          return new SkippedTestData(recipe, suite, name, message);
        default:
          throw new IllegalArgumentException(
              String.format("Failed to parse: %s.%s.%s", recipe, suite, name));
      }
    }
    return new PassedTestData(recipe, suite, name, "");
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
