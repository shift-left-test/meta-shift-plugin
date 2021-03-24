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

package com.lge.plugins.metashift.models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of CacheData objects.
 *
 * @author Sung Gon Kim
 */
public final class CacheSet extends DataSet<CacheData> {
  @Override
  public void accept(final Visitor visitor) {
    visitor.visit(this);
  }

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param recipe name
   * @param path to the report directory
   * @return a set of objects
   */
  public static CacheSet create(final String recipe, final File path) throws
      IOException, InterruptedException {
    File report = FileUtils.getFile(path, "caches.json");
    CacheSet set = new CacheSet();
    try {
      InputStream is = new BufferedInputStream(new FileInputStream(report));
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, "UTF-8"));
      for (Object o : json.getJSONObject("Premirror").getJSONArray("Found")) {
        set.add(new PremirrorCacheData(recipe, (String) o, true));
      }
      for (Object o : json.getJSONObject("Premirror").getJSONArray("Missed")) {
        set.add(new PremirrorCacheData(recipe, (String) o, false));
      }
      for (Object o : json.getJSONObject("Shared State").getJSONArray("Found")) {
        set.add(new SharedStateCacheData(recipe, (String) o, true));
      }
      for (Object o : json.getJSONObject("Shared State").getJSONArray("Missed")) {
        set.add(new SharedStateCacheData(recipe, (String) o, false));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report);
    }
    return set;
  }
}
