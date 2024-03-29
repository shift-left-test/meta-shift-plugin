/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for the CodeViolationData objects.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public CodeViolationParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      Any json = JsonUtils.createObject(report);
      if (!json.keys().contains("violations")) {
        return;
      }
      List<Any> array = json.get("violations").asList();
      List<CodeViolationData> objects = new ArrayList<>(array.size());
      for (Any o : array) {
        String file = o.toString("file");
        if (isHidden(file)) {
          continue;
        }
        objects.add(createInstance(path.getName(), o));
      }
      dataList.addAll(objects);
      dataList.add(CodeViolationData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Creates an instance using the given data.
   *
   * @param recipe name
   * @param object data to parse
   * @return a CodeViolationData object
   */
  private static CodeViolationData createInstance(final String recipe, final Any object) {
    String file = object.toString("file");
    long line = object.toLong("line");
    long column = object.toLong("column");
    String rule = object.toString("rule");
    String message = object.toString("message");
    String description = object.toString("description");
    String severity = object.toString("severity");
    String level = object.toString("level");
    String tool = object.toString("tool");
    switch (level.toLowerCase()) {
      case "major":
        return new MajorCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      case "minor":
        return new MinorCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      case "info":
        return new InfoCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      default:
        throw new JsonException("Unknown level value: " + level);
    }
  }
}
