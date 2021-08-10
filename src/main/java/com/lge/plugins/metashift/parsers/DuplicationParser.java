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
      Any json = JsonUtils.createObject2(report);
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
