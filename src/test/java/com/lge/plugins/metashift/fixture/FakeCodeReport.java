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

package com.lge.plugins.metashift.fixture;

import com.lge.plugins.metashift.utils.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * FakeCodeReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCodeReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeCodeReport(FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private JSONObject createComplexityObject(FakeSource source, int level) {
    JSONObject object = new JSONObject();
    object.put("file", source.getFile().getAbsolutePath());
    object.put("function", FakeRandom.nextString());
    object.put("start", FakeRandom.nextNumber());
    object.put("end", FakeRandom.nextNumber());
    object.put("value", level);
    return object;
  }

  private JSONArray createComplexityList() {
    JSONArray array = new JSONArray();
    for (FakeSource source : recipe.getSources()) {
      for (long i = 0; i < source.getComplexityExceeded(); i++) {
        array.add(createComplexityObject(source, source.getComplexityTolerance()));
      }
      for (long i = 0; i < source.getComplexityNormal(); i++) {
        array.add(createComplexityObject(source, 0));
      }
    }
    return array;
  }

  private JSONArray createDuplicationList() {
    return new JSONArray();
  }

  private JSONArray createSizeList() {
    JSONArray array = new JSONArray();
    for (FakeSource source : recipe.getSources()) {
      JSONObject object = new JSONObject();
      object.put("file", source.getFile().getAbsolutePath());
      object.put("total_lines", source.getTotalLines());
      object.put("code_lines", source.getCodeLines());
      object.put("comment_lines", source.getCommentLines());
      object.put("duplicated_lines", source.getDuplicatedLines());
      object.put("classes", 0);
      object.put("functions", 0);
      array.add(object);
    }
    return array;
  }

  private JSONObject createViolationObject(FakeSource source, String level) {
    JSONObject object = new JSONObject();
    object.put("file", source.getFile().getAbsolutePath());
    object.put("tool", FakeRandom.nextString());
    object.put("rule", FakeRandom.nextString());
    object.put("level", level);
    object.put("severity", 1);
    object.put("message", FakeRandom.nextString());
    object.put("description", FakeRandom.nextString());
    object.put("line", FakeRandom.nextNumber());
    object.put("column", FakeRandom.nextNumber());
    return object;
  }

  private JSONArray createViolationList() {
    JSONArray array = new JSONArray();
    for (FakeSource source : recipe.getSources()) {
      for (long i = 0; i < source.getMajorViolations(); i++) {
        array.add(createViolationObject(source, "major"));
      }
      for (long i = 0; i < source.getMinorViolations(); i++) {
        array.add(createViolationObject(source, "minor"));
      }
      for (long i = 0; i < source.getInfoViolations(); i++) {
        array.add(createViolationObject(source, "info"));
      }
    }
    return array;
  }

  @Override
  public void toFile(File directory) throws IOException {
    List<String> lines = new ArrayList<>();
    File file = FileUtils.getFile(directory, recipe.getName(), "checkcode", "sage_report.json");
    JSONObject object = new JSONObject();
    object.put("version", "0.4.0");
    object.put("complexity", createComplexityList());
    object.put("duplications", createDuplicationList());
    object.put("size", createSizeList());
    object.put("violations", createViolationList());
    JsonUtils.saveAs(object, file);
  }
}
