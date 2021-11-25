/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
          .getFile(recipe.getSourcePath(), script.getFilename());
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
    return FileUtils.getFile(directory, recipe.getName(), "checkrecipe", filename);
  }

  @Override
  public void toFile(File directory) throws IOException {
    JsonUtils.saveAs(createMetadataObject(recipe), getPathOf(directory, "files.json"));
    JsonUtils.saveAs(createReportObject(recipe), getPathOf(directory, "recipe_violations.json"));
  }
}
