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

package com.lge.plugins.metashift.parsers;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsers class for the RecipeViolationData objects.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationParser extends FileParser {

  private final FilePath path;
  private final DataList dataList;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public RecipeViolationParser(FilePath path, DataList dataList) {
    this.path = path;
    this.dataList = dataList;
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    FilePath report = path.child("checkrecipe").child("recipe_violations.json");
    try {
      Any json = JsonUtils.createObject2(report);
      List<Any> array = json.get("issues").asList();
      List<RecipeViolationData> objects = new ArrayList<>(array.size());

      for (Any o : array) {
        String file = o.toString("file");
        if (isHidden(file)) {
          continue;
        }
        objects.add(createInstance(path.getName(), o));
      }
      dataList.addAll(objects);
      dataList.add(RecipeViolationData.class);
    } catch (JsonException e) {
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
  private static RecipeViolationData createInstance(final String recipe, final Any object) {
    String file = object.toString("file");
    long line = object.toLong("line");
    String rule = object.toString("rule");
    String description = object.toString("description");
    String severity = object.toString("severity");
    switch (severity.toLowerCase()) {
      case "error":
        return new MajorRecipeViolationData(recipe, file, line, rule, description, severity);
      case "warning":
        return new MinorRecipeViolationData(recipe, file, line, rule, description, severity);
      case "info":
        return new InfoRecipeViolationData(recipe, file, line, rule, description, severity);
      default:
        throw new JsonException("Unknown severity value: " + severity);
    }
  }
}
