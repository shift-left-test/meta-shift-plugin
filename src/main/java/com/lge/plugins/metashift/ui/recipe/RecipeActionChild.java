/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.builders.RecipeGroup;
import com.lge.plugins.metashift.ui.ActionChildBase;
import com.lge.plugins.metashift.ui.ActionParentBase;
import com.lge.plugins.metashift.ui.tables.SummaryTableModel;
import com.lge.plugins.metashift.ui.tables.SummaryTableSpec;
import com.lge.plugins.metashift.ui.tables.TestListTableModel;
import io.jenkins.plugins.datatables.TableModel;
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

  @Override
  public TableModel getTableModel(String id) {
    if ("unit_tests".equals(id)) {
      return new TestListTableModel(id, getGroup().getSummaries());
    }
    return new SummaryTableModel(SummaryTableSpec.forRecipeFile(id), getGroup().getSummaries());
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
