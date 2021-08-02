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

package com.lge.plugins.metashift.parsers;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.utils.xml.SimpleXmlParser;
import com.lge.plugins.metashift.utils.xml.Tag;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * A parsers class for the MutationTestData objects.
 *
 * @author Sung Gon Kim
 */
public class MutationTestParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public MutationTestParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checktest").child("mutations.xml");
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      List<MutationTestData> objects = new ArrayList<>();

      for (Tag tag : parser.getChildNodes("mutation")) {
        String file = tag.getChildNodes("sourceFilePath").first().getTextContent();
        if (isHidden(file)) {
          continue;
        }
        objects.add(createInstance(path.getName(), tag));
      }
      dataList.addAll(objects);
      dataList.add(MutationTestData.class);
    } catch (ParserConfigurationException | SAXException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Parse the tag to create a data object.
   *
   * @param recipe name
   * @param tag    to parse
   * @return an object
   * @throws SAXException if failed to parse the file
   */
  private static MutationTestData createInstance(final String recipe, final Tag tag)
      throws SAXException {
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
      case "skip":
        return new SkippedMutationTestData(recipe, file, mutatedClass, mutatedMethod, line, mutator,
            killingTest);
      default:
        throw new SAXException("Unknown detected tag: " + detected);
    }
  }
}
