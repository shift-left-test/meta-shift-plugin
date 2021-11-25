/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.builders.RecipeGroup;
import com.lge.plugins.metashift.ui.ActionChildBase;
import com.lge.plugins.metashift.ui.ActionParentBase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Detail view common feature class.
 */
public class RecipeActionChild extends ActionChildBase {

  private final RecipeGroup recipeGroup;

  /**
   * constructor.
   *
   * @param parent parent action
   */
  public RecipeActionChild(ActionParentBase parent, RecipeGroup recipeGroup,
      String name, String url, boolean percentScale) {
    super(parent, name, url, percentScale);

    this.recipeGroup = recipeGroup;
  }

  public final RecipeGroup getGroup() {
    return this.recipeGroup;
  }

  /**
   * return evaluation available.
   */
  public boolean isAvailable() {
    return this.getGroup().getEvaluation().getBoolean("available");
  }

  /**
   * return threshold string.
   */
  public final String getThresholdString() {
    return this.getFormattedValue(this.getGroup().getEvaluation().getDouble("threshold"));
  }

  /**
   * return scale.
   *
   * @return string
   */
  public String getScale() {
    JSONObject evaluator = this.getGroup().getEvaluation();
    if (evaluator.getBoolean("available")) {
      return this.getFormattedValue(evaluator.getDouble("ratio"));
    } else {
      return "N/A";
    }
  }

  @JavaScriptMethod
  public JSONArray getTableModel() throws InterruptedException {
    return this.getGroup().getSummaries();
  }

  /**
   * return file detail model.
   */
  @JavaScriptMethod
  public JSONObject getFileDetailModel(String codePath) throws InterruptedException {
    JSONObject result = new JSONObject();

    result.put("dataList", this.getGroup().getObjects(codePath));
    result.put("content", this.getGroup().readFile(codePath));

    return result;
  }
}
