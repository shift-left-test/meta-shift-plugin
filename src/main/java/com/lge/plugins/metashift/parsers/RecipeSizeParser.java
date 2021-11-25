/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for RecipeSizeData objects.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public RecipeSizeParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkrecipe").child("files.json");
    try {
      Any json = JsonUtils.createObject(report);
      List<Any> array = json.get("lines_of_code").asList();
      List<RecipeSizeData> objects = new ArrayList<>(array.size());

      for (Any o : array) {
        String file = Paths.get(o.toString("file")).normalize().toString();
        if (isHidden(file)) {
          continue;
        }
        objects.add(new RecipeSizeData(
            path.getName(),
            file,
            o.toLong("code_lines")
        ));
      }
      dataList.addAll(objects);
      dataList.add(RecipeSizeData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
