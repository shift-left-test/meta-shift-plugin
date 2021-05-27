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

package com.lge.plugins.metashift.models.factory;

import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * A factory class for PremirrorCacheData objects.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheFactory {

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   * @throws IOException              if failed to locate report files
   */
  public static List<PremirrorCacheData> create(final File path)
      throws IllegalArgumentException, IOException {
    List<PremirrorCacheData> list = new ArrayList<>();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "checkcache", "caches.json");
    try {
      JSONObject json = JsonUtils.createObject(report);
      for (Object o : json.getJSONObject("Premirror").getJSONArray("Found")) {
        list.add(new PremirrorCacheData(recipe, (String) o, true));
      }
      for (Object o : json.getJSONObject("Premirror").getJSONArray("Missed")) {
        list.add(new PremirrorCacheData(recipe, (String) o, false));
      }
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    }
    Collections.sort(list);
    return list;
  }

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   * @throws IOException              if failed to locate report files
   * @throws InterruptedException     if an interruption occurred
   */
  public static List<PremirrorCacheData> create(final FilePath path)
      throws IllegalArgumentException, IOException, InterruptedException {
    return create(new File(path.toURI()));
  }
}
