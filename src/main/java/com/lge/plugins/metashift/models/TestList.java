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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
      } catch (ParserConfigurationException | SAXException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Failed to parse: " + file);
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
    Collection<TestData> list = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(file);
    document.getDocumentElement().normalize();
    NodeList nodes = document.getElementsByTagName("testsuite");
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        String suite = element.getAttribute("name");
        list.addAll(createTestDataList(recipe, suite, element));
      }
    }
    return list;
  }

  /**
   * Create a list of test data.
   *
   * @param recipe  name
   * @param suite   name
   * @param element to parse
   * @return a list of test data
   */
  private static Collection<? extends TestData> createTestDataList(final String recipe,
      final String suite, final Element element) {
    Collection<TestData> list = new ArrayList<>();
    NodeList nodes = element.getElementsByTagName("testcase");
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        list.add(createTestData(recipe, suite, (Element) node));
      }
    }
    return list;
  }

  /**
   * Creates the test data object.
   *
   * @param recipe  name
   * @param suite   name
   * @param element to parse
   * @return a test data object
   */
  private static TestData createTestData(final String recipe, final String suite,
      final Element element) {
    String name = element.getAttribute("name");
    if (!element.hasChildNodes()) {
      return new PassedTestData(recipe, suite, name, "");
    }
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      String message = ((Element) node).getAttribute("message");
      String status = ((Element) node).getTagName();
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
    throw new IllegalArgumentException(
        String.format("Failed to parse: %s.%s.%s", recipe, suite, name));
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
