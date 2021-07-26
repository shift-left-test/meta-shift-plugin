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
import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * PremirrorCacheDataParser class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheDataParser extends DataParser {

  private final FilePath path;
  private final List<Data> dataList;

  /**
   * Default constructor.
   *
   * @param path     to parse
   * @param dataList to store
   */
  public PremirrorCacheDataParser(FilePath path, List<Data> dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkcache").child("caches.json");
    String recipe = path.getName();
    try {
      Any json = JsonUtils.createObject2(report);
      List<Any> found = json.get("Premirror", "Found").asList();
      List<Any> missed = json.get("Premirror", "Missed").asList();
      List<PremirrorCacheData> objects = new ArrayList<>(found.size() + missed.size());

      for (Any o : found) {
        objects.add(new PremirrorCacheData(recipe, o.toString(), true));
      }
      for (Any o : missed) {
        objects.add(new PremirrorCacheData(recipe, o.toString(), false));
      }
      dataList.addAll(objects);
      dataList.add(new PremirrorCacheDataParsed(recipe));
    } catch (JsonException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
