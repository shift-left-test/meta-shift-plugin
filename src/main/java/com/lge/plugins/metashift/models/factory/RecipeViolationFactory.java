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
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import com.lge.plugins.metashift.utils.PathUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * A factory class for the RecipeViolationData objects.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationFactory {

  /**
   * Creates a set of objects by parsing a report file from the given path.
   *
   * @param path to the report directory
   * @throws IOException          if failed to locate report files
   * @throws InterruptedException if an interruption occurs
   */
  public static void create(final FilePath path, final DataList dataList)
      throws IOException, InterruptedException {
    List<RecipeViolationData> objects = new ArrayList<>();
    String recipe = path.getName();
    FilePath report = path.child("checkrecipe").child("recipe_violations.json");
    try {
      JSONObject json = JsonUtils.createObject(report);
      for (Object o : json.getJSONArray("issues")) {
        String file = ((JSONObject) o).getString("file");
        if (PathUtils.isHidden(file)) {
          continue;
        }
        objects.add(createInstance(recipe, (JSONObject) o));
      }
      Collections.sort(objects);
      dataList.addAll(objects);
      dataList.add(RecipeViolationData.class);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to parse: " + report, e);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Create a RecipeViolationData object.
   *
   * @param recipe name
   * @param object to parse
   * @return RecipeViolationData object
   */
  private static RecipeViolationData createInstance(final String recipe, final JSONObject object) {
    String file = object.getString("file");
    long line = object.getLong("line");
    String rule = object.getString("rule");
    String description = object.getString("description");
    String severity = object.getString("severity");
    switch (severity.toLowerCase()) {
      case "error":
        return new MajorRecipeViolationData(recipe, file, line, rule, description, severity);
      case "warning":
        return new MinorRecipeViolationData(recipe, file, line, rule, description, severity);
      case "info":
        return new InfoRecipeViolationData(recipe, file, line, rule, description, severity);
      default:
        throw new JSONException("Unknown severity value: " + severity);
    }
  }
}
