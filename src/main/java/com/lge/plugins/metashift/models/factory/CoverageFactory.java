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
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.xml.SimpleXmlParser;
import com.lge.plugins.metashift.models.xml.Tag;
import com.lge.plugins.metashift.models.xml.TagList;
import com.lge.plugins.metashift.utils.PathUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * A factory class for the CoverageData objects.
 *
 * @author Sung Gon Kim
 */
public class CoverageFactory {

  /**
   * Creates a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @throws IOException          if failed to locate report files
   * @throws InterruptedException if an interruption occurs
   */
  public static void create(final FilePath path, final DataList dataList)
      throws IOException, InterruptedException {
    FilePath report = path.child("coverage").child("coverage.xml");
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      List<CoverageData> objects = new ArrayList<>();

      for (Tag tag : parser.getChildNodes("class")) {
        String filename = tag.getAttribute("filename");
        if (PathUtils.isHidden(filename)) {
          continue;
        }
        for (Tag line : tag.getChildNodes("lines").last().getChildNodes("line")) {
          objects.addAll(createInstances(path.getName(), filename, line));
        }
      }
      Collections.sort(objects);
      dataList.addAll(objects);
      dataList.add(CoverageData.class);
    } catch (ParserConfigurationException | SAXException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Creates a list of coverage objects based on the given tags.
   *
   * @param recipe   name
   * @param filename name
   * @param line     number
   * @return a list of coverage objects
   */
  private static List<CoverageData> createInstances(String recipe, String filename, Tag line) {
    List<CoverageData> list = new ArrayList<>();
    try {
      long lineNumber = Long.parseLong(line.getAttribute("number", "0"));
      boolean covered = Long.parseLong(line.getAttribute("hits", "0")) > 0;
      TagList conditions = line.getChildNodes("cond");
      if (conditions.isEmpty()) {
        list.add(new StatementCoverageData(recipe, filename, lineNumber, covered));
      } else {
        for (Tag condition : conditions) {
          long index = Long.parseLong(condition.getAttribute("branch_number", "0"));
          covered = Long.parseLong(condition.getAttribute("hit", "0")) > 0;
          list.add(new BranchCoverageData(recipe, filename, lineNumber, index, covered));
        }
      }
    } catch (NullPointerException | NumberFormatException ignored) {
      // ignored
    }
    return list;
  }
}
