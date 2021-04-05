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
 * Represents a set of MutationTestData objects.
 */
public final class MutationTestList extends DataList<MutationTestData> {

  /**
   * Return the text content of the tag name of the element.
   *
   * @param element object
   * @param tagName to retrieve
   * @return text content
   */
  private static String getTextContent(final Element element, final String tagName)
      throws IllegalArgumentException {
    Node node = element.getElementsByTagName(tagName).item(0);
    if (node == null) {
      throw new IllegalArgumentException("Failed to find the tag: " + tagName);
    }
    return node.getTextContent();
  }

  /**
   * Parse the element to create data object.
   *
   * @param recipe  name
   * @param element to parse
   * @return an object
   */
  private static MutationTestData parseElement(final String recipe, final Element element) {
    String detected = element.getAttribute("detected");
    String file = getTextContent(element, "sourceFilePath");
    String mutatedClass = getTextContent(element, "mutatedClass");
    String mutatedMethod = getTextContent(element, "mutatedMethod");
    int line = Integer.parseInt(getTextContent(element, "lineNumber"));
    String mutator = getTextContent(element, "mutator");
    String killingTest = getTextContent(element, "killingTest");
    switch (detected.toLowerCase()) {
      case "true":
        return new KilledMutationTestData(recipe, file, mutatedClass, mutatedMethod, line, mutator,
            killingTest);
      case "false":
        return new SurvivedMutationTestData(recipe, file, mutatedClass, mutatedMethod, line,
            mutator, killingTest);
      default:
        return new SkippedMutationTestData(recipe, file, mutatedClass, mutatedMethod, line, mutator,
            killingTest);
    }
  }

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   */
  public static MutationTestList create(final File path) throws IllegalArgumentException {
    MutationTestList list = new MutationTestList();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "checktest", "mutations.xml");
    if (!report.exists()) {
      return list;
    }
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(report);
      document.getDocumentElement().normalize();
      NodeList nodes = document.getElementsByTagName("mutation");
      for (int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          list.add(parseElement(recipe, (Element) node));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException | SAXException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report);
    }
    Collections.sort(list);
    return list;
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
