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
import java.util.Collections;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

/**
 * Represents a set of MutationTestData objects.
 */
public final class MutationTestList extends DataList<MutationTestData> {

  /**
   * Parse the tag to create a data object.
   *
   * @param recipe name
   * @param tag    to parse
   * @return an object
   */
  private static MutationTestData createInstance(final String recipe, final Tag tag) {
    String detected = tag.getAttribute("detected");
    String file = tag.findByName("sourceFilePath").first().getTextContent();
    String mutatedClass = tag.findByName("mutatedClass").first().getTextContent();
    String mutatedMethod = tag.findByName("mutatedMethod").first().getTextContent();
    int line = Integer.parseInt(tag.findByName("lineNumber").first().getTextContent());
    String mutator = tag.findByName("mutator").first().getTextContent();
    String killingTest = tag.findByName("killingTest").first().getTextContent();
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
      SimpleXmlParser parser = new SimpleXmlParser(report);
      for (Tag tag : parser.findByName("mutation")) {
        list.add(createInstance(recipe, tag));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException | SAXException | IndexOutOfBoundsException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    }
    Collections.sort(list);
    return list;
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
