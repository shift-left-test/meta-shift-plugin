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
import com.lge.plugins.metashift.models.xml.TagList;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

/**
 * Represents a group of CoverageData objects.
 *
 * @author Sung Gon Kim
 */
public final class CoverageList extends DataList<CoverageData> {

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   */
  public static CoverageList create(final File path) {
    CoverageList list = new CoverageList();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "coverage", "coverage.xml");
    if (!report.exists()) {
      return list;
    }
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      for (Tag tag : parser.getChildNodes("class")) {
        String filename = tag.getAttribute("filename");
        TreeMap<Integer, String> methods = findMethods(tag.getChildNodes("method"));
        for (Tag line : tag.getChildNodes("lines").last().getChildNodes("line")) {
          list.addAll(createInstances(methods, recipe, filename, line));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException | SAXException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    }
    Collections.sort(list);
    return list;
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
  private static CoverageList createInstances(TreeMap<Integer, String> methods, String recipe,
      String filename, Tag line) {
    CoverageList list = new CoverageList();
    int lineNumber = Integer.parseInt(line.getAttribute("number"));
    boolean covered = Integer.parseInt(line.getAttribute("hits")) > 0;
    String method = methods.floorEntry(lineNumber).getValue();
    TagList conditions = line.getChildNodes("cond");
    if (conditions.isEmpty()) {
      list.add(new StatementCoverageData(recipe, filename, method, lineNumber, covered));
    } else {
      for (Tag condition : conditions) {
        int index = Integer.parseInt(condition.getAttribute("branch_number"));
        covered = Integer.parseInt(condition.getAttribute("hit")) > 0;
        list.add(new BranchCoverageData(recipe, filename, method, lineNumber, index, covered));
      }
    }
    return list;
  }

  /**
   * Returns the map of line number and method name pairs.
   *
   * @param tags to parse
   * @return map of line number and method names
   */
  private static TreeMap<Integer, String> findMethods(TagList tags) {
    TreeMap<Integer, String> methods = new TreeMap<>();
    for (Tag tag : tags) {
      String method = tag.getAttribute("name");
      TagList lines;
      if ((lines = tag.getChildNodes("line")).size() > 0) {
        int line = Integer.parseInt(lines.first().getAttribute("number"));
        methods.putIfAbsent(line, method);
      }
    }
    return methods;
  }

  @Override
  public void accept(Visitable visitor) {
    visitor.visit(this);
  }
}
