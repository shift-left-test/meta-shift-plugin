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
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of ComplexityData objects.
 *
 * @author Sung Gon Kim
 */
public final class ComplexityList extends DataList<ComplexityData> {

  /**
   * Removes all duplicates except the one with the highest complexity value.
   *
   * @param objects to remove duplicates
   * @return unique list
   */
  private static ComplexityList removeDuplicates(ComplexityList objects) {
    ComplexityList collected = new ComplexityList();
    objects.sort(Comparator.comparingInt(ComplexityData::getValue).reversed());
    collected.addAll(objects.stream().distinct().collect(Collectors.toList()));
    return collected;
  }

  /**
   * Create a list of objects using the given data.
   *
   * @param path to the report directory
   * @return a list of objects
   */
  public static ComplexityList create(File path) {
    ComplexityList list = new ComplexityList();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "checkcode", "sage_report.json");
    try {
      InputStream is = new BufferedInputStream(new FileInputStream(report));
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
      for (Object o : json.getJSONArray("complexity")) {
        list.add(new ComplexityData(recipe,
            ((JSONObject) o).getString("file"),
            ((JSONObject) o).getString("function"),
            ((JSONObject) o).getInt("start"),
            ((JSONObject) o).getInt("end"),
            ((JSONObject) o).getInt("value")));
      }
      list = removeDuplicates(list);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report);
    }
    Collections.sort(list);
    return list;
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
