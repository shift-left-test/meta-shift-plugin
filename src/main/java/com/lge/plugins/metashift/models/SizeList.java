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

import com.lge.plugins.metashift.metrics.Visitable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of SizeData objects.
 *
 * @author Sung Gon Kim
 */
public final class SizeList extends DataList<SizeData> {

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param recipe name
   * @param path   to the report directory
   * @return a set of objects
   */
  public static SizeList create(final String recipe, final File path) {
    File report = FileUtils.getFile(path, "checkcode", "sage_report.json");
    SizeList set = new SizeList();
    try {
      InputStream is = new BufferedInputStream(new FileInputStream(report));
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
      for (Object o : json.getJSONArray("size")) {
        set.add(new SizeData(recipe,
            ((JSONObject) o).getString("file"),
            ((JSONObject) o).getInt("total_lines"),
            ((JSONObject) o).getInt("functions"),
            ((JSONObject) o).getInt("classes")));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report);
    }
    return set;
  }
}
