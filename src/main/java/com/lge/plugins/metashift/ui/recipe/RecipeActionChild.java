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
    if (this.percentScale) {
      return String.format("%d%%",
          (int) (this.getGroup().getEvaluation().getDouble("threshold") * 100));
    } else {
      return String.format("%.2f",
          this.getGroup().getEvaluation().getDouble("threshold"));
    }
  }

  /**
   * return scale.
   *
   * @return string
   */
  public String getScale() {
    JSONObject evaluator = this.getGroup().getEvaluation();
    if (evaluator.getBoolean("available")) {
      if (this.percentScale) {
        return String.format("%d%%", (long) (evaluator.getDouble("ratio") * 100));
      } else {
        return String.format("%.2f", evaluator.getDouble("ratio"));
      }
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
