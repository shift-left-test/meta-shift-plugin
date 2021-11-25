/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for PremirrorCacheData objects.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public PremirrorCacheParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcache").child("caches.json");
    try {
      Any json = JsonUtils.createObject(report);
      List<Any> found = json.get("Premirror", "Found").asList();
      List<Any> missed = json.get("Premirror", "Missed").asList();
      List<PremirrorCacheData> objects = new ArrayList<>(found.size() + missed.size());

      for (Any o : found) {
        objects.add(new PremirrorCacheData(path.getName(), o.toString(), true));
      }
      for (Any o : missed) {
        objects.add(new PremirrorCacheData(path.getName(), o.toString(), false));
      }
      dataList.addAll(objects);
      dataList.add(PremirrorCacheData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
