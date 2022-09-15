/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for the ComplexityData objects.
 *
 * @author Sung Gon Kim
 */
public class ComplexityParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public ComplexityParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      Any json = JsonUtils.createObject(report);
      if (!json.keys().contains("complexity")) {
        return;
      }
      List<Any> array = json.get("complexity").asList();
      List<ComplexityData> objects = new ArrayList<>(array.size());

      for (Any o : array) {
        String file = o.toString("file");
        if (isHidden(file)) {
          continue;
        }
        objects.add(new ComplexityData(
            path.getName(),
            file,
            o.toString("function"),
            o.toLong("start"),
            o.toLong("end"),
            o.toLong("value")));
      }
      dataList.addAll(objects);
      dataList.add(ComplexityData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
