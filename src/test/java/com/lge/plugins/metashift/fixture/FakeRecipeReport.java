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
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * FakeRecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipeReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeRecipeReport(FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private JSONObject createMetadataObject(FakeRecipe recipe) {
    List<JSONObject> array = new ArrayList<>();
    recipe.getScripts().forEach(script -> {
      JSONObject o = new JSONObject();
      File file = FileUtils
          .getFile(recipe.getSourcePath(), recipe.getRecipe(), script.getFilename());
      o.put("file", file.getAbsolutePath());
      o.put("code_lines", script.getLines());
      array.add(o);
    });
    JSONObject metadata = new JSONObject();
    metadata.put("lines_of_code", array);
    return metadata;
  }

  private JSONObject createIssueObject(File path, String severity, long lines) {
    JSONObject object = new JSONObject();
    object.put("file", path.getAbsolutePath());
    object.put("line", FakeRandom.nextNumber());
    object.put("severity", severity);
    object.put("rule", FakeRandom.nextString());
    object.put("description", FakeRandom.nextString());
    return object;
  }

  private JSONObject createReportObject(FakeRecipe recipe) {
    List<JSONObject> array = new ArrayList<>();
    for (FakeScript script : recipe.getScripts()) {
      JSONObject o = new JSONObject();
      for (long i = 0; i < script.getMajorIssues(); i++) {
        array.add(createIssueObject(script.getFile(), "error", script.getLines()));
      }
      for (long i = 0; i < script.getMinorIssues(); i++) {
        array.add(createIssueObject(script.getFile(), "warning", script.getLines()));
      }
      for (long i = 0; i < script.getInfoIssues(); i++) {
        array.add(createIssueObject(script.getFile(), "info", script.getLines()));
      }
    }
    JSONObject report = new JSONObject();
    report.put("issues", array);
    return report;
  }

  private File getPathOf(File directory, String filename) {
    return FileUtils.getFile(directory, recipe.getRecipe(), "checkrecipe", filename);
  }

  @Override
  public void toFile(File directory) throws IOException {
    JsonUtils.saveAs(createMetadataObject(recipe), getPathOf(directory, "files.json"));
    JsonUtils.saveAs(createReportObject(recipe), getPathOf(directory, "recipe_violations.json"));
  }
}
