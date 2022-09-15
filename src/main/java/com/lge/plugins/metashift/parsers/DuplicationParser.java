/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A parsers class for the DuplicationData objects.
 *
 * @author Sung Gon Kim
 */
public class DuplicationParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public DuplicationParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  private DuplicationData newObject(String recipe, List<Any> sizes, Any object) {
    String file = object.toString("file");
    Any data = sizes.stream()
        .filter(o -> o.toString("file").equals(file))
        .findFirst().orElseThrow(JsonException::new);
    return new DuplicationData(
        recipe,
        file,
        data.toLong("total_lines"),
        object.toLong("start"),
        object.toLong("end"));
  }

  private void addObjects(Set<DuplicationData> objects, DuplicationData first,
      DuplicationData second) {
    objects.add(first);
    objects.add(second);
    DuplicationData a = objects.stream().filter(o -> o == first).findFirst().orElse(first);
    DuplicationData b = objects.stream().filter(o -> o == second).findFirst().orElse(second);
    a.add(b);
    b.add(a);
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      Any json = JsonUtils.createObject(report);
      if (!json.keys().contains("size") || !json.keys().contains("duplications")) {
        return;
      }
      List<Any> sizes = json.get("size").asList();
      List<Any> duplications = json.get("duplications").asList();
      Set<DuplicationData> objects = new HashSet<>();
      for (Any pair : duplications) {
        if (pair.size() != 2) {
          continue;
        }
        DuplicationData first = newObject(path.getName(), sizes, pair.get(0));
        DuplicationData second = newObject(path.getName(), sizes, pair.get(1));
        if (isHidden(first.getFile()) || isHidden(second.getFile())) {
          continue;
        }
        addObjects(objects, first, second);
      }
      dataList.addAll(objects);
      dataList.add(DuplicationData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
