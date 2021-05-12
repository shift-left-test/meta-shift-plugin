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

import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.models.xml.SimpleXmlParser;
import com.lge.plugins.metashift.models.xml.Tag;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

/**
 * A factory class for the MutationTestData objects.
 *
 * @author Sung Gon Kim
 */
public class MutationTestFactory {

  /**
   * Parse the tag to create a data object.
   *
   * @param recipe name
   * @param tag    to parse
   * @return an object
   */
  private static MutationTestData createInstance(final String recipe, final Tag tag) {
    String detected = tag.getAttribute("detected");
    String file = tag.getChildNodes("sourceFilePath").first().getTextContent();
    String mutatedClass = tag.getChildNodes("mutatedClass").first().getTextContent();
    String mutatedMethod = tag.getChildNodes("mutatedMethod").first().getTextContent();
    long line = Long.parseLong(tag.getChildNodes("lineNumber").first().getTextContent());
    String mutator = tag.getChildNodes("mutator").first().getTextContent();
    String killingTest = tag.getChildNodes("killingTest").first().getTextContent();
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
   * @throws IOException              if failed to locate report files
   */
  public static List<MutationTestData> create(final File path)
      throws IllegalArgumentException, IOException {
    List<MutationTestData> list = new ArrayList<>();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "checktest", "mutations.xml");
    if (!report.exists()) {
      throw new IOException("Unable to locate the file: " + report);
    }
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      for (Tag tag : parser.getChildNodes("mutation")) {
        list.add(createInstance(recipe, tag));
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    }
    Collections.sort(list);
    return list;
  }

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   * @throws IOException              if failed to locate report files
   * @throws InterruptedException     if an interruption occurred
   */
  public static List<MutationTestData> create(final FilePath path)
      throws IllegalArgumentException, IOException, InterruptedException {
    return create(new File(path.toURI()));
  }
}
