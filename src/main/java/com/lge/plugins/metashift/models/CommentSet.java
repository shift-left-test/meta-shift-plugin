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
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of CommentData objects.
 *
 * @author Sung Gon Kim
 */
public final class CommentSet extends DataSet<CommentData> {
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
  public static CommentSet create(final String recipe, final File path) throws
      IOException, InterruptedException {
    File report = new File(path, "checkcode" + File.separator + "sage_report.json");
    CommentSet set = new CommentSet();
    try {
      InputStream is = new BufferedInputStream(new FileInputStream(report));
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, "UTF-8"));
      for (Object o : json.getJSONArray("size")) {
        set.add(new CommentData(recipe,
                                ((JSONObject) o).getString("file"),
                                ((JSONObject) o).getInt("total_lines"),
                                ((JSONObject) o).getInt("comment_lines")));
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
