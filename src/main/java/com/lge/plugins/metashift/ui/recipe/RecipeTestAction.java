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
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SummaryStatistics;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.ui.models.StatisticsItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Test detail view action class.
 */
public class RecipeTestAction extends RecipeActionChild {

  // statistics data
  private final long passedCount;
  private final long failedCount;
  private final long errorCount;
  private final long skippedCount;

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeTestAction(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale,
      TaskListener listener, Recipe recipe) {
    super(parent, channel, metadata, name, url, percentScale);

    List<TestData> testList = recipe.objects(TestData.class).collect(Collectors.toList());

    JSONArray testArray = JSONArray.fromObject(testList);

    passedCount = testList.stream()
        .filter(o -> o.getStatus().equals("PASSED")).count();
    failedCount = testList.stream()
        .filter(o -> o.getStatus().equals("FAILED")).count();
    errorCount = testList.stream()
        .filter(o -> o.getStatus().equals("ERROR")).count();
    skippedCount = testList.stream()
        .filter(o -> o.getStatus().equals("SKIPPED")).count();

    try {
      this.setTableModelJson(testArray);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public SummaryStatistics getMetricStatistics() {
    return this.getParentAction().getMetricStatistics()
        .getTest();
  }

  @Override
  public Evaluator<?> getEvaluator() {
    return this.getParentAction().getMetrics().getTest();
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = passedCount + failedCount + errorCount + skippedCount;

    StatisticsItemList stats = new StatisticsItemList();
    stats.addItem("Passed", "valid-good",
        allCount > 0 ? passedCount * 100 / allCount : 0,
        passedCount);
    stats.addItem("Failed", "valid-bad",
        allCount > 0 ? failedCount * 100 / allCount : 0,
        failedCount);
    stats.addItem("Error", "valid-error",
        allCount > 0 ? errorCount * 100 / allCount : 0,
        errorCount);
    stats.addItem("Skipped", "invalid",
        allCount > 0 ? skippedCount * 100 / allCount : 0,
        skippedCount);

    return stats.toJsonArray();
  }
}
