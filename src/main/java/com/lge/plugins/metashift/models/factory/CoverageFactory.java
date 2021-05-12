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

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.xml.SimpleXmlParser;
import com.lge.plugins.metashift.models.xml.Tag;
import com.lge.plugins.metashift.models.xml.TagList;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

/**
 * A factory class for the CoverageData objects.
 *
 * @author Sung Gon Kim
 */
public class CoverageFactory {

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   * @throws IOException              if failed to locate report files
   */
  public static List<CoverageData> create(final File path)
      throws IllegalArgumentException, IOException {
    List<CoverageData> list = new ArrayList<>();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "coverage", "coverage.xml");
    if (!report.exists()) {
      throw new IOException("Unable to locate the file:" + report);
    }
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      for (Tag tag : parser.getChildNodes("class")) {
        String filename = tag.getAttribute("filename");
        TreeMap<Long, String> methods = findMethods(tag.getChildNodes("method"));
        for (Tag line : tag.getChildNodes("lines").last().getChildNodes("line")) {
          list.addAll(createInstances(methods, recipe, filename, line));
        }
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
  public static List<CoverageData> create(final FilePath path)
      throws IllegalArgumentException, IOException, InterruptedException {
    return create(new File(path.toURI()));
  }

  /**
   * Creates a list of coverage objects based on the given tags.
   *
   * @param methods  map of line number and method names
   * @param recipe   name
   * @param filename name
   * @param line     number
   * @return a list of coverage objects
   */
  private static List<CoverageData> createInstances(TreeMap<Long, String> methods, String recipe,
      String filename, Tag line) {
    List<CoverageData> list = new ArrayList<>();
    if (methods.isEmpty()) {
      return list;
    }
    try {
      long lineNumber = Long.parseLong(line.getAttribute("number", "0"));
      boolean covered = Long.parseLong(line.getAttribute("hits", "0")) > 0;
      String method = methods.floorEntry(lineNumber).getValue();
      TagList conditions = line.getChildNodes("cond");
      if (conditions.isEmpty()) {
        list.add(new StatementCoverageData(recipe, filename, method, lineNumber, covered));
      } else {
        for (Tag condition : conditions) {
          long index = Long.parseLong(condition.getAttribute("branch_number", "0"));
          covered = Long.parseLong(condition.getAttribute("hit", "0")) > 0;
          list.add(new BranchCoverageData(recipe, filename, method, lineNumber, index, covered));
        }
      }
    } catch (NullPointerException | NumberFormatException ignored) {
      // ignored
    }
    return list;
  }

  /**
   * Returns the map of line number and method name pairs.
   *
   * @param tags to parse
   * @return map of line number and method names
   */
  private static TreeMap<Long, String> findMethods(TagList tags) {
    TreeMap<Long, String> methods = new TreeMap<>();
    for (Tag tag : tags) {
      String method = tag.getAttribute("name");
      TagList lines;
      if ((lines = tag.getChildNodes("line")).size() > 0) {
        long line = Long.parseLong(lines.first().getAttribute("number", "0"));
        methods.putIfAbsent(line, method);
      }
    }
    return methods;
  }
}
