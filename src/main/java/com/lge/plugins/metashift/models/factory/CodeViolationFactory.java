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

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * A factory class for the CodeViolationData objects.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationFactory {

  /**
   * Creates a set of objects by parsing a report file from the given path.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   * @throws IOException          if failed to locate report files
   * @throws InterruptedException if an interruption occurs
   */
  public static void create(final FilePath path, final DataList dataList)
      throws IOException, InterruptedException {
    List<CodeViolationData> objects = new ArrayList<>();
    String recipe = path.getName();
    FilePath report = path.child("checkcode").child("sage_report.json");
    try {
      JSONObject json = JsonUtils.createObject(report);
      for (Object o : json.getJSONArray("violations")) {
        objects.add(createInstance(recipe, (JSONObject) o));
      }
      Collections.sort(objects);
      dataList.addAll(objects);
      dataList.add(CodeViolationData.class);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Creates an instance using the given data.
   *
   * @param recipe name
   * @param object data to parse
   * @return a CodeViolationData object
   */
  private static CodeViolationData createInstance(final String recipe, final JSONObject object) {
    String file = object.getString("file");
    long line = object.getLong("line");
    long column = object.getLong("column");
    String rule = object.getString("rule");
    String message = object.getString("message");
    String description = object.getString("description");
    String severity = object.getString("severity");
    String level = object.getString("level");
    String tool = object.getString("tool");
    switch (level.toLowerCase()) {
      case "major":
        return new MajorCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      case "minor":
        return new MinorCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      case "info":
        return new InfoCodeViolationData(recipe, file, line, column, rule, message, description,
            severity, tool);
      default:
        throw new JSONException("Unknown level value: " + level);
    }
  }
}
