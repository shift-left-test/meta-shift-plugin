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
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SummaryStatistics;
import com.lge.plugins.metashift.ui.models.StatisticsItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Complexity detail view action class.
 */
public class RecipeComplexityAction
    extends RecipeActionChild {

  static final String STORE_KEY_COMPLEXITYLIST = "ComplexityList";

  private final long complexityTolerance;

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeComplexityAction(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale,
      TaskListener listener, Recipe recipe) {
    super(parent, channel, metadata, name, url, percentScale);

    List<ComplexityData> complexityDataList =
        recipe.objects(ComplexityData.class).collect(Collectors.toList());

    HashMap<String, List<ComplexityData>> fileComplexityList = new HashMap<>();

    for (ComplexityData complexityData : complexityDataList) {
      String file = complexityData.getFile();

      if (!fileComplexityList.containsKey(file)) {
        fileComplexityList.put(file, new ArrayList<>());
      }

      fileComplexityList.get(file).add(complexityData);
    }

    JSONArray fileComplexityArray = new JSONArray();
    complexityTolerance =
        this.getParentAction().getParentAction().getConfiguration().getComplexityTolerance();

    fileComplexityList.forEach((file, complexityList) -> {
      JSONObject fileComplexity = new JSONObject();
      fileComplexity.put("file", file);
      fileComplexity.put("functions", complexityList.size());
      fileComplexity.put("complexFunctions",
          complexityList.stream().filter(o -> o.getValue() >= complexityTolerance).count());
      fileComplexityArray.add(fileComplexity);

      try {
        this.saveFileContents(file);
        this.getDataSource().put(complexityList,
            this.getParentAction().getName(), file, STORE_KEY_COMPLEXITYLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      this.setTableModelJson(fileComplexityArray);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public SummaryStatistics getMetricStatistics() {
    return this.getParentAction().getMetricStatistics()
        .getComplexity();
  }

  @Override
  public Evaluator<?> getEvaluator() {
    return this.getParentAction().getMetrics().getComplexity();
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getComplexity();

    StatisticsItemList stats = new StatisticsItemList();
    stats.addItem("Complex", "valid-bad",
        (int) (evaluator.getRatio() * 100),
        (int) evaluator.getNumerator());
    stats.addItem("Normal", "invalid",
        (int) ((1 - evaluator.getRatio()) * 100),
        (int) (evaluator.getDenominator() - evaluator.getNumerator()));

    return stats.toJsonArray();
  }

  /**
   * return file complexity detail.
   */
  @JavaScriptMethod
  public JSONObject getFileComplexityDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<ComplexityData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_COMPLEXITYLIST);

    result.put("complexityTolerance", complexityTolerance);
    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
