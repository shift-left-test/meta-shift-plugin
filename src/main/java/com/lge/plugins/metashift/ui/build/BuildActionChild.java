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

package com.lge.plugins.metashift.ui.build;

import com.lge.plugins.metashift.builders.ProjectGroup;
import com.lge.plugins.metashift.ui.ActionChildBase;
import com.lge.plugins.metashift.ui.ActionParentBase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * The main post build action child class.
 */
public class BuildActionChild extends ActionChildBase {

  private final ProjectGroup projectGroup;

  /**
   * constructor.
   *
   * @param parent parent action.
   */
  public BuildActionChild(ActionParentBase parent, ProjectGroup projectGroup,
      String name, String url, boolean percentScale) {
    super(parent, name, url, percentScale);

    this.projectGroup = projectGroup;
  }

  public final ProjectGroup getGroup() {
    return this.projectGroup;
  }

  @Override
  public boolean isAvailable() {
    return this.getGroup().getEvaluation().getBoolean("available");
  }

  /**
   * return threshold string.
   *
   * @return threshold string
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
   * return recipe treemap chart model.
   */
  @JavaScriptMethod
  public JSONObject getRecipesTreemapModel() {
    JSONObject model = new JSONObject();
    model.put("data", this.getGroup().getTreemap());
    model.put("tooltipInfo", this.getGroup().getSummaries());

    return model;
  }

  /**
   * return recipes list.
   *
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONArray getRecipesTableModel() throws InterruptedException {
    return this.getGroup().getSummaries();
  }
}
