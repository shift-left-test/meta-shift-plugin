/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.utils.xml.SimpleXmlParser;
import com.lge.plugins.metashift.utils.xml.Tag;
import com.lge.plugins.metashift.utils.xml.TagList;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * A parsers class for the CoverageData objects.
 *
 * @author Sung Gon Kim
 */
public class CoverageParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public CoverageParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("coverage").child("coverage.xml");
    try {
      SimpleXmlParser parser = new SimpleXmlParser(report);
      List<CoverageData> objects = new ArrayList<>();

      for (Tag tag : parser.getChildNodes("class")) {
        String filename = tag.getAttribute("filename");
        if (isHidden(filename)) {
          continue;
        }
        for (Tag line : tag.getChildNodes("lines").last().getChildNodes("line")) {
          objects.addAll(createInstances(path.getName(), filename, line));
        }
      }
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
