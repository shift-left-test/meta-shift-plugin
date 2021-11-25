/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for SharedStateCacheData objects.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public SharedStateCacheParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcache").child("caches.json");
    try {
      Any json = JsonUtils.createObject(report);
      List<Any> found = json.get("Shared State", "Found").asList();
      List<Any> missed = json.get("Shared State", "Missed").asList();
      List<SharedStateCacheData> objects = new ArrayList<>(found.size() + missed.size());

      for (Any o : found) {
        objects.add(new SharedStateCacheData(path.getName(), o.toString(), true));
      }
      for (Any o : missed) {
        objects.add(new SharedStateCacheData(path.getName(), o.toString(), false));
      }
      dataList.addAll(objects);
      dataList.add(SharedStateCacheData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
