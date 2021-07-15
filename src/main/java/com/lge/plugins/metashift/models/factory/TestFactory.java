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

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.models.xml.SimpleXmlParser;
import com.lge.plugins.metashift.models.xml.Tag;
import hudson.FilePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * A factory class for the TestData objects.
 *
 * @author Sung Gon Kim
 */
public class TestFactory {

  /**
   * Creates a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @throws IOException          if failed to locate report files
   * @throws InterruptedException if an interruption occurs
   */
  public static void create(final FilePath path, final DataList dataList)
      throws IOException, InterruptedException {
    List<TestData> objects = new ArrayList<>();
    String recipe = path.getName();
    try {
      FilePath[] files = path.list("test/**/*.xml");
      if (files.length == 0) {
        return;
      }
      for (FilePath file : files) {
        objects.addAll(parseFile(recipe, file));
      }
      Collections.sort(objects);
      dataList.addAll(objects);
      dataList.add(TestData.class);
    } catch (ParserConfigurationException | SAXException | IOException ignored) {
      // ignored
    }
  }

  /**
   * Parses the report file to create list of test data.
   *
   * @param recipe name
   * @param file   report file
   * @return a list of test data objects
   * @throws ParserConfigurationException if failed to parse the xml files
   * @throws IOException                  if failed to parse the xml files
   * @throws SAXException                 if failed to parse the xml files
   * @throws InterruptedException         if an interruption occurs
   */
  private static Collection<? extends TestData> parseFile(final String recipe, final FilePath file)
      throws ParserConfigurationException, IOException, SAXException, InterruptedException {
    List<TestData> list = new ArrayList<>();
    SimpleXmlParser parser = new SimpleXmlParser(file);
    for (Tag testsuite : parser.getChildNodes("testsuite")) {
      String suite = testsuite.getAttribute("name");
      for (Tag testcase : testsuite.getChildNodes("testcase")) {
        list.add(createInstance(recipe, suite, testcase));
      }
    }
    return list;
  }

  /**
   * Creates a test data instance using the given tag.
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
}
