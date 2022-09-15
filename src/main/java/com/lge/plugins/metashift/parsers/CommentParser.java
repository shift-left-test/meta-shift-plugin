/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A parsers class for the CommentData objects.
 *
 * @author Sung Gon Kim
 */
public class CommentParser extends Parser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor..
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public CommentParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      Any json = JsonUtils.createObject(report);
      if (!json.keys().contains("size")) {
        return;
      }
      List<Any> array = json.get("size").asList();
      List<Any> filtered = array.stream()
          .filter(o -> !isHidden(o.toString("file")))
          .filter(o -> o.keys().contains("total_lines") && o.keys().contains("comment_lines"))
          .collect(Collectors.toList());
      if (!filtered.isEmpty()) {
        List<CommentData> objects = new ArrayList<>(array.size());
        for (Any o : filtered) {
          objects.add(new CommentData(
              path.getName(),
              o.toString("file"),
              o.toLong("total_lines"),
              o.toLong("comment_lines")));
        }
        dataList.addAll(objects);
        dataList.add(CommentData.class);
      }
//      for (Any o : array) {
//        String file = o.toString("file");
//        if (isHidden(file)) {
//          continue;
//        }
//        objects.add(new CommentData(
//            path.getName(),
//            file,
//            o.toLong("total_lines"),
//            o.toLong("comment_lines")));
//      }
//      dataList.addAll(objects);
//      dataList.add(CommentData.class);
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
