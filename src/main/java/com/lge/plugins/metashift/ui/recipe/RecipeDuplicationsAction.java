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

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.StatisticsItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's duplication detail view action class.
 */
public class RecipeDuplicationsAction
    extends RecipeActionChild {

  static final String STORE_KEY_DUPLICATIONLIST = "DuplicationList";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeDuplicationsAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    JSONArray duplicationList = JSONArray.fromObject(
        recipe.objects(DuplicationData.class).toArray());

    try {
      dataSource.put(duplicationList, this.getParentAction().getName(), STORE_KEY_DUPLICATIONLIST);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return "Duplications";
  }

  @Override
  public String getUrlName() {
    return "duplications";
  }

  @Override
  public String getScale() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getDuplications();
    if (evaluator.isAvailable()) {
      return String.format("%d%%", (long) (evaluator.getRatio() * 100));
    } else {
      return "N/A";
    }
  }

  @Override
  public JSONObject getMetricStatistics() {
    JSONObject result = this.getParentAction().getMetricStatistics()
        .getDuplications().toJsonObject();

    Evaluator<?> evaluator = this.getParentAction().getMetrics().getDuplications();

    result.put("scale", evaluator.getRatio());
    result.put("available", evaluator.isAvailable());
    result.put("percent", true);

    return result;
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getDuplications();

    StatisticsItemList stats = new StatisticsItemList();
    stats.addItem("Duplicated", "valid-bad",
        (int) (evaluator.getRatio() * 100),
        (int) evaluator.getNumerator());
    stats.addItem("Code", "invalid",
        (int) ((1 - evaluator.getRatio()) * 100),
        (int) (evaluator.getDenominator() - evaluator.getNumerator()));

    return stats.toJsonArray();
  }

  /**
   * return paginated duplication list.
   *
   * @return duplication list
   */
  @JavaScriptMethod
  public JSONArray getRecipeFiles() {
    return this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_DUPLICATIONLIST);
  }
}
