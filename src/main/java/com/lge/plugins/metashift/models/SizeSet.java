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

import hudson.FilePath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of SizeData objects.
 *
 * @author Sung Gon Kim
 */
public final class SizeSet extends DataSet<SizeData> {
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
  public static SizeSet create(final String recipe, final File path)
      throws IOException, InterruptedException {
    File report = new File(path, "sage_report.json");
    SizeSet set = new SizeSet();
    try {
      InputStream is = new FileInputStream(report);
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, "UTF-8"));
      JSONArray objects = json.getJSONArray("size");
      for (int i = 0; i < objects.size(); i++) {
        JSONObject object = objects.getJSONObject(i);
        set.add(new SizeData(recipe,
                             object.getString("file"),
                             object.getInt("total_lines"),
                             object.getInt("functions"),
                             object.getInt("classes")));

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
