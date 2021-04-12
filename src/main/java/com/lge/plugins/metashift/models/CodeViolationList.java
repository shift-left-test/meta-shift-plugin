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
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a set of CodeViolationData objects.
 *
 * @author Sung Gon Kim
 */
public final class CodeViolationList extends DataList<CodeViolationData> {

  /**
   * Create a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @return a list of objects
   * @throws IllegalArgumentException if failed to parse report files
   */
  public static CodeViolationList create(File path) throws IllegalArgumentException {
    CodeViolationList list = new CodeViolationList();
    String recipe = path.getName();
    File report = FileUtils.getFile(path, "checkcode", "sage_report.json");
    try {
      InputStream is = new BufferedInputStream(new FileInputStream(report));
      JSONObject json = JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
      for (Object o : json.getJSONArray("violations")) {
        list.add(list.createInstance(recipe, (JSONObject) o));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    }
    Collections.sort(list);
    return list;
  }

  /**
   * Creates an instance using the given data.
   *
   * @param recipe name
   * @param object data to parse
   * @return a CodeViolationData object
   */
  private CodeViolationData createInstance(final String recipe, final JSONObject object) {
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

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
