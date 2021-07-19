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

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import com.lge.plugins.metashift.utils.PathUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * A factory class for the DuplicationData objects.
 *
 * @author Sung Gon Kim
 */
public class DuplicationFactory {

  /**
   * Creates a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @throws IOException          if failed to locate report files
   * @throws InterruptedException if an interruption occurs
   */
  public static void create(final FilePath path, final DataList dataList)
      throws IOException, InterruptedException {
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      JSONObject json = JsonUtils.createObject(report);
      JSONArray array = json.getJSONArray("size");
      List<DuplicationData> objects = new ArrayList<>(array.size());

      for (Object o : array) {
        String file = ((JSONObject) o).getString("file");
        if (PathUtils.isHidden(file)) {
          continue;
        }
        objects.add(new DuplicationData(
            path.getName(),
            file,
            ((JSONObject) o).getLong("total_lines"),
            ((JSONObject) o).getLong("duplicated_lines")));
      }
      Collections.sort(objects);
      dataList.addAll(objects);
      dataList.add(DuplicationData.class);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }
}
